package fr.cm.scorexpress.core.model;

import fr.cm.scorexpress.core.model.impl.Date2;
import static fr.cm.scorexpress.core.model.impl.DateFactory.createDate;
import static org.apache.commons.lang.StringUtils.EMPTY;

import java.util.ArrayList;
import java.util.Date;

@SuppressWarnings({"NestedAssignment"}) public final class ObjBalise extends IData implements Balise {
    private static final long serialVersionUID = 9180913900067035745L;

    private String num = "0";
    private String description;
    private String type = null;
    private Date2 penalite = createDate(false);
    private int points = 1;
    private ArrayList<String> tabOrdre = null;

    public ObjBalise(final String num, final String type, final String description) {
        setNum(num);
        setType(type);
        this.description = description;
    }

    ObjBalise(final String num, final String type, final Date penalite) {
        this(num, type, EMPTY);
        this.penalite = createDate(penalite);
    }

    public String getDescription() {
        return description;
    }

    public String getNum() {
        return num;
    }

    public void setNum(final String num) {
        try {
            firePropertyChange(Balise.VAR_BALISE_NUM, this.num, this.num = new Integer(num) + EMPTY);
        } catch (Exception ignored) {
        }
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        final String newType;
        if (type == null) {
            newType = EMPTY;
        } else {
            newType = type;
        }
        firePropertyChange(Balise.VAR_BALISE_TYPE, this.type, this.type = newType);
    }

    public Object getInfoLocal(final String attribut) {
        if (attribut.equalsIgnoreCase(Balise.VAR_BALISE_NUM)) {
            return new Integer(num);
        }
        if (attribut.equalsIgnoreCase(Balise.VAR_BALISE_TYPE)) {
            return type;
        }
        if (attribut.equalsIgnoreCase(Balise.VAR_DESC)) {
            return description;
        }
        if (attribut.equalsIgnoreCase(Balise.VAR_BALISE_PENALITE)) {
            return getPenaliteStr();
        }
        if (attribut.indexOf(Balise.VAR_PREFIX_BALISE_ORDER) != -1) {
            try {
                final int nrOrdre = new Integer(attribut.substring(Balise.VAR_PREFIX_BALISE_ORDER.length(),
                        attribut.length()));
                if (tabOrdre != null && tabOrdre.size() > nrOrdre) {
                    return tabOrdre.get(nrOrdre);
                }
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public boolean setInfoLocal(final String attribut, final Object val) {
        if (attribut.equalsIgnoreCase(Balise.VAR_BALISE_NUM)) {
            setNum(EMPTY + val);
            return true;
        }
        if (attribut.equalsIgnoreCase(Balise.VAR_BALISE_TYPE)) {
            setType(EMPTY + val);
            return true;
        }
        if (attribut.equalsIgnoreCase(Balise.VAR_DESC)) {
            setDescription(EMPTY + val);
            return true;
        }
        if (attribut.equalsIgnoreCase(Balise.VAR_BALISE_PENALITE)) {
            setPenaliteStr(EMPTY + val);
            return true;
        }
        if (attribut.indexOf(Balise.VAR_PREFIX_BALISE_ORDER) != -1) {
            try {
                final int nrOrdre = new Integer(attribut.substring(
                        Balise.VAR_PREFIX_BALISE_ORDER.length(), attribut.length()));
                if (tabOrdre == null) {
                    tabOrdre = new ArrayList<String>();
                }
                while (tabOrdre.size() <= nrOrdre) {
                    tabOrdre.add(null);
                }
                try {
                    tabOrdre.set(nrOrdre, new Integer(val + EMPTY)
                            + EMPTY);
                } catch (RuntimeException e) {
                    tabOrdre.set(nrOrdre, null);
                }
                modifyCalculData();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    public String getPrefix() {
        return Balise.VAR_PREFIX;
    }

    public Date getPenalite() {
        return penalite;
    }

    public int getPoints() {
        return points;
    }

    public String getPenaliteStr() {
        final Date2 d = createDate(getPenalite());
        d.setAffichage(true, false, true);
        return d.toString();
    }

    public void setPenalite(final Date penalite) {
        this.penalite = createDate(penalite);
        modifyCalculData();
    }

    public void setPoints(final int points) {
        this.points = points;
        modifyCalculData();
    }

    public void setPenaliteStr(final String penalite) {
        this.penalite = createDate(penalite);
        modifyCalculData();
    }

    public int getOrdre(final int nrOrdre) {
        if (tabOrdre != null && nrOrdre < tabOrdre.size()) {
            try {
                return new Integer(tabOrdre.get(nrOrdre));
            } catch (Exception e) {
                return -1;
            }
        } else {
            return -1;
        }
    }

    public Object accept(final ElementModelVisitor visitor, final Object data) {
        return visitor.visitBalise(this, data);
    }

    public boolean equals(final Object obj) {
        if (obj != null && num != null && obj instanceof ObjBalise) {
            final Balise balise = (Balise) obj;
            if (balise.getNum() != null) {
                return balise.getNum().equals(getNum());
            }
        }
        return false;
    }

    public String toString() {
        final String pen = penalite.isNull() ? EMPTY : ",tps=" + penalite;
        return num + ",type=" + type + pen;
    }

    public void setOrder(final int noOrder, final int orderValue) {
        setInfoLocal(Balise.VAR_PREFIX_BALISE_ORDER + noOrder, orderValue);
    }
}
