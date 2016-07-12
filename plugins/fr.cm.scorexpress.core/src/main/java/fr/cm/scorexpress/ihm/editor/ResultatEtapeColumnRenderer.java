package fr.cm.scorexpress.ihm.editor;

import fr.cm.common.widget.table.TableColumnRenderer;
import fr.cm.scorexpress.core.model.AbstractGetInfo;
import fr.cm.scorexpress.core.model.ColTable;
import fr.cm.scorexpress.core.model.ObjResultat;
import fr.cm.scorexpress.core.model.impl.Date2;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;

import static fr.cm.scorexpress.ihm.editor.i18n.Message.i18n;

class ResultatEtapeColumnRenderer extends TableColumnRenderer<ObjResultat> {
    private final ColTable           colTable;
    private final ResultatEtapeModel model;

    ResultatEtapeColumnRenderer(final ColTable colTable, final ResultatEtapeModel model) {
        this.colTable = colTable;
        this.model = model;
    }

    @Override
    public String getToolTipText(final Object element) {
        final ObjResultat resultat = (ObjResultat) element;

        final StringBuilder builder = new StringBuilder();

        builder.append(resultat.getDossard().getNum()).append(". ").append(resultat.getDossard().getInfoStr("S.FIRSTNAME"));

        addInfo(i18n("Result.tooltip.finalTime"), ObjResultat.VAR_RESULTAT_TEMPS, resultat, builder);
        builder.append("\n-----");
        addInfoDate(i18n("Result.tooltip.chronoTime"), ObjResultat.VAR_TEMPS_CHRONO, resultat, builder);
        if (!resultat.getTempsArretChronoResultat().isNull()) {
            builder.append(" = ").append(resultat.getInfo(ObjResultat.VAR_TEMPSPARCOURS)).append(" ").append(resultat.getTempsArretChronoResultat());
        }
        addInfoDate(i18n("Result.tooltip.chronoMini"), ObjResultat.VAR_TEMPS_CHRONO_MINI, resultat, builder);
        addInfoDate(i18n("Result.tooltip.bonusTime"), ObjResultat.VAR_BONIFICATION, resultat, builder);
        addInfoDate(i18n("Result.tooltip.otherPenalityTime"), ObjResultat.VAR_PENALITE_AUTRE, resultat, builder);
        addInfoDate(i18n("Result.tooltip.penalityTime"), ObjResultat.VAR_PENALITE_BALISE, resultat, builder);
        builder.append("\n-----");
        addInfo(i18n("Result.tooltip.nbBalise"), ObjResultat.VAR_NB_BALISE, resultat, builder);
        addInfo(i18n("Result.tooltip.nbPenaliteBalise"), ObjResultat.VAR_NB_PENALITE, resultat, builder);
        addInfo(i18n("Result.tooltip.nbBaliseBonus"), ObjResultat.VAR_NB_BALISE_BONUS, resultat, builder);
        addInfo(i18n("Result.tooltip.missingBalise"), ObjResultat.VAR_RESULTAT_BALISESMANQUEES, resultat, builder);
        addInfo(i18n("Result.tooltip.baliseList"), ObjResultat.VAR_RESULTAT_BALISES_OK, resultat, builder);
        addInfo(i18n("Result.tooltip.baliseBonus"), ObjResultat.VAR_RESULTAT_BALISESBONUS, resultat, builder);
        addInfo(i18n("Result.tooltip.baliseDisordered"), ObjResultat.VAR_RESULTAT_BALISE_DISORDERED, resultat, builder);

        if(model.isHideTooltip())
            return null;
        return builder.toString();
    }

    @Override
    public boolean useNativeToolTip(final Object object) {
        return false;
    }

    @Override
    public Color getToolTipBackgroundColor(final Object object) {
        return new Color(Display.getCurrent(), 138, 191, 206);
    }

    @Override
    public Font getToolTipFont(final Object object) {
        return new Font(Display.getCurrent(), new FontData("Courier", 10, SWT.BOLD));
    }

    @Override
    public int getToolTipDisplayDelayTime(final Object object) {
        return 3000;
    }

    @Override
    public Color getBackground(final Object element) {
        final ObjResultat resultat = (ObjResultat) element;
        if(resultat.isTriche()){
            return new Color(Display.getCurrent(), 255, 128, 0);
        }
        if (resultat.isError() && model.isSignalError()) {
            resultat.showErrors();
            return new Color(Display.getCurrent(), 255, 0, 0);
        }
        if (colTable.isTmp()) {
            return new Color(Display.getCurrent(), 255, 255, 160);
        } else {
            return getBackgroundColorFromSelection(resultat);
        }
    }

    private Color getBackgroundColorFromSelection(final ObjResultat resultat) {
        final int index = model.matchSelection(resultat);
        if (index != -1) {
            final int red = 75 * index % 255;
            final int blue = (91 * index + (index != 0 ? 100 : 0)) % 255;
            final int green = 255 - (133 * index + (index != 0 ? 30 : 255)) % 255;
            return new Color(Display.getCurrent(), red, green, blue);
        } else {
            return null;
        }
    }

    @Override
    public Color getForeground(final Object element) {
        final ObjResultat resultat = (ObjResultat) element;
        if (resultat.isError() && model.isSignalError()) {
            return new Color(Display.getCurrent(), 255, 255, 255);
        } else {
            return null;
        }
    }

    @Override
    public String getColumnText(final ObjResultat element) {
        return element.getInfoStr(colTable.getChamp());
    }

    @Override
    public int compare(final ObjResultat elem1, final ObjResultat elem2) {
        if (elem1.isAbandon() != elem2.isAbandon()) {
            if (elem1.isAbandon()) {
                return +1;
            }
            return -1;
        }
        if (elem1.isDeclasse() != elem2.isDeclasse()) {
            if (elem1.isDeclasse()) {
                return +1;
            }
            return -1;
        }
        if (elem1.isHorsClassement() != elem2.isHorsClassement()) {
            if (elem1.isHorsClassement()) {
                return +1;
            }
            return -1;
        }
        return super.compare(elem1, elem2);
    }

    private static void addInfo(final String label, final String attribute, final AbstractGetInfo resultat, final StringBuilder builder) {
        final String element = resultat.getInfoStr(attribute);
        if (!element.isEmpty()) {
            if (element.length() > 80) {
                builder.append("\n  ").append(label).append("\n    ");
                int length = 0;
                for (final String split : element.split("]")) {
                    length += split.length();
                    if (length < 80) {
                        builder.append(split).append(']');
                    } else {
                        length = split.length();
                        builder.append("\n    ").append(split).append(']');
                    }
                }
            } else {
                builder.append("\n  ").append(label).append(" ").append(element);
            }
        }
    }

    private static void addInfoDate(final String label, final String info, final AbstractGetInfo resultat, final StringBuilder builder) {
        final Date2 date = (Date2) resultat.getInfo(info);
        if (date != null && !date.isNull()) {
            builder.append("\n  ").append(label).append(" ").append(date);
        }
    }
}
