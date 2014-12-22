package com.example.wcl.qrcodeexample.ExtraZxing;

import android.graphics.Bitmap;

/**
 * created by C.L.Wang
 * <p/>
 * 生成二维码的父类，制定需要使用的接口
 */
public interface QRCodeBuilder {

    /**
     * 添加字符串
     *
     * @param data 字符串的值
     * @return 装饰者模式的主对象
     */
    public QRCodeBuilder withData(String data);

    /**
     * 添加长宽
     *
     * @param width  宽
     * @param height 高
     * @return 装饰者模式的主对象
     */
    public QRCodeBuilder withSize(int width, int height);

    /**
     * 装饰对象
     *
     * @param decorator 装饰者
     * @return 装饰者模式的主对象
     */
    public QRCodeBuilder decorate(QRCodeDecorator decorator);

    /**
     * 生成二维码图像
     *
     * @return 图像
     */
    public Bitmap toQRCode();

}
