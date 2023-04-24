package com.wishbook.catalog.Utils.widget;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatEditText;
import android.util.AttributeSet;

import com.wishbook.catalog.R;

public class CustomDrawableEditText extends AppCompatEditText {
    public CustomDrawableEditText(Context context) {
        super(context);
    }

    public CustomDrawableEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
    }

    public CustomDrawableEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
    }

    void initAttrs(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray attributeArray = context.obtainStyledAttributes(
                    attrs,
                    R.styleable.CustomDrawableEditText);

            Drawable drawableLeft = null;
            Drawable drawableRight = null;
            Drawable drawableBottom = null;
            Drawable drawableTop = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                drawableLeft = attributeArray.getDrawable(R.styleable.CustomDrawableEditText_drawableLeftEditTextCompat);
                drawableRight = attributeArray.getDrawable(R.styleable.CustomDrawableEditText_drawableRightEditTextCompat);
                drawableBottom = attributeArray.getDrawable(R.styleable.CustomDrawableEditText_drawableBottomEditTextCompat);
                drawableTop = attributeArray.getDrawable(R.styleable.CustomDrawableEditText_drawableTopEditTextCompat);
            } else {
                final int drawableLeftId = attributeArray.getResourceId(R.styleable.CustomDrawableEditText_drawableLeftEditTextCompat, -1);
                final int drawableRightId = attributeArray.getResourceId(R.styleable.CustomDrawableEditText_drawableRightEditTextCompat, -1);
                final int drawableBottomId = attributeArray.getResourceId(R.styleable.CustomDrawableEditText_drawableBottomEditTextCompat, -1);
                final int drawableTopId = attributeArray.getResourceId(R.styleable.CustomDrawableEditText_drawableTopEditTextCompat, -1);

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
