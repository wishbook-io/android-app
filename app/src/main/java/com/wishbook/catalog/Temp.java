package com.wishbook.catalog;

import android.util.Log;
import android.view.View;

import com.wishbook.catalog.Utils.DateUtils;
import com.wishbook.catalog.Utils.multipleimageselect.models.Image;
import com.wishbook.catalog.login.Fragment_VerifyOtp;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Temp {

    public static void main(String args[]){
        checkMessage();
        distictData();
        boolean isSinglePcAvailable = false;
        System.out.println("Boolean Check"+String.valueOf(!isSinglePcAvailable));
        Class t = Temp.class;
        try {
            Method m = t.getDeclaredMethod("checkMethodReflection",Double.class);
            try {
                Temp searchActivity = new Temp();
                m.invoke(searchActivity,5.0);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        String otp = "<#>Your Wishbook verification code is 985377\nk/zNkgfvG4E";
        String[] otp_array = otp.split("\n");
        String regex = "\\\\d+";
        if(otp_array.length > 0 ) {
            System.out.println("Code 0 is==>"+otp_array[0]);
            if (otp_array[0].contains("code")) {
                Pattern pattern = Pattern.compile("[0-9]+");
                Matcher matcher = pattern.matcher(otp_array[0]);
                while (matcher.find()) {
                    String match = matcher.group();
                    System.out.println("Regex Match Find====>"+match);
                }
            }

            String valid_till ="2019-09-07T18:00:00.000000Z";
            System.out.println("Date ===>"+DateUtils.getCouponsValidity(valid_till));;

            try {
                int photo_counter = 0;
                for (int  i = 0;i< 5;i++) {
                    Thread.sleep(500);
                    photo_counter++;
                    System.out.println("main: PhotoCounter"+photo_counter );

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(88*3);
            System.out.println(56/12);

            String test_one ="Mode : COD\n" +
                    "Status : Partially Paid\n" +
                    "TransactionId : C8993\n" +
                    "Paid Amount: 0.00\n" +
                    "Pending Amount: 663.0\n";

            String[] multi_string = test_one.split("\n");
            for (String s:
                 multi_string) {
                if(!s.contains("Paid Amount") && !s.contains("Pending Amount"))
                    System.out.println("String====>"+s);
            }





         /*   if(otp_array[0].matches(regex)) {
                System.out.println("Regex Match");
            } else {
                System.out.println("Regex Not Match");
            }*/

        }
       /* String error ="ooooooo";

        if (!error.equals("") &&
                (!error.contains("handshake")
                && !error.contains("cache")
                && !error.contains("failed to rename")
                && !error.contains("connect"))) {
            System.out.println("Error throw");

        }

        //String phone ="Hi Bhavik 99 24713 811";
        String phone ="price 5000 is  99 247 13811 hello";
        //String phone ="999-250-4856";


        String orignal_one = new String(phone);
        phone = phone.replaceAll("#","");
        phone =  phone.replaceAll(" ","");

        ArrayList<String> phone_regex = new ArrayList<>();


       if(isPhoneNumber(phone) > 8) {
          System.out.println(orignal_one.replaceAll("^\\d{3}-\\d{3}-\\d{4}$","*"));

           System.out.println(orignal_one.replaceAll("^[+]?(?:\\(\\d+(?:\\.\\d+)?\\)|\\d+(?:\\.\\d+)?)(?:[ -]?(?:\\(\\d+(?:\\.\\d+)?\\)|\\d+(?:\\.\\d+)?))*(?:[ ]?(?:x|ext)\\.?[ ]?\\d{1,5})?$","****"));


           System.out.println(orignal_one.replaceAll("[\\.)(]*([0-9+-.]{2,3})[\\.)( ]*([0-9+-.]{3})[\\.)( ]*([0-9+-.]{2,8})$","****"));

           System.out.println(orignal_one.replaceAll("[\\.)(]*([0-9+-.]{1,4})[\\.)( ]*([0-9+-.]{1,4})[\\.)( ]*([0-9+-.]{1,4})[\\.)( ]*([0-9+-.]{1,4})[\\.)( ]*([0-9+-.]{1,4})[\\.)( ]*([0-9+-.]{1,4})[\\.)( ]*([0-9+-.]{1,4})[\\.)( ]*([0-9+-.]{1,4})[\\.)( ]*([0-9+-.]{1,4})[\\.)( ]*([0-9+-.]{1,4})$","****"));


           System.out.println(orignal_one.replaceAll("[\\.)(]*([0-9+-.]{1,2})[\\.)( ]*([0-9+-.]{1,4})[\\.)( ]*([0-9+-.]{1,4})[\\.)( ]*([0-9+-.]{1,4})[\\.)( ]*([0-9+-.]{1,4})[\\.)( ]*([0-9+-.]{1,4})[\\.)( ]*([0-9+-.]{1,4})","****"));
       }

       // Pattern p = Pattern.compile("^[+]?(?:\\(\\d+(?:\\.\\d+)?\\)|\\d+(?:\\.\\d+)?)(?:[ -]?(?:\\(\\d+(?:\\.\\d+)?\\)|\\d+(?:\\.\\d+)?))*(?:[ ]?(?:x|ext)\\.?[ ]?\\d{1,5})?$");

        Pattern p = Pattern.compile("[\\.)(]*([0-9+-.]{2,3})[\\.)( ]*([0-9+-.]{3})[\\.)( ]*([0-9+-.]{2,8})$");
        Matcher m = p.matcher(orignal_one);
        boolean is_valid  = m.find() &&m.group().equals(orignal_one);

        System.out.println("Is Valid===>"+is_valid);




        String name = "";

        if(name == null || name.isEmpty()){
            System.out.println("name is invalid ");
        }*/


    }

    public static int isPhoneNumber(String s)
    {

     /*   Pattern p = Pattern.compile();
        Matcher m = p.matcher(s);
        int count = 0;
        return (m.find() && m.group().equals(s));*/


      /*  Pattern p = Pattern.compile("^(?!.*?\\d{4}).+$");
        Matcher m  = p.matcher(s);
        int count = 0;
        while (m.find())
            count++;
        return count;*/


       /* Pattern p = Pattern.compile("^\\d{3}-\\d{3}-\\d{4}$");
        Matcher m  = p.matcher(s);
        int count = 0;
        while (m.find())
            count++;
        return count;*/



        Pattern p = Pattern.compile("[0-9]");
        Matcher m = p.matcher(s);
        int count = 0;
        while (m.find())
            count++;
        return count;
    }


    public static void distictData() {
        ArrayList<String> catalog_size = new ArrayList<>();
        ArrayList<Image> images = new ArrayList<>();
        ArrayList<String> one = new ArrayList<>();
        one.add("L");one.add("XL");one.add("M");

        ArrayList<String> two = new ArrayList<>();
        two.add("M");two.add("XL");

        ArrayList<String> three = new ArrayList<>();
        three.add("XL");three.add("XXL");

        images.add(new Image(one));
        images.add(new Image(two));
        images.add(new Image(three));

        for (int i=0;i<images.size();i++) {
            if(catalog_size.containsAll(images.get(i).getAvailable_sizes())) {
               System.out.println("Contains i===>"+i);
            } else {
                System.out.println("Not Contains i===>"+i);
               /* for (int j = 0;j<catalog_size.size();j++) {
                    if(!images.get(i).getAvailable_sizes().(catalog_size)) {
                        catalog_size.add();
                    }
                }*/
                catalog_size.addAll(images.get(i).getAvailable_sizes());

            }
        }

        Set uniqueValues = new HashSet(catalog_size);
        System.out.println("Unique===>"+uniqueValues.size());
        List<String> listFromSet = new ArrayList<String>(uniqueValues);
        for (int i = 0;i<listFromSet.size();i++) {
            System.out.println("list Value===>"+listFromSet.get(i));
        }
    }

    public void checkMethodReflection(Double d) {
        System.out.println("Test CheckMethodReflection"+d.toString());
    }

    public static boolean isContainsPhoneNumber(String s)
    {
        Pattern p = Pattern.compile("[\\.)(]*([0-9+-.]{1,2})[\\.)( ]*([0-9+-.]{1,4})[\\.)( ]*([0-9+-.]{1,4})[\\.)( ]*([0-9+-.]{1,4})[\\.)( ]*([0-9+-.]{1,4})[\\.)( ]*([0-9+-.]{1,4})[\\.)( ]*([0-9+-.]{1,4})");
        Matcher m = p.matcher(s);
        if(m.find()) {
            System.out.println("phone match"+m.group());
        }
        return (m.find());

    }

    public static void checkMessage() {
        String html_text = "Due to the high returns, you will not be able to place new orders on Wishbook. Please contact us on 02616718997 if you think this message is in error.";
        System.out.println("Check Message====>"+isContainsPhoneNumber(html_text));
    }
}
