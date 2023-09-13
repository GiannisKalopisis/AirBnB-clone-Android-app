package com.dit.airbnb.service;

import com.dit.airbnb.dto.Apartment;
import com.dit.airbnb.dto.Image;
import com.dit.airbnb.dto.UserReg;
import com.dit.airbnb.exception.ResourceNotFoundException;
import com.dit.airbnb.exception.StorageException;
import com.dit.airbnb.repository.ApartmentRepository;
import com.dit.airbnb.repository.ImageRepository;
import com.dit.airbnb.repository.UserRegRepository;
import com.dit.airbnb.response.generic.ApiResponse;
import com.dit.airbnb.security.user.CurrentUser;
import com.dit.airbnb.security.user.UserDetailsImpl;
import com.dit.airbnb.storage.StorageProperties;
import jakarta.annotation.PostConstruct;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.core.io.Resource;

import org.springframework.util.FileSystemUtils;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ImageService {

    private final Path rootLocation;

    @Autowired
    private UserRegRepository userRegRepository;

    @Autowired
    private ApartmentRepository apartmentRepository;

    @Autowired
    private ImageRepository imageRepository;

    public static int IMAGE_COUNTER = 1;

    @Autowired
    public ImageService(StorageProperties properties) {
        this.rootLocation = Paths.get(properties.getLocation());
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage location", e);
        }
    }

    public String store(MultipartFile file) {
        String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        if (filename.contains(".png")) {
            filename = filename.split(".png")[0] + (ImageService.IMAGE_COUNTER) + ".png";
        } else {
            filename = filename + (ImageService.IMAGE_COUNTER) + ".png";
        }
        ImageService.IMAGE_COUNTER++;

        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file " + filename);
            }
            if (filename.contains("..")) {
                // This is a security check
                throw new StorageException(
                        "Cannot store file with relative path outside current directory "
                                + filename);
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, this.rootLocation.resolve(filename),
                        StandardCopyOption.REPLACE_EXISTING);
            }
        }
        catch (IOException e) {
            throw new StorageException("Failed to store file " + filename, e);
        }

        return filename;
    }

    private Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    private Resource loadAsResource(String filename) throws FileNotFoundException {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
            else {
                throw new FileNotFoundException(
                        "Could not read file: " + filename);
            }
        }
        catch (MalformedURLException e) {
            throw new FileNotFoundException("Could not read file: " + filename);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Resource loadFileAsResource(String fileName) throws FileNotFoundException {
        try {
            Path filePath = rootLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()) {
                return resource;
            } else {
                throw new FileNotFoundException("File not found " + fileName);
            }
        } catch (MalformedURLException | FileNotFoundException ex) {
            throw new FileNotFoundException("File not found " + fileName);
        }
    }

    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    public List<Long> getUserRegImageResources(Long userId) {
        List<Image> images = imageRepository.findByUserRegId(userId);
        List<Long> imageResources = new ArrayList<>();
        for (Image image : images) {
            imageResources.add(image.getId());
        }
        if (imageResources.isEmpty()) {
            imageResources.add((long) 0);
        }
        return imageResources;
    }

    public ResponseEntity<?> getUserImage(Long userId) throws FileNotFoundException {
        List<Image> images = imageRepository.findByUserRegId(userId);
        if (images == null) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + "emptyImage" + "\"")
                    .body(null);
        }
        Resource resource = loadAsResource(images.get(0).getPath());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    public ResponseEntity<?> getApartmentImageByImageId(@RequestParam Long imageId) throws IOException {
        Optional<Image> images = imageRepository.findById(imageId);
        if (images.isEmpty()) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + "emptyImage" + "\"")
                    .body(null);
        }

        Resource resource = loadAsResource(images.get().getPath());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    public ResponseEntity<?> getApartmentImageIds(Long apartmentId) {
        List<Long> ids = imageRepository.findByApartmentId(apartmentId);
        return ResponseEntity.ok(new ApiResponse(true, "getApartmentImageIds succeed", ids));
    }

    public void deleteImagesByIds(List<Long> ids) throws IOException {
        for (Long imageId: ids) {
            // delete image
            Optional<Image> image = imageRepository.findById(imageId);
            if (image.isEmpty()) continue;
            Image imageDel = image.get();
            imageRepository.delete(imageDel);
            Files.delete(Paths.get("src/main/resources/static/images/" + imageDel.getPath()));
        }
    }

    public ResponseEntity<?> updateUserImage(UserReg userReg, MultipartFile image) throws IOException {

        if (image != null && !image.isEmpty()) {
            // delete image
            List<Image> images = imageRepository.findByUserRegId(userReg.getId());
            Image firstImage = images.get(0);
            imageRepository.delete(firstImage);

            if (!firstImage.getPath().equals(PopulateDBService.IMAGE_DEFAULT_PATH)) Files.delete(Paths.get("src/main/resources/static/images/" + firstImage.getPath()));

            // store the new
            String imageName = store(image);
            Image imageIn = new Image(imageName);
            imageIn.setUserReg(userReg);
            imageRepository.save(imageIn);
        }

        return ResponseEntity.ok().body(new ApiResponse(true, "updateUserImage succeed", userReg));
    }

    public ResponseEntity<?> updateApartmentImages(Apartment apartment, List<MultipartFile> images) {

        // Store image
        for (var image: images) {
            String imageName = store(image);
            Image imageIn = new Image(imageName);
            imageIn.setApartment(apartment);
            imageRepository.save(imageIn);
        }

        return ResponseEntity.ok().body(new ApiResponse(true, "updateApartmentImages succeed", apartment));
    }

    public ResponseEntity<?> getSingleApartmentImage(Long apartmentId) throws FileNotFoundException {

        Optional<Image> image = imageRepository.findFirstByApartmentId(apartmentId);
        if (image.isEmpty()) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + "emptyImage" + "\"")
                    .body(null);
        }

        Resource resource = loadAsResource(image.get().getPath());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
