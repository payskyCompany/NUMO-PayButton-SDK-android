package io.paysky.paybutton.util;

import android.graphics.Bitmap;

public interface QrBitmapLoadListener {

    void onLoadBitmapQrSuccess(Bitmap bitmap);

    void onLoadBitmapQrFailed();
}
