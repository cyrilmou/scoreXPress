package fr.cm.scorexpress.model;

import com.google.common.base.Predicates;
import fr.cm.common.widget.button.ButtonAdapter;
import fr.cm.common.widget.button.ButtonModel;
import fr.cm.common.widget.label.LabelModel;
import fr.cm.common.widget.table.TableModel;
import fr.cm.common.widget.text.TextModel;
import fr.cm.common.widget.text.TextModifyAdapter;
import fr.cm.scorexpress.core.model.ObjManifestation;
import fr.cm.scorexpress.core.model.impl.ObjStep;
import fr.cm.scorexpress.ihm.application.ImageReg;
import fr.cm.scorexpress.ihm.application.ScoreXPressPlugin;
import org.eclipse.swt.graphics.Image;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static fr.cm.scorexpress.core.model.impl.StepFactory.createStep;
import static fr.cm.scorexpress.ihm.editor.i18n.Message.i18n;

public class ManifModel {
    private final ObjManifestation    manif;
    private final LabelModel          nameLabelModel;
    private final TextModel           nameTextModel;
    private final ButtonModel         addStepButtonModel;
    private final TableModel<ObjStep> stepTableModel;
    private final ButtonModel         removeStepButtonModel;
    private static final List<String> selections = new ArrayList<String>();
    private static final TextModel<String> searchTextModel = createSearchTextModel();
    private              boolean      dirty      = false;

    public ManifModel(final ObjManifestation manif) {
        this.manif = manif;
        nameLabelModel = new LabelModel(i18n("ManifDetailsBlockPage.LIBELLE"));
        nameTextModel = createNameTextModel(manif);
        addStepButtonModel = createStepButtonModel();
        stepTableModel = createStepTableModel();
        removeStepButtonModel = createRemoveStepModel();

    }

    public ObjManifestation getManif() {
        return manif;
    }

    public String getTitle() {
        return i18n("ManifPage.CONFIGURATION_MANIF");
    }

    public Image getBackgroundImage() {
        return ImageReg.getImg(ScoreXPressPlugin.IMG_FORM_BG);
    }

    public void save() {
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(final boolean dirty) {
        this.dirty = dirty;
    }

    public boolean issaveAllowed() {
        return false;
    }

    public void dispose() {
    }

    public String getMasterSectionTitle() {
        return i18n("ManifDetailsBlockPage.CONFIGURATION");
    }

    public String getMasterSectionDescription() {
        return i18n("ManifDetailsBlockPage.INFOS");
    }

    public LabelModel getNameLabelModel() {
        return nameLabelModel;
    }

    public TextModel<Object> getNameTextModel() {
        return nameTextModel;
    }

    public ButtonModel getAddStepButtonModel() {
        return addStepButtonModel;

    }

    public TableModel<ObjStep> getStepTableModel() {
        return stepTableModel;
    }

    public ButtonModel getRemoveStepButtonModel() {
        return removeStepButtonModel;
    }

    private ButtonModel createStepButtonModel() {
        final ButtonModel buttonModel = new ButtonModel(i18n("ManifDetailsBlockPage.AJOUTER"));
        buttonModel.addWidgetListener(new ButtonAdapter() {
            public void click() {
                final ObjStep newStep = createStep("new Step");
                if (manif.addStep(newStep)) {
                    stepTableModel.addRow(newStep);
                    // stepTableModel.refilter();
                }
            }
        });
        return buttonModel;
    }

    private TextModel createNameTextModel(final ObjManifestation manif) {
        final TextModel textModel = new TextModel(manif.getNom() == null ? "" : manif.getNom());
        textModel.addModifyListener(new ManifNameModify(manif));
        return textModel;
    }

    private ButtonModel createRemoveStepModel() {
        final ButtonModel model = new ButtonModel(i18n("ManifDetailsBlockPage.SUPPRIMER"));
        model.addWidgetListener(new ButtonAdapter() {
            public void click() {
                for (final ObjStep step : stepTableModel.getSelection()) {
                    if (manif.removeStep(step)) {
                        stepTableModel.removeRow(step);
                        //stepTableModel.refilter();
                    }
                }
            }
        });

        return model;
    }

    private TableModel<ObjStep> createStepTableModel() {
        final TableModel<ObjStep> model = new TableModel<ObjStep>(Predicates.<ObjStep>alwaysTrue());
        model.addRows(manif.getSteps());
        model.addColumn("Etape").withAutoResize(true);
        return model;
    }

    public int matchSelection(final String num) {
        return selections.indexOf(num);
    }

    public static String getSelectionsStr() {
        final StringBuilder builder = new StringBuilder();
        final Iterator<String> iterator = selections.iterator();
        while (iterator.hasNext()) {
            final String selection = iterator.next();

            builder.append(selection);
            if (iterator.hasNext()) {
                builder.append(", ");
            }
        }
        return builder.toString();
    }

    public static void setSelectionsStr(final String selectionsStr) {
        selections.clear();
        for (final String element : selectionsStr.replace("\n", "").split(" +")) {
            if (!element.isEmpty()) {
                selections.add(element);
            }
        }
    }

    private static TextModel<String> createSearchTextModel() {
        final TextModel<String> model = new TextModel<String>(getSelectionsStr());
        model.addModifyListener(new TextModifyAdapter<String>() {
            @Override
            public void onModify(final String value) {
                model.setText(value);
            }

            @Override
            public void onExit() {
                super.onExit();
                setSelectionsStr(model.getText());
            }
        });
        return model;
    }

    public TextModel<String> getSearchTextModel() {
        return searchTextModel;
    }

    public boolean isFiltered() {
        return !selections.isEmpty();
    }

    private class ManifNameModify extends TextModifyAdapter {

        private final ObjManifestation manif;

        ManifNameModify(final ObjManifestation manif) {
            this.manif = manif;
        }

        public void onExit() {
            manif.setNom(nameTextModel.getText());
        }
    }
}
