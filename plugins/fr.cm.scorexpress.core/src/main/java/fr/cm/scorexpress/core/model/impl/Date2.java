package fr.cm.scorexpress.core.model.impl;

import static fr.cm.scorexpress.core.model.impl.DateUtils.getHeure;

import java.util.Date;

/**
 * <p>
 * Title: ChronosRAID
 * </p>
 * <p>
 * Description: Programme de chronométrage de RAID multisport
 * </p>
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * <p>
 * Company: Jurazimut
 * </p>
 *
 * @author Moutenet Cyril
 * @version 1.0
 */

public class Date2 extends Date {

    private static final long serialVersionUID = 1L;

    boolean dateNull = false;

    boolean showSigne = true;

    boolean hidePlus = true;

    boolean mode24h = true;

    boolean jour = false;

    private boolean showNull = true;

    Date2() {
        super(new Date().getTime());
    }

    Date2(final boolean dateNull) {
        this.dateNull = dateNull;
        super.setTime(0);
    }

    Date2(final Date d) {
        if (d != null)
            super.setTime(d.getTime());
        else {
            super.setTime(0);
            dateNull = true;
        }
    }

    Date2(final long date) {
        super(date);
    }

    Date2(final String s) {
        try {
            final Date d = getHeure(s);
            if (d != null) {
                super.setTime(d.getTime());
                return;
            }
        } catch (Exception ex) {
        }
        dateNull = true;
        super.setTime(0);
    }

    @Override
    public String toString() {
        if (jour)
            return DateUtils.showDate(this);
        if (showNull && dateNull)
            return null;
        return DateUtils.getHeureStr(this, hidePlus);
    }

    public String toStringOld() {
        return super.toString();
    }

    public boolean isNull() {
        return dateNull;
    }

    public void setNull() {
        dateNull = true;
        setTime(0);
    }

    public void setNull(final boolean dateNull) {
        this.dateNull = dateNull;
    }

    public void setAffichage(final boolean showSigne,
                             final boolean hidePlus,
                             final boolean mode24h) {
        this.hidePlus = hidePlus;
        this.showSigne = showSigne;
        this.mode24h = mode24h;
    }

    /**
     * @param jour the jour to set
     */
    public void setJour(final boolean jour) {
        this.jour = jour;
    }

    /**
     *
     */
    public void hideZeroValue() {
        if (getTime() == 0)
            setNull();
    }

    public String toStringNotNull() {
        if (getTime() == 0)
            return "";
        else
            return toString();
    }

    /**
     * @param showNull the showNull to set
     */
    public void setShowNull(final boolean showNull) {
        this.showNull = showNull;
    }
}
