package com.wishbook.catalog.Utils.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.appcompat.content.res.AppCompatResources;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

import com.wishbook.catalog.R;

public class EditTextBackEvent extends EditText {

    private EditTextImeBackListener mOnImeBack;

    public EditTextBackEvent(Context context) {
        super(context);
    }

    public EditTextBackEvent(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
    }

    public EditTextBackEvent(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttrs(context, attrs);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            if (mOnImeBack != null) mOnImeBack.onImeBack();
        }
        return super.dispatchKeyEvent(event);
    }

    public void setOnEditTextImeBackListener(EditTextImeBackListener listener) {
        mOnImeBack = listener;
    }
    public interface EditTextImeBackListener {
        void onImeBack();
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

