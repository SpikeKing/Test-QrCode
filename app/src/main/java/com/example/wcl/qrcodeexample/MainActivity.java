package com.example.wcl.qrcodeexample;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.wcl.qrcodeexample.ExtraZxing.ColoredQRCode;
import com.example.wcl.qrcodeexample.ExtraZxing.ImageOverlay;
import com.example.wcl.qrcodeexample.ExtraZxing.QRCodeBuilder;
import com.example.wcl.qrcodeexample.ExtraZxing.ZXingQRCodeBuilder;

/**
 * 生成二维码示例：
 * 输入字符串生成二维码，中间可以添加Logo(任何图片均可)；
 * 点击二维码可以判断输出字符串，生成二维码时就包含判断；
 * <p/>
 * created by C.L.Wang
 */
public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    private ImageButton mImageButton;
    private Bitmap mQrcode;
    private Button mButton;
    private EditText mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButton = (Button) findViewById(R.id.generate_button);
        mImageButton = (ImageButton) findViewById(R.id.code_image);
        mEditText = (EditText) findViewById(R.id.code_content);

        if (mEditText.getText().toString() == null)
            mButton.setEnabled(false);

        /*编辑内容，控制按钮点击*/
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s == null || s.length() == 0) {
                    mButton.setEnabled(false);
                } else {
                    mButton.setEnabled(true);
                }
            }
        });

        // 生成二维码，中间带Logo图像
        mQrcode = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contents = "";
                try {
                    contents = mEditText.getText().toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                QRCodeBuilder qrcodeBuilder = new ZXingQRCodeBuilder();

                Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.logo);

                /**
                 * 图像大小500*500，背景黑色，不透明(1)，比例(0.25f)
                 */
                mQrcode = qrcodeBuilder.withSize(500, 500)
                        .withData(contents)
                        .decorate(ColoredQRCode.colorizeQRCode(Color.BLACK))
                        .decorate(ImageOverlay.addImageOverlay(logo, 1f, 0.25f))
                        .toQRCode();

                // 如果颜色和Logo导致二维码出现问题，则会导致生成图像为null
                if (mQrcode != null) {
                    mImageButton.setImageBitmap(mQrcode);
                } else {
                    Toast.makeText(getApplicationContext(), "QrCode无法解析", Toast.LENGTH_LONG).show();
                }
            }
        });

        // 解析二维码输出二维码内容
        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String src = ZXingQRCodeBuilder.readQRCode(mQrcode);

                Log.i("DEBUG: " + TAG, "String: " + src);
                if (src != null)
                    Toast.makeText(getApplicationContext(), src, Toast.LENGTH_LONG).show();
            }
        });

    }
}
