package fr.cm.scorexpress.ihm.view;

import fr.cm.scorexpress.core.model.ObjManifestation;
import fr.cm.scorexpress.core.model.impl.ObjStep;
import static fr.cm.scorexpress.ihm.application.ImageReg.getImg;
import static fr.cm.scorexpress.ihm.application.ScoreXPressPlugin.*;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class NavigatorLabelProvider extends LabelProvider {
    private final Image etapeDesactiveeImage = getImg(IMG_DESACTIVATE);
    private final Image etapeImage = getImg(IMG_ETAPE);
    private final Image manifestationImage = getImg(IMG_MANIFESTATION);
    private final Image etapeNoChronos = getImg(IMG_NO_CHRONO);

    @Override
    public Image getImage(final Object element) {
        try {
            if (element instanceof ObjStep) {
                final ObjStep etape = (ObjStep) element;
                final Image image;
                if (etape.isActif()) {
                    if (etape.isArretChrono()) {
                        image = getImg(IMG_ARRETCHRONO);
                    } else {
                        if (etape.isEpreuve() && !etape.isCumulerSousEtape()
                                && etape.getUserChronos().isEmpty()) {
                            image = etapeNoChronos;
                        } else {
                            image = etapeImage;
                        }
                    }
                } else {
                    image = etapeDesactiveeImage;
                }
                return image;
            } else if (element instanceof ObjManifestation) {
                return manifestationImage;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.getImage(element);
    }

    @Override
    public String getText(final Object element) {
        if (element instanceof ObjManifestation) {
            return ((ObjManifestation) element).getNom();
        }
        if (element instanceof ObjStep) {
            return ((ObjStep) element).getLib();
        }
        return element.toString();
    }
}
