package com.example.wcl.qrcodeexample.ExtraZxing;

import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * created by C.L.Wang
 * <p/>
 * 颜色转换装饰器：
 * 转换颜色，使用Color类参数构造：如Color.GREEN等；
 * 即把二维码的颜色由黑色转换为其他颜色。
 */
public class ColoredQRCode implements QRCodeDecorator {

    private int color; //传入颜色值

    /**
     * 颜色值前两位是透明度，非透明颜色FF即可，
     * 其余位分别对应RGB颜色
     *
     * @param color 颜色值
     */
    private ColoredQRCode(int color) {
        if (color > 0xFFFFFFFF) //判断颜色
            throw new IllegalArgumentException("Unknown Color Value");
        this.color = color;
    }

    /**
     * 为二维码连续添加装饰
     *
     * @param color 颜色值
     * @return QRCodeDecorator 基类
     */
    public static QRCodeDecorator colorizeQRCode(int color) {
        return new ColoredQRCode(color);
    }

    /**
     * 改变二维码颜色
     *
     * @param qrcode 二维码
     * @return Bitmap 添加装饰之后的二维码图片
     */
    @Override
    public Bitmap decorate(Bitmap qrcode) {

        int width = qrcode.getWidth();
        int height = qrcode.getHeight();

        int[] pixels = new int[width * height];
        qrcode.getPixels(pixels, 0, width, 0, 0, width, height);

        /**
         * 把黑色转换相应的二维码颜色
         */
        for (int i = 0; i < pixels.length; i++) {
            if (pixels[i] == Color.BLACK)
                pixels[i] = color;
        }

        return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888);
    }
}
