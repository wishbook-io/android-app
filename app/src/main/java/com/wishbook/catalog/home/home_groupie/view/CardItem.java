package com.wishbook.catalog.home.home_groupie.view;


import com.wishbook.catalog.R;
import com.wishbook.catalog.databinding.GroupieItemCardBinding;
import com.xwray.groupie.databinding.BindableItem;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import static com.wishbook.catalog.home.Fragment_Home2.INSET;
import static com.wishbook.catalog.home.Fragment_Home2.INSET_TYPE_KEY;


public class CardItem extends BindableItem<GroupieItemCardBinding> {

    @ColorInt
    private int colorRes;
    private CharSequence text;

    public CardItem(@ColorInt int colorRes) {
        this(colorRes, "");
    }

    public CardItem(@ColorInt int colorRes, CharSequence text) {
        this.colorRes = colorRes;
        this.text = text;
        getExtras().put(INSET_TYPE_KEY, INSET);
    }

    @Override
    public int getLayout() {
        return R.layout.groupie_item_card;
    }

    public void setText(CharSequence text) {
        this.text = text;
    }

    public CharSequence getText() {
        return text;
    }

    @Override
    public void bind(@NonNull GroupieItemCardBinding viewBinding, int position) {
        viewBinding.text.setText(text);
    }
}

