package fr.cm.scorexpress.core.model;

public final class ObjColTable implements ColTable {
    public static final String VAR_ALIGN = "ALIGN";
    private static final long serialVersionUID = -2938909791287459280L;

    private String champ;
    private final String champSecondaire;
    private String lib;
    private final String lib2;
    private final String description;
    private String choix;
    private final boolean choixObligatoire;
    private boolean masque;
    private int width;
    private final ColTableType type;
    private final boolean modifiable;
    private final boolean tmp;
    private final int align;
    private transient IData element;

    ObjColTable(final String champ, final String lib, final int width, final ColTableType type,
                final String champSecondaire, final String lib2, final boolean modifiable,
                final boolean tmp, final boolean choixObligatoire, final String choix,
                final boolean masque, final String description, final int align, final IData element) {
        this.champ = champ;
        this.lib = lib;
        this.width = width;
        this.type = type;
        this.champSecondaire = champSecondaire;
        this.lib2 = lib2;
        this.modifiable = modifiable;
        this.tmp = tmp;
        this.choixObligatoire = choixObligatoire;
        this.choix = choix;
        this.masque = masque;
        this.description = description;
        this.align = align;
        this.element = element;
    }

    public String toString() {
        return "field=" + champ + ",lib=\"" + lib + '\"';
    }

    public String isChoixObligatoireStr() {
        if (!choixObligatoire) {
            return null;
        }
        return choixObligatoire + "";
    }

    public String isMasqueStr() {
        if (!masque) {
            return null;
        }
        return masque + "";
    }

    public String isModifiableStr() {
        if (modifiable) {
            return null;
        }
        return modifiable + "";
    }

    public int compareTo(final ColTable colTable) {
        try {
            return champ.compareTo(colTable.getChamp());
        } catch (Exception ex) {
            return 1;
        }
    }

    /**
     * @param element the element to set
     */
    public void setElement(final IData element) {
        this.element = element;
    }

    public String getChampSecondaire() {
        return champSecondaire;
    }

    public IData getElement() {
        return element;
    }

    public Object accept(final ElementModelVisitor visitor, final Object data) {
        return visitor.visite(this, data);
    }

    public boolean isTmp() {
        return tmp;
    }

    public boolean isModifiable() {
        return modifiable;
    }

    public ColTableType getType() {
        return type;
    }

    public int getAlign() {
        return align;
    }

    public int getWidth() {
        return width;
    }

    public boolean isMasque() {
        return masque;
    }

    public String getLib() {
        return lib;
    }

    public String getLib2() {
        return lib2;
    }

    public String getDescription() {
        return description;
    }

    public boolean isChoixObligatoire() {
        return choixObligatoire;
    }

    public String getChoix() {
        return choix;
    }

    public String getChamp() {
        return champ;
    }

    @Deprecated
    public void setWidth(final int width) {
        this.width = width;
    }

    @Deprecated
    public void setChoix(final String choix) {
        this.choix = choix;
    }

    @Deprecated
    public void setMasque(final boolean masque) {
        this.masque = masque;
    }

    @Deprecated
    public void setChamp(final String champ) {
        this.champ = champ;
    }

    @Deprecated
    public void setLib(final String lib) {
        this.lib = lib;
    }
}
