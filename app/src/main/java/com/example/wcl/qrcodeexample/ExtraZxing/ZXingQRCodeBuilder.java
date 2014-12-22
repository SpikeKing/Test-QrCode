package com.example.wcl.qrcodeexample.ExtraZxing;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

/**
 * created by C.L.Wang
 * <p/>
 * ZXing二维码生成器的辅助功能添加；
 * 功能：
 * 改变二维码颜色 - 颜色越浅识别率越差；
 * 添加Logo图片 - 图片越大越清晰识别率越差；
 */
public class ZXingQRCodeBuilder implements QRCodeBuilder {

    private String mData; //存储数据
    private int mWidth, mHeight; //二维码长宽
    private List<QRCodeDecorator> mDecorators; //装饰者数量

    /**
     * 添加需要生成二维码的字符串
     *
     * @param data 字符串
     * @return QRCodeBuilder 装饰器模式，继续添加装饰
     */
    @Override
    public QRCodeBuilder withData(String data) {
        this.mData = data;
        return this;
    }

    /**
     * 添加需要生成二维码的宽和长
     *
     * @param width  宽度
     * @param height 高度
     * @return QRCodeBuilder 装饰器模式，继续添加装饰
     */
    @Override
    public QRCodeBuilder withSize(int width, int height) {
        this.mWidth = width;
        this.mHeight = height;
        return this;
    }

    /**
     * 需要添加的各种装饰器
     *
     * @param decorator 装饰器
     * @return QRCodeBuilder 装饰器模式，继续添加装饰
     */
    @Override
    public QRCodeBuilder decorate(QRCodeDecorator decorator) {
        if (mDecorators == null) {
            mDecorators = new LinkedList<QRCodeDecorator>();
        }
        mDecorators.add(decorator);
        return this;
    }

    /**
     * 最终返回二维码的图片
     *
     * @return Bitmap 二维码图片
     */
    @Override
    public Bitmap toQRCode() {
        Bitmap qrcode = encode();
        qrcode = decorate(qrcode);

        if (!mData.equals(readQRCode(qrcode))) {
            return null;
        }

        return qrcode;
    }

//--------------------
// private methods
//--------------------

    /**
     * 生成二维码图片
     * 容错级别最高30%，二维码边界最小1，UTF-8可以处理汉字；
     *
     * @return Bitmap 二维码图片
     */
    private Bitmap encode() {
        try {
            Hashtable hints = new Hashtable();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H); //容错最高级别
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8"); //处理汉字
            hints.put(EncodeHintType.MARGIN, 1); //最小边界

            BitMatrix matrix = new MultiFormatWriter().encode(
                    mData, BarcodeFormat.QR_CODE, mWidth, mHeight, hints);
            return bitMatrix2Bitmap(matrix);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 把bit矩阵转换为Bitmap图像
     * 默认背景是白色，字体是黑色；
     * 背景必须使用浅色，字体使用深色；
     *
     * @param matrix 二维码bit矩阵
     * @return Bitmap 最终图像
     */
    private Bitmap bitMatrix2Bitmap(BitMatrix matrix) {
        int w = matrix.getWidth();
        int h = matrix.getHeight();
        int[] rawData = new int[w * h];
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                int color = Color.WHITE;
                if (matrix.get(i, j)) {
                    color = Color.BLACK;
                }
                rawData[i + (j * w)] = color;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
        bitmap.setPixels(rawData, 0, w, 0, 0, w, h);
        return bitmap;
    }

    /**
     * 把图像根据添加的装饰者数量依次添加装饰
     *
     * @param qrcode 输入图像
     * @return Bitmap 添加装饰者后的图像
     */
    private Bitmap decorate(Bitmap qrcode) {
        if (mDecorators != null) {
            for (QRCodeDecorator decorator : mDecorators) {
                qrcode = decorator.decorate(qrcode);
            }
        }
        return qrcode;
    }

//--------------------
// public static methods
//--------------------

    /**
     * 读取二维码图像，使用UTF-8格式读取
     *
     * @param qrcode 二维码图像
     * @return String 二维码代表的字符串
     */
    public static String readQRCode(Bitmap qrcode) {

        String contents = null;

        int[] intArray = new int[qrcode.getWidth() * qrcode.getHeight()];
        qrcode.getPixels(intArray, 0, qrcode.getWidth(), 0, 0, qrcode.getWidth(), qrcode.getHeight());

        LuminanceSource source = new RGBLuminanceSource(qrcode.getWidth(), qrcode.getHeight(), intArray);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        try {
            Hashtable hints = new Hashtable();
            hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
            Result result = new MultiFormatReader().decode(bitmap, hints);
            contents = result.getText();
        } catch (NotFoundException e) {
            e.printStackTrace();
        }

        return contents;
    }
}
