package com.example.fakebnb.utils;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ImageUtils {

    public static MultipartBody.Part getImagePart(Bitmap imageBitmap, String imagePath) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream);
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), outputStream.toByteArray());
        return MultipartBody.Part.createFormData("file", imagePath, requestBody);
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
