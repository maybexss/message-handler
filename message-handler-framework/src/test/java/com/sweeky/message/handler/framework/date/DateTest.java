package com.sweeky.message.handler.framework.date;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.sweeky.message.handler.common.constants.DateFormat.DATE_FORMAT_YYYYMMDD;

public class DateTest {
    public static void main(String[] args) {
        String date = new SimpleDateFormat(DATE_FORMAT_YYYYMMDD).format(new Date());
        System.out.print(date);
    }
}
