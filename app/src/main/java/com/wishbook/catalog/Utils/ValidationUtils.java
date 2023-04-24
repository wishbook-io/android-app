package com.wishbook.catalog.Utils;

import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.EditText;

public class ValidationUtils {

    public static boolean validatePhone(String input_phone){
        if(input_phone.charAt(0) == '9' || input_phone.charAt(0) == '6'
                || input_phone.charAt(0) == '7' || input_phone.charAt(0) == '8' && input_phone.length()==10){
            return true;
        }else{
            return false;
        }
    }

    public final static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public final static boolean isValidAadaharNumber(CharSequence target) {
        return (!TextUtils.isEmpty(target) && target.length() == 12);
    }

    public static boolean isEmptyValidation(EditText editText) {
        if (editText.getText().toString().trim().isEmpty())
            return true;
        else
            return false;
    }

    public static boolean validCellPhone(String number) {
        return android.util.Patterns.PHONE.matcher(number).matches();
    }

    public static boolean isIFSCFifthCharacterZero(EditText editText) {
        if(!editText.getText().toString().trim().isEmpty()) {
            char validate_char = new Character('0');
            if(editText.getText().toString().length() > 5) {
                char fifth_char =   editText.getText().toString().charAt(4);
                if(validate_char == fifth_char) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

}
