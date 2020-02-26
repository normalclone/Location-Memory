package com.app.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtil {
    private static String datetimePattern = "yyyy/MM/dd hh:mm:ss";
    private static String datePattern = "yyyy/MM/dd";
    public static String convertDatetimeToString(Date date){
        return new SimpleDateFormat(datetimePattern).format(date);
    }
    public static String convertDateToString(Date date){
        return new SimpleDateFormat(datePattern).format(date);
    }
    public static Date convertStringToDatetime(String date) throws ParseException{
        return new SimpleDateFormat(datetimePattern).parse(date);
    }
    public static String checkPickedNumber(int number){
        if(number >= 10) return String.valueOf(number);
        else return "0"+number;
    }
}
