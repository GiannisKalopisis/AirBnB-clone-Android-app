package com.example.fakebnb.Callbacks;

import android.graphics.Bitmap;

public interface ImageLoadCallback {
    void onImageLoaded(Bitmap userImageBitmap);
    void onError(String errorMessage);
}
