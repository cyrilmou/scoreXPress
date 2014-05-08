package fr.cm.scorexpress.core.model.impl;

import java.util.Date;

public class DateFactory {
    private DateFactory() {
    }

    public static Date2 createDate() {
        return new Date2();
    }

    public static Date2 createDate(final boolean dateNull) {
        return new Date2(dateNull);
    }

    public static Date2 createDate(final Date d) {
        return new Date2(d);
    }

    public static Date2 createDate(final long date) {
        return new Date2(date);
    }

    public static Date2 createDate(final String s) {
        return new Date2(s);
    }

}
