package com.wishbook.catalog.Utils.widget;


public class SpecialCharacterInputFilter extends RegexInputFilter {

    private static final String SPECIAL_CHARACTER_REGEX = "[ a-zA-Z0-9]+";

    public SpecialCharacterInputFilter() {
        super(SPECIAL_CHARACTER_REGEX);
    }
}
