package com.example.fakebnb.Callbacks;

import android.graphics.Bitmap;

public interface ApartmentImageLoadCallback {
    void onImageLoaded(Bitmap userImageBitmap);
    void onError(String errorMessage);
}
