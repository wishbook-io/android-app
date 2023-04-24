package com.wishbook.catalog.Utils;

import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {

    private static final int SECOND = 1000;
    private static final int MINUTE = 60 * SECOND;
    private static final int HOUR = 60 * MINUTE;
    private static final int DAY = 24 * HOUR;


    public static String getTimeAgo(String date, Context context) {

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            Date created_date = sdf.parse(date);
            long time = created_date.getTime();

            if (time < 1000000000000L) {
                // if timestamp given in seconds, convert to millis
                time *= 1000;
            }
            long now = Calendar.getInstance().getTimeInMillis() + 5000L;
            if (time > now || time <= 0) {
                return null;
            }

            final long diff = now - time;
            if (diff < MINUTE) {
                return "Just Now";
            } else if (diff < 2 * MINUTE) {
                return "a minute ago";
            } else if (diff < 50 * MINUTE) {
                return diff / MINUTE + " minutes ago";
            } else if (diff < 90 * MINUTE) {
                return "an hour ago";
            } else if (diff < 24 * HOUR) {
                return diff / HOUR + " hours ago";
            } else if (diff < 48 * HOUR) {
                return "Yesterday";
            } else {
                return diff / DAY + " days ago";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }


    public static String getCouponsValidity(String date) {

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            Date created_date = sdf.parse(date);
            long time = created_date.getTime();

            long now = Calendar.getInstance().getTimeInMillis() ;
            System.out.println("Time ==>"+time);
            System.out.println("Now==>"+now);


            long diff =  time - now;
            int numOfDays = (int) (diff / (1000 * 60 * 60 * 24));
            int hours = (int) (diff / (1000 * 60 * 60));
            int minutes = (int) (diff / (1000 * 60));

            if (minutes < 60) {
                return "1 hours.";
            } else if (hours <  24) {
                return hours +" hours";
            }  else {
                return numOfDays+" days";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

    public static String getFormattedDate(String dat) {
        String format = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        String toformat = "MMMM dd, yyyy";
        SimpleDateFormat tosdf = new SimpleDateFormat(toformat, Locale.US);

        try {
            Date date = sdf.parse(dat);
            return tosdf.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }


    public static Date stringToDate(String string_date, SimpleDateFormat format) {
        try {
            Date date = format.parse(string_date);
            System.out.println(date);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String changeDateFormat(String currentFormat, String requiredFormat, String dateString) {
        String result = "";
        if (dateString == null && !dateString.isEmpty()) {
            return result;
        }
        SimpleDateFormat formatterOld = new SimpleDateFormat(currentFormat, Locale.US);
        SimpleDateFormat formatterNew = new SimpleDateFormat(requiredFormat, Locale.US);
        Date date = null;
        try {
            date = formatterOld.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date != null) {
            result = formatterNew.format(date);
        }
        return result;
    }

    public static String currentIST(String dateFromServer) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        SimpleDateFormat sdfLocal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            return sdfLocal.format(sdf.parse(dateFromServer));
        } catch (ParseException e) {
            e.printStackTrace();
            try {
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                sdf1.setTimeZone(TimeZone.getTimeZone("GMT"));
                return sdfLocal.format(sdf1.parse(dateFromServer));

            } catch (Exception e1) {

            }

        }

        return "";

       /* String inputFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        String outputFormat = "MM/dd/yyyy hh:mm:ss a";
        SimpleDateFormat sdfInput = new SimpleDateFormat(inputFormat);
        SimpleDateFormat sdfOutput = new SimpleDateFormat(outputFormat);

        // Convert to local time zone
        sdfOutput.setTimeZone(TimeZone.getDefault());

        try {
            Date parsed = sdfInput.parse(dateFromServer);
            String outputDate = sdfOutput.format(parsed);
        } catch (Exception e) {
            Log.e("formattedDateFromString", "Exception in formateDateFromstring(): " + e.getMessage());
        }




        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        SimpleDateFormat sdfLocal = new SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.getDefault());
        Date date = null;
        try {
            date = sdf.parse(dateFromServer);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        sdf.setTimeZone(TimeZone.getDefault());
        if(date!=null) {
            try {
                return String.valueOf(sdfLocal.format( sdf.format(date)));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }else{
            return "";
        }
        return  "";*/
    }


    public static String currentISTDate(String dateFromServer) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        SimpleDateFormat sdfLocal = new SimpleDateFormat("yyyy-MM-dd");
        // sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            return sdfLocal.format(sdf.parse(dateFromServer));
        } catch (ParseException e) {
            e.printStackTrace();
            try {
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                sdf1.setTimeZone(TimeZone.getTimeZone("GMT"));
                return sdfLocal.format(sdf1.parse(dateFromServer));

            } catch (Exception e1) {

            }
        }
        return "";

    }

    public static String currentISTTime(String dateFromServer) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        SimpleDateFormat sdfLocal = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            return sdfLocal.format(sdf.parse(dateFromServer));
        } catch (ParseException e) {
            e.printStackTrace();
            try {
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                sdf1.setTimeZone(TimeZone.getTimeZone("GMT"));
                return sdfLocal.format(sdf1.parse(dateFromServer));

            } catch (Exception e1) {

            }

        }

        return "";

    }

    public static String currentUTC() {
        Date test = new Date();
        test.toGMTString();
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormatGmt.format(test);
    }


}
