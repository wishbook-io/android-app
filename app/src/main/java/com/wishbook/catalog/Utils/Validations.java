package com.wishbook.catalog.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Rahul on 07/Mar/2016.
 */
public class Validations {

    public static String valid = "ValidField";
    public static String invalid = "InvalidField";
    public static String minError = "MinCharError";
    public static String maxError = "MaxCharError";
    public static String empty = "isEmpty";

    // Email Validation
    public static boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Username Validation - no spaces or special characters
    public static String isUsernameValid(String username, int minValue, int maxValue) {

        String error = invalid;

        if (isEmpty(username)) {
            error = empty;
            return error;
        }

        if (isMin(username, minValue)) {
            error = minError;
            return error;
        }

        if (isMax(username, maxValue)) {
            error = maxError;
            return error;
        }

        String expression = "^[a-z0-9]{1,20}$";

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(username);
        if (matcher.matches()) {
            error = valid;
            return error;
        }
        return error;
    }

    // Password Validation - All special characters are allowed as well as spaces
    public static String isPasswordValid(String field, int minValue, int maxValue) {
        String error = invalid;

        if (isEmpty(field)) {
            error = empty;
            return error;
        }

        if (isMin(field, minValue)) {
            error = minError;
            return error;
        }

        if (isMax(field, maxValue)) {
            error = maxError;
            return error;
        }

        if (!(isMin(field, minValue) && isMax(field, maxValue))) {
            error = valid;
        }

        return error;
    }

    // First name and Last name Validations with no spaces
    public static String isPersonNameValid(String firstName, int minValue, int maxValue) {

        String error = invalid;

        if (isEmpty(firstName)) {
            error = valid;
            return error;
        }

        if (isMin(firstName, minValue)) {
            error = minError;
            return error;
        }

        if (isMax(firstName, maxValue)) {
            error = maxError;
            return error;
        }

        String expression = "^[\\p{L} .'-]{1,20}+$";

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(firstName);
        if (matcher.matches()) {
            error = valid;
            return error;
        }
        return error;
    }

    /*public static boolean isMobileNumValid(String mobileNumber) {
        boolean isValid = false;

        if (isEmpty(mobileNumber)) {
            isValid = true;
            return isValid;
        }

        *//* if(isMin(mobileNumber, minValue)){
            isValid = false;
        }

        if (isMax(mobileNumber, maxValue)){
            isValid = false;
        }*//*

        String expression = "(\\+[0-9]+[\\- \\.]*)?"      // +<digits><sdd>*
                + "(\\([0-9]+\\)[\\- \\.]*)?"   // (<digits>)<sdd>*
                + "([0-9][0-9\\- \\.][0-9\\- \\.]+[0-9])";

        //String expression = "/[1-9]{2}\\d{8}/";
        CharSequence inputStr = mobileNumber;

        Pattern pattern = Build.VERSION.SDK_INT >= 16 ? Patterns.PHONE : Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
            return isValid;
        }

        return isValid;
    }*/

    public static boolean isMobileNumValid(String mobileNumber) {//}, int maxValue, int minValue) {
        if(mobileNumber.equals("")){
            return true;
        }
        if(mobileNumber.length() < 10){
            return false;
        }
        return android.util.Patterns.PHONE.matcher(mobileNumber).matches();

/*
        if (isEmpty(mobileNumber)) {
            isValid = true;
            return isValid;
        }

      *//* if(isMin(mobileNumber, minValue)){
            isValid = false;
            return isValid;
        }

        if (isMax(mobileNumber, maxValue)){
            isValid = false;
            return isValid;
        }*//*

        String expression = "\\d{10}+$";
        CharSequence inputStr = mobileNumber;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
            return isValid;
        }*/


    }

    // City Validation - no special characters only periods, '-' and spaces
    public static String isCityNameValid(String city) {

        String error = invalid;

        if (isEmpty(city)) {
            error = valid;
            return error;

        }
        String expression = "^[\\p{L} .-]+$";

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(city);
        if (matcher.matches()) {
            error = valid;
            return error;
        }
        return error;
    }

    // State Validation - no special characters or periods, only spaces
    public static String isStateCountryNameValid(String state) {

        String error = invalid;

        if (isEmpty(state)) {
            error = valid;
            return error;
        }

        String expression = "^[\\p{L} ]+$";

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(state);
        if (matcher.matches()) {
            error = valid;
            return error;
        }
        return error;
    }

    // Validation - check if field is empty or not
    public static boolean isEmpty(String field) {
        boolean isValid = false;

        if (field.equals("")) {
            isValid = true;
        }
        return isValid;
    }

    // Validation - check if field has got minimum characters or not
    public static boolean isMin(String field, int minValue) {
        boolean isValid = false;

        if ((!field.equals("") && field.length() < minValue)) {
            isValid = true;
        }
        return isValid;
    }

    // Validation - check if field has got maximum characters or not
    public static boolean isMax(String field, int maxValue) {
        boolean isValid = false;

        if ((!field.equals("") && field.length() > maxValue)) {
            isValid = true;
        }
        return isValid;
    }
}