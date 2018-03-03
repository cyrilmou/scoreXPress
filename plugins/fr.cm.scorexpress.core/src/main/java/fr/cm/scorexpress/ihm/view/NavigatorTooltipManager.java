package fr.cm.scorexpress.ihm.view;

import fr.cm.scorexpress.core.model.*;
import fr.cm.scorexpress.core.model.impl.ObjStep;
import fr.cm.scorexpress.core.model.impl.StepUtils;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;

import static fr.cm.scorexpress.core.model.impl.ObjStep.VAR_DESCRIPTION;
import static fr.cm.scorexpress.core.util.PenalityUtils.getPenalityTypeDescription;
import static fr.cm.scorexpress.ihm.editor.i18n.Message.i18n;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.eclipse.swt.SWT.MouseExit;
import static org.eclipse.swt.SWT.MouseMove;

public class NavigatorTooltipManager implements Listener {
    private final Tree tree;

    NavigatorTooltipManager(final Tree tree) {
        this.tree = tree;

    }

    public static void applyTooltipNavigator(final Tree tree) {

        tree.addListener(MouseMove, new NavigatorTooltipManager(tree));
        tree.addListener(MouseExit, new Listener() {
                             @Override
                             public void handleEvent(final Event event) {
                                 tree.setToolTipText(EMPTY);
                             }
                         }
        );
    }

    private static String createToolTip(final ObjStep step) {
        final StringBuilder toolTip = new StringBuilder();
        toolTip.append(step.getLib());

        final String description = step.getInfoStr(VAR_DESCRIPTION);
        if (description != null && !description.equals(EMPTY)) {
            toolTip.append('\n').append("Description: ").append(description);
        }

        updateStartStopBalise(step, toolTip);

        for (final ObjPenalite penality : step.getPenalites()) {
            toolTip.append('\n').append(getPenalityTypeDescription(penality, i18n("Tooltip.Penality"),
                    i18n("Tooltip.Scale"), i18n("Tooltip.Maxi"), i18n("Tooltip.NbBalise"),
                    i18n("Tooltip.NbBaliseMissed"), i18n("Tooltip.Parcours"), i18n("Tooltip.NbBaliseMore")));
        }

        updateBaliseType(step, i18n("Tooltip.mandatory.balise"), Balise.TYPE_OBLIGATOIRE, toolTip);
        updateBaliseType(step, i18n("Tooltip.bonus.balise"), Balise.TYPE_BONUS, toolTip);
        updateBaliseType(step, i18n("Tooltip.optional.balise"), Balise.TYPE_PAS_OBLIGATOIRE, toolTip);
        updateBaliseType(step, i18n("Tooltip.ordered.balise"), Balise.TYPE_ORDONNEE, toolTip);

        return toolTip.toString();
    }

    private static void updateStartStopBalise(final ObjStep step, final StringBuilder toolTip) {
        final String start = step.getBaliseDepart();
        final String end   = step.getBaliseArrivee();

        if (step.isEpreuve() && step.isCumulerSousEtape()) {
            toolTip.append('\n').append(i18n("Tooltip.epreuveDesc")).append(": ");
            for (final Step subStep : step.getSteps()) {
                toolTip.append("\n  + ").append(subStep.getLib());
            }
        }else{
            toolTip.append('\n').append(i18n("Tooltip.epreuveDesc")).append(": ");
            if (!step.isEpreuve()) {
                if (start == null && end == null) {
                    toolTip.append(i18n("Tooltip.betweenFirstAndEndPoint"));
                } else {
                    toolTip.append(start == null ? i18n("Tooltip.start") : start).append(" -> ")
                            .append(end == null ? i18n("Tooltip.end") : end);
                }
            }
        }
    }

    private static void updateBaliseType(final AbstractBalises step, final String desc, final String baliseType,
                                         final StringBuilder toolTip) {
        final String balise = StepUtils.getConfiguredBalises(step, baliseType);
        if (balise != null && !balise.equals(EMPTY)) {
            toolTip.append('\n').append(desc).append(": ").append(balise);
        }

        updateBaliseType2(step, desc, baliseType, toolTip);
    }

    private static void updateBaliseType2(final AbstractBalises step, final String desc, final String baliseType,
                                          final StringBuilder toolTip) {
        final String balise = StepUtils.getConfiguredBalises2(step, baliseType);
        if (balise != null && !balise.equals(EMPTY)) {
            toolTip.append('\n').append(desc).append(": ").append(balise);
        }
    }

    @Override
    public void handleEvent(final Event event) {
        final Point pt      = new Point(event.x, event.y);
        final Item  item    = tree.getItem(pt);
        String      toolTip = EMPTY;
        if (item != null) {
            final Object itemData = item.getData();
            if (itemData instanceof ObjManifestation) {
                final ObjManifestation manif = (ObjManifestation) itemData;
                toolTip = manif.getFileName();
            } else if (itemData instanceof ObjStep) {
                toolTip = createToolTip((ObjStep) itemData);
            }
        }
        tree.setToolTipText(toolTip);
    }
}
