package ru.job4j.total;

import java.text.SimpleDateFormat;
import java.util.Locale;

class Utils {

    static String getDate(long date) {
        SimpleDateFormat fullDateFormat
                = new SimpleDateFormat("MMM dd yyyy", Locale.getDefault());
        return fullDateFormat.format(date);
    }
}
