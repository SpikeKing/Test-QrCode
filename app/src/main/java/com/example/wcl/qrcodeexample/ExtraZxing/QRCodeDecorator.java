package com.example.wcl.qrcodeexample.ExtraZxing;

import android.graphics.Bitmap;

/**
 * created by C.L.Wang
 *
 * QrCode的装饰者模式基类，在二维码图像中添加功能。
 */
public interface QRCodeDecorator {
    public Bitmap decorate(Bitmap qrcode);
}
