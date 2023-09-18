package com.example.fakebnb.utils;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ImageUtils {

    public static MultipartBody.Part getImagePart(Bitmap imageBitmap) {
        // Convert your bitmap to a byte array
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();

        // Create a MultipartBody.Part for the image
        return MultipartBody.Part.createFormData("image", "image", RequestBody.create(MediaType.parse("image/*"), imageBytes));
    }

    public static List<MultipartBody.Part> getImageParts(List<Bitmap> bitmapList) {
        List<MultipartBody.Part> imageParts = new ArrayList<>();

        for (int i = 0; i < bitmapList.size(); i++) {
            Bitmap imageBitmap = bitmapList.get(i);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();
            imageParts.add(MultipartBody.Part.createFormData("image", "image", RequestBody.create(MediaType.parse("multipart/form-data"), imageBytes)));
        }

        return imageParts;
    }
}
