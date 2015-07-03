package fr.cm.scorexpress.core.model.impl;

import java.text.*;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import fr.cm.scorexpress.core.model.ObjChrono;

public class DateUtils {

    private static final transient DateFormat   sdfMinutes = getSimpleDateFormat("mm:ss");
    private static final transient DateFormat   sdf        = getSimpleDateFormat("HH:mm:ss");
    private static final transient DateFormat   sdf2       = getSimpleDateFormat2("mm:ss");
    private static final transient DateFormat   sdf3       = getSimpleDateFormat("+HH:mm:ss");
    private static final transient NumberFormat nf         = new DecimalFormat("#,#########");
    private static final transient DateFormat   sdfJour    = getSimpleDateFormat("dd");
    private static final transient DateFormat   sdfHeure   = getSimpleDateFormat("HH");
    private static final transient DateFormat   sdfMinute  = getSimpleDateFormat(":mm:ss");
    private static final transient DateFormat   sdfSecond  = getSimpleDateFormat(":ss");
    private static final transient DateFormat   sdfDate    = getSimpleDateFormat("hh:mm:ss dd/MM/yyyy");
    private static final transient NumberFormat df         = new DecimalFormat("00");
    private static final transient DateFormat   sdfPerso   = null;

    private DateUtils() {
    }

    public static int compare(final ObjChrono chrono1, final ObjChrono chrono2) {
        return chrono1.getTemps().compareTo(chrono2.getTemps());
    }

    private static DateFormat getSimpleDateFormat2(final String pattern) {
        final DateFormat sdf      = new SimpleDateFormat(pattern);
        final TimeZone   timeZone = TimeZone.getDefault();
        timeZone.setRawOffset(-3600000);
        sdf.setTimeZone(timeZone);
        return sdf;
    }

    private static DateFormat getSimpleDateFormat(final String pattern) {
        final DateFormat sdf      = new SimpleDateFormat(pattern);
        final TimeZone   timeZone = TimeZone.getDefault();
        timeZone.setRawOffset(0);
        sdf.setTimeZone(timeZone);
        return sdf;
    }

    public static void add(final Date2 t, final Date d) {
        if (d != null) {
            t.setTime(t.getTime() + d.getTime());
            if (d.getTime() != 0) {
                t.setNull(false);
            }
        }
    }

    public static void remove(final Date2 t, final Date d) {
        if (d != null) {
            t.setTime(t.getTime() - d.getTime());
            if (d.getTime() != 0) {
                t.setNull(false);
            }
        }
    }

    public static String showDate(final Date date) {
        return sdfDate.format(date);
    }

    public static long parseDate(final String source) {
        try {
            return sdfDate.parse(source).getTime();
        } catch (final Exception e) {
            return 0;
        }
    }

    public static Date getHeure(final String heure) {
        if (heure == null) {
            return null;
        }
        if (heure.equalsIgnoreCase("-----") || heure.equalsIgnoreCase("")) {
            return null;
        }
        if (sdfPerso != null) {
            try {
                return sdfPerso.parse(heure);
            } catch (final ParseException e) {
                e.printStackTrace();
                return new Date(0);
            }
        }
        final boolean negatif = heure.indexOf('-') != -1;
        try {
            Date d = sdf.parse(heure);
            if (negatif && d.getTime() > 0) {
                d = new Date(d.getTime() * -1);
            }
            return d;
        } catch (final Exception ex) {
        }
        try {
            return sdf3.parse(heure);
        } catch (final Exception ex3) {
        }
        try {
            if (!negatif) {
                Date d = sdf2.parse(heure);
                final long offset = Calendar.getInstance().getTimeZone().getOffset(d.getTime());
                if (d.getTime() > 0) {
                    d = new Date(d.getTime() - offset);
                }
                return d;
            }
        } catch (final ParseException ex2) {
        }
        try {
            final double val = nf.parse(heure).doubleValue();
            final long milliseconde = new Double(val * 24 * 3600000).longValue();
            return new Date(milliseconde);
        } catch (final Exception ex1) {
            return new Date(0);
        }
    }

    public static String getHeureStr(Date date, final boolean hidePlus) {
        if (sdfPerso != null) {
            return sdfPerso.format(date);
        }
        try {
            long nbHour = 0;
            String signe = "";
            if (date.getTime() < 0) {
                signe = "-";
                nbHour = date.getTime() / 3600000;
                date = DateFactory.createDate(nbHour * 3600000 - date.getTime());
            } else if (!hidePlus) {
                signe = "+";
            }
            final int nbJour = new Integer(sdfJour.format(date)) - 1;
            // if ( (date.getTime()) < 0){
            // date = new Date(date.getTime()*-1);
            // }
            if (nbJour > 0) {
                final int heure = new Integer(sdfHeure.format(date));
                // if (nbJour > 1)
                // System.out.println("NbJour >1");
                final long nbHour1 = 24 * nbJour + heure;
                return signe + df.format(nbHour1) + sdfMinute.format(date);
            } else if (nbHour != 0) {
                return df.format(nbHour) + sdfMinute.format(date);
            }
            return signe + sdf.format(date);
        } catch (final Exception ex) {
            return null;
        }
    }

    public static String getMinutesStr(final Date date, final boolean hidePlus) {
        if (sdfPerso != null) {
            return sdfPerso.format(date);
        }
        try {
            long nbMinutes = date.getTime() / 60000;
            String signe = "";
            if (date.getTime() < 0) {
                signe = "-";
                nbMinutes *= -1;
            } else if (!hidePlus) {
                signe = "+";
            }
            if (nbMinutes >= 60) {
                return getHeureStr(date, hidePlus);
            }
            return signe + nbMinutes + sdfSecond.format(date);
        } catch (final Exception ex) {
            return null;
        }
    }

    public static void upTime(final Date2 date2, final Date d) {
        add(date2, d);
    }

    public static void downTime(final Date2 date2, final Date d) {
        remove(date2, d);
    }

    @SuppressWarnings({"TypeMayBeWeakened"})
    public static boolean equalsDate(final Date2 temps, final Date2 temps1) {
        if (temps == null || temps1 == null) {
            return false;
        } else {
            return temps.equals(temps1);
        }
    }
}
