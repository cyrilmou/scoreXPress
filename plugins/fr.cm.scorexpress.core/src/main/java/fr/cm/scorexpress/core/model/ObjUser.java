package fr.cm.scorexpress.core.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class ObjUser extends IUser {

    private static final long serialVersionUID = -8993732005522400016L;
    public static final String VAR_ID = "IDUSER";
    public static final String VAR_ABANDON = "ABANDON";
    public static final String VAR_HORSCOURSE = "HORSCOURSE";
    public static final String VAR_PREFIX = "PARTICIPANT_";
    public static final String VAR_DATENAISS = "DATENAISS";
    private static final DateFormat dateNaiss = new SimpleDateFormat("dd/MM/yyyy");

    public ObjUser() {
    }

    public ObjUser(final IData parent) {
        this.parent = parent;
    }

    @Override
    public String toString() {
        String info = "<PARTICIPANT>";
        for (final InfoDivers infoDivers : infoDiverses.values()) {
            info += infoDivers;
        }
        return info + "</PARTICIPANT>";
    }

    private void setDateNaiss(final String date) {
        try {
            dateNaiss.parse(date);
            setInfoDiverse(VAR_DATENAISS, date);
        } catch (Exception ex) {
        }
    }

    public boolean setInfoLocal(final String attribut, final Object val) {
        if (attribut.equalsIgnoreCase(VAR_DATENAISS)) {
            setDateNaiss("" + val);
            return true;
        }
        return false;
    }

    public Object getInfoLocal(final String attribut) {
        if (VAR_ID.equalsIgnoreCase(attribut)) {
            return super.getId();
        }
        return null;
    }

    public String getPrefix() {
        return VAR_PREFIX;
    }

    public Object accept(final ElementModelVisitor visitor, final Object data) {
        return visitor.visitUser(this, data);
    }
}
