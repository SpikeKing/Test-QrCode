package com.example.wcl.qrcodeexample.ExtraZxing;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created on C.L.Wang
 * <p/>
 * 在二维码的中心添加Logo图标，和设置Logo图标的透明度
 */
public class ImageOverlay implements QRCodeDecorator {

    private static final float DEFAULT_OVERLAY_TRANSPARENCY = 1f; //透明度，默认不透明
    private static final float DEFAULT_OVERLAY_TO_QRCODE_RATIO = 0.25f; //图像比例，默认0.25

    private Bitmap mOverlay; //Logo图片
    private float mOverlayToQRCodeRatio, mOverlayTransparency; //透明度和图像比例

    /**
     * 添加Logo图片，可以选择透明度和比例
     *
     * @param overlay              Logo图片
     * @param overlayTransparency  透明度
     * @param overlayToQRCodeRatio 缩放比例
     */
    private ImageOverlay(Bitmap overlay, float overlayTransparency, float overlayToQRCodeRatio) {
        if (overlay == null)
            throw new IllegalArgumentException("Overlay is required");

        this.mOverlay = overlay;
        this.mOverlayTransparency = overlayTransparency;
        this.mOverlayToQRCodeRatio = overlayToQRCodeRatio;
    }

    private ImageOverlay(Bitmap overlay, float overlayTransparency) {
        this(overlay, overlayTransparency, DEFAULT_OVERLAY_TO_QRCODE_RATIO);
    }

    private ImageOverlay(Bitmap overlay) {
        this(overlay, DEFAULT_OVERLAY_TRANSPARENCY, DEFAULT_OVERLAY_TO_QRCODE_RATIO);
    }

    /**
     * 为二维码连续添加装饰
     *
     * @param overlay              Logo图片
     * @param overlayTransparency  透明度
     * @param overlayToQRCodeRatio 缩放比例
     * @return QRCodeDecorator 装饰器基类
     */
    public static QRCodeDecorator addImageOverlay(Bitmap overlay, Float overlayTransparency, Float overlayToQRCodeRatio) {
        return new ImageOverlay(overlay, overlayTransparency, overlayToQRCodeRatio);
    }

    /**
     * 在二维码中心设置带有透明度的Logo
     *
     * @param qrcode 二维码源图像
     * @return Bitmap 装饰过的二维码图像
     */
    public Bitmap decorate(Bitmap qrcode) {

        Bitmap logo = mOverlay;
        int longedge = mOverlay.getWidth() > mOverlay.getHeight() ?
                mOverlay.getWidth() : mOverlay.getHeight(); //较长边

        float scale = qrcode.getWidth() / longedge; //缩放比例

        /**
         * 按比例缩放
         */
        Bitmap logoScaled = Bitmap.createScaledBitmap(
                logo,
                Math.round(logo.getWidth() * scale * mOverlayToQRCodeRatio),
                Math.round(logo.getHeight() * scale * mOverlayToQRCodeRatio),
                false);

        Bitmap combined = Bitmap.createBitmap(
                qrcode.getWidth(), qrcode.getHeight(), Bitmap.Config.ARGB_8888); //创建纯白背景图像

        Canvas canvas = new Canvas(combined); //创建画布

        canvas.drawBitmap(qrcode, 0, 0, null); //绘制二维码图像

        Paint paint = new Paint();
        paint.setAlpha(Math.round(mOverlayTransparency * 255)); //透明度

        /**
         * Logo在图像的中心
         */
        float left = combined.getWidth() / 2f - logoScaled.getWidth() / 2f;
        float top = combined.getHeight() / 2f - logoScaled.getHeight() / 2f;
        canvas.drawBitmap(logoScaled, left, top, paint); //绘制Logo图像

        return combined;
    }
}
