package com.wishbook.catalog.Utils.widget;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatButton;
import android.util.AttributeSet;

import com.wishbook.catalog.R;

public class CustomDrawableAppCompactButton extends AppCompatButton {

    public CustomDrawableAppCompactButton(Context context) {
        super(context);
    }

    public CustomDrawableAppCompactButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
    }

    public CustomDrawableAppCompactButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
    }


    void initAttrs(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray attributeArray = context.obtainStyledAttributes(
                    attrs,
                    R.styleable.CustomDrawableButton);

            Drawable drawableLeft = null;
            Drawable drawableRight = null;
            Drawable drawableBottom = null;
            Drawable drawableTop = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                drawableLeft = attributeArray.getDrawable(R.styleable.CustomDrawableButton_drawableLeftButtonCompat);
                drawableRight = attributeArray.getDrawable(R.styleable.CustomDrawableButton_drawableRightButtonCompat);
                drawableBottom = attributeArray.getDrawable(R.styleable.CustomDrawableButton_drawableBottomButtonCompat);
                drawableTop = attributeArray.getDrawable(R.styleable.CustomDrawableButton_drawableTopButtonCompat);
            } else {
                final int drawableLeftId = attributeArray.getResourceId(R.styleable.CustomDrawableButton_drawableLeftButtonCompat, -1);
                final int drawableRightId = attributeArray.getResourceId(R.styleable.CustomDrawableButton_drawableRightButtonCompat, -1);
                final int drawableBottomId = attributeArray.getResourceId(R.styleable.CustomDrawableButton_drawableBottomButtonCompat, -1);
                final int drawableTopId = attributeArray.getResourceId(R.styleable.CustomDrawableButton_drawableTopButtonCompat, -1);

                if (drawableLeftId != -1)
                    drawableLeft = AppCompatResources.getDrawable(context, drawableLeftId);
                if (drawableRightId != -1)
                    drawableRight = AppCompatResources.getDrawable(context, drawableRightId);
                if (drawableBottomId != -1)
                    drawableBottom = AppCompatResources.getDrawable(context, drawableBottomId);
                if (drawableTopId != -1)
                    drawableTop = AppCompatResources.getDrawable(context, drawableTopId);
            }
            setCompoundDrawablesWithIntrinsicBounds(drawableLeft, drawableTop, drawableRight, drawableBottom);
            attributeArray.recycle();
        }
    }
}
