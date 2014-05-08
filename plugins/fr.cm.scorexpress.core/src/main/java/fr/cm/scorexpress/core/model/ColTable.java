package fr.cm.scorexpress.core.model;

import java.io.Serializable;

public interface ColTable extends Comparable<ColTable>, Serializable, ElementModel {
    public String getChamp();

    public String getChoix();

    public boolean isChoixObligatoire();

    public String getDescription();

    public String getLib();

    public String getLib2();

    public boolean isMasque();

    public int getWidth();

    public int getAlign();

    public ColTableType getType();

    public boolean isModifiable();

    public boolean isTmp();

    public IData getElement();

    public String getChampSecondaire();
}
