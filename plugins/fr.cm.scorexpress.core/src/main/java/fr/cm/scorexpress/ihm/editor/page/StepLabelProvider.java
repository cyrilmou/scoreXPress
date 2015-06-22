package fr.cm.scorexpress.ihm.editor.page;

import fr.cm.scorexpress.core.model.Balise;
import fr.cm.scorexpress.core.model.ObjBalise;
import fr.cm.scorexpress.core.model.ObjPenalite;
import fr.cm.scorexpress.core.model.impl.ObjStep;
import fr.cm.scorexpress.core.util.PenalityUtils;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import static fr.cm.scorexpress.ihm.application.ImageReg.getImg;
import static fr.cm.scorexpress.ihm.application.ScoreXPressPlugin.*;
import static org.apache.commons.lang.StringUtils.EMPTY;

public class StepLabelProvider implements ILabelProvider {
    private final Image imgArretChrono = getImg(IMG_ARRETCHRONO);
    private final Image imgDisable     = getImg(IMG_PENALITY_DESACTIVATE);
    private final Image imgEtape       = getImg(IMG_ETAPE);
    private final Image imgPenalite    = getImg(IMG_PENALITY);
    private final Image imgBalise      = getImg(IMG_BALISE);

    @Override
    public void addListener(final ILabelProviderListener listener) {
    }

    @Override
    public void dispose() {
    }

    @Override
    public Image getImage(final Object element) {
        if (element instanceof ObjStep) {
            final ObjStep etape = (ObjStep) element;
            if (!etape.isActif()) {
                return imgDisable;
            }
            if (etape.isArretChrono()) {
                return imgArretChrono;
            } else {
                return imgEtape;
            }
        } else if (element instanceof ObjPenalite) {
            if (((ObjPenalite) element).isActivate()) {
                return imgPenalite;
            } else {
                return imgDisable;
            }
        } else if (element instanceof StepDetailsBlockPage.Balises) {
            return imgBalise;
        } else if (element instanceof ObjBalise) {
            return imgBalise;
        }
        return null;
    }

    @Override
    public String getText(final Object element) {
        if (element instanceof ObjPenalite) {
            final ObjPenalite penality = (ObjPenalite) element;
            return penality.getLib() + " (" + PenalityUtils.getTypePenaliteStr(penality) + ')';
        } else if (element instanceof ObjStep) {
            final ObjStep etape = (ObjStep) element;
            if (!etape.isEpreuve()) {
                String message = " [";
                if (etape.getBaliseDepart() == null) {
                    message += ".. ";
                } else {
                    message += etape.getBaliseDepart() + ' ';
                }
                if (etape.getBaliseArrivee() == null) {
                    message += "..]";
                } else {
                    message += etape.getBaliseArrivee() + ']';
                }
                return etape.getLib() + message;
            }
            return etape.getLib();
        } else if (element instanceof Balise) {
            final Balise balise = (Balise) element;
            return balise.getNum() + ". " + balise.getType() + '(' + balise.getPenalite() + ')';
        } else if (element instanceof StepDetailsBlockPage.Balises) {
            return ((StepDetailsBlockPage.Balises) element).getLib();
        }
        return EMPTY;
    }

    @Override
    public boolean isLabelProperty(final Object element, final String property) {
        return false;
    }

    @Override
    public void removeListener(final ILabelProviderListener listener) {
    }
}
