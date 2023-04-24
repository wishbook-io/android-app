package com.wishbook.catalog.home.home_groupie;

import com.wishbook.catalog.home.Fragment_Home2;

import androidx.annotation.ColorInt;
import androidx.annotation.Dimension;

public class InsetItemDecoration extends com.wishbook.catalog.home.home_groupie.decoration.InsetItemDecoration {
    public InsetItemDecoration(@ColorInt int backgroundColor, @Dimension int padding) {
        super(backgroundColor, padding, Fragment_Home2.INSET_TYPE_KEY, Fragment_Home2.INSET);
    }
}
