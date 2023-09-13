package com.example.fakebnb.Callbacks;

import android.graphics.Bitmap;

public interface SingleRentalImageCallback {
    void onImageLoaded(Bitmap rentalImageBitmap);
    void onError(String errorMessage);
}
