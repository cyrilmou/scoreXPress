package fr.cm.scorexpress.core.model;

import static java.lang.Boolean.parseBoolean;
import static java.lang.Boolean.valueOf;
import java.util.ArrayList;
import java.util.Collection;

public class ObjTeam extends IUser implements IUsers {

    private static final long serialVersionUID = 3229394692507698254L;

    public static final String VAR_ABANDON = "ABANDON";

    public static final String VAR_PREFIX = "TEAM_";

    private final Collection<IUser> users = new ArrayList<IUser>();

    private Integer category;

    public ObjTeam() {
    }

    public ObjTeam(final IData parent) {
        this.parent = parent;
    }

    public String toString() {
        String info = "<PARTICIPANT>";
        for (final InfoDivers infoDivers : infoDiverses.values()) {
            info += infoDivers;
        }
        return info + "</PARTICIPANT>";
    }

    public boolean setInfoLocal(final String attribut, final Object val) {
        return false;
    }

    public ObjDossard getDossard() {
        if (parent != null && parent instanceof ObjDossard) {
            return (ObjDossard) parent;
        }
        return null;
    }

    public boolean isAbandon() {
        final InfoDivers infoDivers = infoDiverses.get(VAR_ABANDON);
        try {
            return parseBoolean("" + infoDivers.getInfo());
        } catch (Exception ex) {
            return false;
        }
    }

    public Object getInfoLocal(final String attribut) {
        if (VAR_ABANDON.equalsIgnoreCase(attribut)) {
            return valueOf(isAbandon());
        }
        return null;
    }

    public String getPrefix() {
        return VAR_PREFIX;
    }

    public Object accept(final ElementModelVisitor visitor, final Object data) {
        return visitor.visitTeam(this, data);
    }

    public boolean addUser(final IUser p) {
        p.setParent(this);
        return users.add(p);
    }

    public Collection<IUser> getUsers() {
        return users;
    }

    public boolean removeUser(final IUser p) {
        return users.remove(p);
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(final Integer category) {
        this.category = category;
    }
}
