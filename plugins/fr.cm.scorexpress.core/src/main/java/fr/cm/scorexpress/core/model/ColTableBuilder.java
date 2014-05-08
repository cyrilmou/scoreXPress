package fr.cm.scorexpress.core.model;

import static fr.cm.scorexpress.core.model.ColTableType.STRING;
import static fr.cm.scorexpress.core.model.ColTableType.valueOf;

public final class ColTableBuilder {
    private static final String EMPTY = "";
    private static final int NO_ALIGN = -1;

    private int align = NO_ALIGN;
    private String champ;
    private String lib;
    private String champSecondaire = EMPTY;
    private String choix = null;
    private boolean choixObligatoire = false;
    private String description = EMPTY;
    private int largeur = 100;
    private String lib2 = EMPTY;
    private boolean masque = false;
    private boolean modifiable = true;
    private boolean tmp = false;
    private ColTableType type = STRING;
    private IData element;

    public ColTableBuilder(final ColTable colTable) {
        this(colTable.getChamp(), colTable.getLib());
        align = colTable.getAlign();
        choix = colTable.getChoix();
        choixObligatoire = colTable.isChoixObligatoire();
        description = colTable.getDescription();
        largeur = colTable.getWidth();
        lib2 = colTable.getLib2();
        masque = colTable.isMasque();
        modifiable = colTable.isModifiable();
        tmp = colTable.isTmp();
        type = colTable.getType();
    }

    public ColTableBuilder(final String champ, final String lib) {
        this.champ = champ;
        this.lib = lib;
    }

    public ColTableBuilder withLib2(final String lib2) {
        this.lib2 = lib2;
        return this;
    }

    public ColTableBuilder withDescription(final String description) {
        this.description = description;
        return this;
    }

    public ColTableBuilder withChoice(final String choice) {
        choix = choice;
        return this;
    }

    public ColTableBuilder withChampSecondaire(final String champSecondaire) {
        this.champSecondaire = champSecondaire;
        return this;
    }

    public ColTableBuilder temp() {
        tmp = true;
        return this;
    }

    public ColTableBuilder modifiable() {
        modifiable = true;
        return this;
    }

    public ColTableBuilder visible(final boolean visible) {
        masque = !visible;
        return this;
    }

    public ColTableBuilder choixObligatoire(final boolean choixObligatoire) {
        this.choixObligatoire = !choixObligatoire;
        return this;
    }

    public ColTableBuilder withAlign(final int align) {
        this.align = align;
        return this;
    }

    public ColTableBuilder withWidth(final int width) {
        largeur = width;
        return this;
    }

    public ColTableBuilder withType(final ColTableType type) {
        this.type = type;
        return this;
    }

    public ColTableBuilder withType(final String type) {
        this.type = valueOf(type);
        return this;
    }

    public static ColTable createObjColTable(final String champ, final String lib, final int width,
                                             final ColTableType type,
                                             final String champSecondaire, final String lib2,
                                             final boolean modifiable,
                                             final boolean tmp, final boolean choixObligatoire, final String choix,
                                             final boolean masque, final String description, final int align) {
        return new ObjColTable(champ, lib, width, type, champSecondaire, lib2, modifiable, tmp, choixObligatoire, choix,
                masque, description, align, null);
    }

    public static ColTable createObjColTable(final String champ, final String lib, final int width) {
        return new ColTableBuilder(champ, lib).withWidth(width).create();
    }

    public ColTable create() {
        return new ObjColTable(champ, lib, largeur, type, champSecondaire, lib2, modifiable, tmp, choixObligatoire,
                choix,
                masque, description, align, element);
    }

    public ColTableBuilder withChamps(final String champ) {
        this.champ = champ;
        return this;
    }

    public ColTableBuilder withLib(final String lib) {
        this.lib = lib;
        return this;
    }

    public ColTableBuilder withElement(final IData element) {
        this.element = element;
        return this;
    }
}
