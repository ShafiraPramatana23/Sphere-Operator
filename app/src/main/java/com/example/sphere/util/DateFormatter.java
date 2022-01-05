package com.example.sphere.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateFormatter {
    public String UTCtoDate(String utc) {
        Date date = new Date(Integer.parseInt(utc)*  (long) 1000);
        DateFormat format = new SimpleDateFormat("EEEE", new Locale("ID"));
        format.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
        String formatted = format.format(date);
        System.out.println(formatted);
        return formatted;
    }

    public String UTCtoTime(String utc) {
        Date date = new Date(Integer.parseInt(utc)*  (long) 1000);
        DateFormat format = new SimpleDateFormat("HH:mm", new Locale("ID"));
        format.setTimeZone(TimeZone.getDefault());
        String formatted = format.format(date);
        System.out.println(formatted);
        return formatted;
    }

    public String convertDate(String dt) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        SimpleDateFormat fmt2 = new SimpleDateFormat("dd MMM yyyy HH:ss", new Locale("ID"));
        try {
            Date date = fmt.parse(dt);
            return fmt2.format(date);
        } catch(ParseException pe) {
            return "Date";
        }
    }
}