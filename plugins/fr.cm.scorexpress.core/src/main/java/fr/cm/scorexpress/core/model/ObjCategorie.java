package fr.cm.scorexpress.core.model;

import static org.apache.commons.lang.StringUtils.EMPTY;

public class ObjCategorie extends IData<IData> {
    private static final long serialVersionUID = 7675251679135510883L;
    private String nom;
    private Integer id;
    private String sexe;

    public static final String NOM = "NOM";
    public static final String AGE = "AGE";
    public static final String SEXE = "SEXE";
    public static final String VAR_CATEGORIE_GROUP = "group";

    public ObjCategorie(final String nom) {
        this.nom = nom;
    }

    public ObjCategorie(final String nom, final Integer id) {
        this(nom);
        this.id = id;
    }

    public boolean equals(final Object object) {
        return nom.equals(((ObjCategorie) object).getNom());
    }

    public String getNom() {
        return nom;
    }

    public Object getInfoLocal(final String attribut) {
        if (attribut == null) {
            return null;
        }
        if (NOM.equals(attribut)) {
            return nom;
        }
        if (SEXE.equals(attribut)) {
            return sexe;
        }
        return null;
    }

    public boolean setInfoLocal(final String attribut, final Object val) {
        if (NOM.equals(attribut)) {
            nom = EMPTY + val;
            return true;
        }
        if (SEXE.equals(attribut)) {
            sexe = EMPTY + val;
            return true;
        }
        return false;
    }

    public String getPrefix() {
        return "CAT_";
    }

    public Integer getId() {
        return id;
    }

    public Object accept(final ElementModelVisitor visitor, final Object data) {
        return visitor.visite(this, data);
    }

    public String getSexe() {
        return sexe;
    }

    public void setSexe(final String sexe) {
        this.sexe = sexe;
    }
}
