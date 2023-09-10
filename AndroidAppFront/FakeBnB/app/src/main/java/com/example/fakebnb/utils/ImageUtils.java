package com.example.fakebnb.utils;

import android.graphics.Bitmap;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ImageUtils {

    public static MultipartBody.Part getImagePart(Bitmap imageBitmap, String imagePath) {
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        imageBitmap.compress(Bitmap.CompressFormat.PNG, 70, outputStream);
//        byte[] byteArray = outputStream.toByteArray();
//        String base64Image = Base64.encodeToString(byteArray, Base64.DEFAULT);
//        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), base64Image);
//        return MultipartBody.Part.createFormData("file", imagePath, requestBody);

        // Convert your bitmap to a byte array
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();

        // Create a MultipartBody.Part for the image
        return MultipartBody.Part.createFormData("image", "test_image.png", RequestBody.create(MediaType.parse("image/*"), imageBytes));
    }

    public static List<MultipartBody.Part> getImageParts(List<Bitmap> bitmapList, List<String> imagePaths) {
        List<MultipartBody.Part> imageParts = new ArrayList<>();

        for (int i = 0; i < bitmapList.size(); i++) {
            Bitmap bitmap = bitmapList.get(i);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream);
            RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), outputStream.toByteArray());
            MultipartBody.Part imagePart = MultipartBody.Part.createFormData("images", imagePaths.get(i), requestBody);
            imageParts.add(imagePart);
        }

        return imageParts;
    }
}
