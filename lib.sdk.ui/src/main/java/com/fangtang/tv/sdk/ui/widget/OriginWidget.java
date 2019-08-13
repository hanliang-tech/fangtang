package com.fangtang.tv.sdk.ui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.support.annotation.Keep;

import com.fangtang.tv.support.glide.BFImgDisplay;
import com.fangtang.tv.support.glide.LoadImageBuilder;
import com.fangtang.tv.support.item2.utils.DimensUtil;
import com.fangtang.tv.support.item2.widget.BuilderWidget;

@Keep
public class OriginWidget extends BuilderWidget<OriginWidget.Builder> implements IOriginWidget {

//    private int titleBottomHeight = DimensUtil.dp2Px(40);
    private int titleBottomHeight = 0;
    private int apkHeight = DimensUtil.dp2Px(47);
    private int y = 0;
    private int textX = DimensUtil.dp2Px(39);
    private int textY;
    private int logoX = DimensUtil.dp2Px(9);
    private int logoWidth = DimensUtil.dp2Px(23);
    private Rect rectBackground = new Rect();
    private Rect rectLogo = new Rect();
    private LinearGradient linearGradient;
    private int parentWidth;
    private int parentHeight;

    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private String apkName;
    private String apkUrl;
    private Bitmap bitmap;

    public OriginWidget(Builder builder) {
        super(builder);
        parentWidth = builder.parentWidth;
        parentHeight = builder.parentHeight;
        initView();
    }


    private void initView() {
        setSize(MATCH_PARENT, apkHeight);
        y = parentHeight - titleBottomHeight - apkHeight;
        textY = y + apkHeight - DimensUtil.dp2Px(11);
        rectBackground.top = y;
        rectBackground.bottom = y + apkHeight;
        rectBackground.left = 0;
        rectBackground.right = parentWidth;

        rectLogo.left = logoX;
        rectLogo.top = y + DimensUtil.dp2Px(19);
        rectLogo.right = rectLogo.left + logoWidth;
        rectLogo.bottom = rectLogo.top + logoWidth;

        linearGradient = new LinearGradient(0, rectBackground.top, 1, rectBackground.bottom, Color.parseColor("#00373538"), Color.parseColor("#ff373538"), Shader.TileMode.CLAMP);

    }

    @Override
    public void onFocusChange(boolean gainFocus) {
        super.onFocusChange(gainFocus);
        if (gainFocus) {
            this.setVisible(true, false);
        } else {
            this.setVisible(false, false);
        }
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void setApkUrl(int parentWidth, int parentHeight, String apkUrl) {
        if (this.parentWidth != parentWidth) {
            this.parentWidth = parentWidth;
            this.parentHeight = parentHeight;
            initView();
        }
        setApkUrl(apkUrl);
    }

    @Override
    public void setApkName(int parentWidth, int parentHeight, String apkName) {
        if (this.parentWidth != parentWidth) {
            this.parentWidth = parentWidth;
            this.parentHeight = parentHeight;
            initView();
        }
        setApkName(apkName);
    }

    public void setApkName(String apkName) {
        this.apkName = apkName;
        invalidateSelf();
    }

    public void setApkUrl(String apkUrl) {
        bitmap = null;
        this.apkUrl = apkUrl;
        BFImgDisplay.displayBitmap(new LoadImageBuilder(getContext())
                        .setUri(apkUrl)
                        .setPreferSize(logoWidth, logoWidth)
                        .setShape(LoadImageBuilder.ImageShape.Round)
                , resource -> bitmap = resource);
    }

    public void clearBitmap() {
        bitmap = null;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(isVisible()){
            paint.setShader(linearGradient);
            canvas.drawRect(rectBackground, paint);
            paint.setShader(null);
            paint.setTextSize(DimensUtil.dp2Px(13.3f));
            paint.setColor(Color.parseColor("#ffffff"));
            if (apkName != null && y > 0) {
                canvas.drawText(apkName, textX, textY, paint);
            }
            if (bitmap != null && y > 0) {
                canvas.drawBitmap(bitmap, null, rectLogo, paint);
            }
        }
    }

    @Keep
    public static class Builder extends BuilderWidget.Builder<OriginWidget> {

        public int parentHeight;
        public int parentWidth;

        @Keep
        public Builder(Context context) {
            super(context);
        }

        public Builder setWidth(int parentWidth) {
            this.parentWidth = parentWidth;

            return this;
        }

        public Builder setHeight(int parentHeight) {
            this.parentHeight = parentHeight;
            return this;
        }

        @Override
        public OriginWidget build() {
            return new OriginWidget(this);
        }
    }
}