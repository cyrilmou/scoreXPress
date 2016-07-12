package fr.cm.scorexpress.ihm.editor;

import com.google.common.base.Predicate;
import com.google.common.collect.Sets;
import fr.cm.common.widget.button.ButtonAdapter;
import fr.cm.common.widget.button.ButtonModel;
import fr.cm.common.widget.combobox.ComboListener;
import fr.cm.common.widget.combobox.ComboModel;
import fr.cm.common.widget.label.LabelModel;
import fr.cm.common.widget.table.TableModel;
import fr.cm.common.widget.text.TextModel;
import fr.cm.common.widget.text.TextModifyAdapter;
import fr.cm.scorexpress.applicative.IActualisationProject;
import fr.cm.scorexpress.applicative.ICalculResult;
import fr.cm.scorexpress.core.AutoResizeColumn;
import fr.cm.scorexpress.core.AutoResizeColumn.AutoResizeListener;
import fr.cm.scorexpress.core.model.ColTable;
import fr.cm.scorexpress.core.model.ColTableBuilder;
import fr.cm.scorexpress.core.model.IData;
import fr.cm.scorexpress.core.model.ObjConfig;
import fr.cm.scorexpress.core.model.ObjResultat;
import fr.cm.scorexpress.core.model.impl.ObjStep;
import fr.cm.scorexpress.core.model.impl.StepUtils;
import fr.cm.scorexpress.ihm.editor.i18n.Message;
import fr.cm.scorexpress.ihm.editor.i18n.Messages;
import fr.cm.scorexpress.model.ManifModel;
import fr.cm.scorexpress.model.StepModel;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;

import static com.google.common.collect.Lists.newArrayList;
import static fr.cm.scorexpress.applicative.CalculUpdater.createCalculUpdater;
import static fr.cm.scorexpress.applicative.ProjectManagerFactory.getAutoImportProcess;
import static fr.cm.scorexpress.core.model.ConfigType.*;
import static fr.cm.scorexpress.core.model.ObjDossard.VAR_DOSSARD_CATEGORIE;
import static fr.cm.scorexpress.core.model.ObjResultat.NR_ETAPE_SEPARATOR;
import static fr.cm.scorexpress.core.model.ObjResultat.PREFIX_RES_INTER;
import static fr.cm.scorexpress.core.model.impl.ObjStep.VAR_TITLE_PRINT;
import static fr.cm.scorexpress.core.util.CalculResultatsUtils.withCategory;
import static fr.cm.scorexpress.ihm.editor.i18n.Message.i18n;
import static org.apache.commons.lang.StringUtils.EMPTY;

public class ResultatEtapeModel implements PropertyChangeListener {
    private final TableModel<ObjResultat> tableModel;
    private final Predicate<ObjResultat>  filter;
    private final AutoResizeColumn        autoResizeContext;
    private final StepModel               stepModel;
    private final int                     mode;
    private final ManifModel              manifModel;
    private final ICalculResult           calculResult;
    private final LabelModel              infoLabel;
    private final LabelModel              titleLabelModel;
    private final ButtonModel categoryButtonModel       = new ButtonModel();
    private final ButtonModel configEtapeButtonModel    = new ButtonModel();
    private final ButtonModel configPenalityButtonmodel = new ButtonModel();
    private final ButtonModel               refreshButtonModel;
    private final ButtonModel               chronosButtonModel;
    private final TextModel<String>         printInfoTextModel;
    private final IActualisationProject     projectUpdateListener;
    private final ComboModel<String>        categoriesComboModel;
    private final ObjStep                   step;
    private final ButtonModel               filterBySearchModel;
    private final TextModifyAdapter<String> modifySearchTextListener;
    private boolean hideDeclasse     = false;
    private boolean hideAbandon      = false;
    private boolean hideTooltip      = false;
    private boolean dirty            = false;
    private boolean signalError      = false;
    private String  infoLabelContent = "";
    private String  infoLabelDelay   = "";
    private boolean byCategory       = false;

    public ResultatEtapeModel(final AutoResizeColumn autoResizeContext, final StepModel stepModel, final int mode,
                              final ManifModel manifModel) {
        this.autoResizeContext = autoResizeContext;
        this.stepModel = stepModel;
        this.mode = mode;
        this.manifModel = manifModel;
        step = stepModel.getStep();
        filter = createFilter();
        tableModel = createTableModel();
        filterBySearchModel = new ButtonModel("Filtre");
        filterBySearchModel.addWidgetListener(new ButtonAdapter() {
            @Override
            public void click() {
                tableModel.refilter();
            }
        });
        modifySearchTextListener = new TextModifyAdapter<String>() {
            @Override
            public void onExit() {
                tableModel.refilter();
            }
        };
        manifModel.getSearchTextModel().addModifyListener(modifySearchTextListener);
        autoResizeContext.addAutoResizeListener(new AutoResizeTableListener());
        calculResult = new UpdateResultat(step);
        titleLabelModel = createTitleLabelModel();
        chronosButtonModel = createChronoButtonModel(step);
        printInfoTextModel = createPrintInfoTextModel();
        refreshButtonModel = createRefreshButtonModel();
        categoriesComboModel = createCategoriesComboModel();
        categoryButtonModel.setSelection(true);
        categoryButtonModel.addWidgetListener(new ButtonAdapter() {
            @Override
            public void click() {
                byCategory = !byCategory;
                calculResult.updateFinish(calculResult.getStep());
            }

            @Override
            public void onActivate() {
            }
        });
        projectUpdateListener = new IActualisationProject() {
            @Override
            public void importChanged() {
                infoLabelDelay = " (...)";
                updateCalcul();
            }

            @Override
            public void waitingDelay(final int delay) {
                infoLabelDelay = " (" + delay / 1000 + ')';
                if (delay == 0) {
                    infoLabelDelay = " (...)";
                }
                updateInfoLabel();
            }
        };
        getAutoImportProcess().addActualisationListener(projectUpdateListener);
        stepModel.getStep().addPropertyChangeListener(this);
        infoLabel = createInfoLabelModel();
    }

    private static LabelModel createInfoLabelModel() {
        return new LabelModel("");
    }

    private static ButtonModel createChronoButtonModel(final ObjStep step) {
        final ButtonModel buttonModel = new ButtonModel();
        if (!step.isEpreuve() || step.isCumulerSousEtape()) {
            buttonModel.setEnable(false);
        }
        return buttonModel;
    }

    private static String getFilterCategory(final String category) {
        String filterCategory = category;
        if (!filterCategory.equals(EMPTY) && !filterCategory.equals("Tous")) {
            final String any = ".*";
            filterCategory = filterCategory.replaceAll("\\*", any);
            filterCategory = filterCategory.replaceAll("\\+", any + '|' + any);
            filterCategory = '(' + any + filterCategory + any + ')';

        }
        return filterCategory;
    }

    public TableModel<ObjResultat> getTableResultatModel() {
        return tableModel;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(final boolean dirty) {
        this.dirty = dirty;
    }

    public boolean isHideDeclasse() {
        return hideDeclasse;
    }

    public void setHideDeclasse(final boolean hideDeclasse) {
        this.hideDeclasse = hideDeclasse;
        tableModel.refilter();
    }

    public boolean isHideAbandon() {
        return hideAbandon;
    }

    public void setHideAbandon(final boolean hideAbandon) {
        this.hideAbandon = hideAbandon;
        tableModel.refilter();
    }

    public boolean isHideTooltip() {
        return hideTooltip;
    }

    public void setHideTooltip(boolean hideTooltip) {
        this.hideTooltip = hideTooltip;
    }

    public String getLabel() {
        final String info;
        switch (mode) {
            case 1:
                info = " (" + i18n("DETAILS") + ')';
                break;
            default:
                info = "";
        }
        return step.getLib() + info;
    }

    public ObjStep getStep() {
        return step;
    }

    public int getMode() {
        return mode;
    }

    public AutoResizeColumn getAutoResizeContext() {
        return autoResizeContext;
    }

    public Collection<String> getCategoriesData() {
        final Collection<String> categories = Sets.newHashSet();
        categories.add("Tous");
        final Iterable<ObjResultat> resultats = newArrayList(step.getResultats());
        for (final ObjResultat resultat : resultats) {
            final String cat = EMPTY + resultat.getDossard().getInfo(VAR_DOSSARD_CATEGORIE);
            if (StepUtils.isGoodCategorie(step, cat)) {
                categories.add(cat);
            }
        }
        return categories;
    }

    public boolean isSignalError() {
        return signalError;
    }

    public void setSignalError(final boolean signalError) {
        this.signalError = signalError;
        tableModel.refilter();
    }

    public ComboModel<String> getCategoriesComboboxModel() {
        return categoriesComboModel;
    }

    public ButtonModel getCategoryButtonModel() {
        return categoryButtonModel;
    }

    public ButtonModel getConfigEtapeButtonModel() {
        return configEtapeButtonModel;
    }

    public ButtonModel getConfigPenalityButtonModel() {
        return configPenalityButtonmodel;
    }

    public void dispose() {
        getAutoImportProcess().removeActualisationListener(projectUpdateListener);
        stepModel.getStep().removePropertyChangeListener(this);
        manifModel.getSearchTextModel().removeModifyListener(modifySearchTextListener);
    }

    public LabelModel getTitleLabelModel() {
        return titleLabelModel;
    }

    public TextModel<String> getPrintInfoTextModel() {
        return printInfoTextModel;
    }

    public ButtonModel getRefreshButtonModel() {
        return refreshButtonModel;
    }

    private ButtonModel createRefreshButtonModel() {
        final ButtonModel model = new ButtonModel();
        model.addWidgetListener(new ButtonAdapter() {
            @Override
            public void click() {
                super.click();
                updateCalcul();
            }
        });
        model.setEnable(false);
        return model;
    }

    public void updateCalcul() {
        if (!step.isEpreuve() || step.isCumulerSousEtape()) {
            chronosButtonModel.setEnable(false);
        }
        if( step.getUserChronos().isEmpty() && step.isEpreuve() && !step.isCumulerSousEtape()){
            chronosButtonModel.setSelection(true);
        }

        refreshButtonModel.setEnable(false);
        createCalculUpdater().update(calculResult);
        categoriesComboModel.setItems(getCategoriesData());
    }

    private void updateInfo() {
        int nbAbandon = 0;
        int nbTotal = 0;
        int nbDeclasse = 0;
        final int nbChronos = step.getUserChronos().size();
        for (final ObjResultat resultat : step.getResultats()) {
            if (resultat.getDossard().isAbandon()) {
                nbAbandon++;
            }
            if (resultat.getDossard().isDisqualifie()) {
                nbDeclasse++;
            }
            nbTotal++;
        }
        final StringBuilder builder = new StringBuilder(
                String.format("Total: %s, Abandons: %s, " + Message.i18n("DECLASSES") + ": %s", nbTotal, nbAbandon,
                              nbDeclasse));
        if (!step.isCumulerSousEtape()) {
            builder.append(", ").append(Message.i18n("CHRONO")).append(": ").append(nbChronos);
            builder.append(", ").append(Message.i18n("PUCES_NON_VIDEE")).append(": ").append(nbTotal - nbChronos);
        }
        infoLabelContent = builder.toString();
        updateInfoLabel();
        infoLabel.setTooltip(builder.toString());
    }

    private void updateInfoLabel() {
        infoLabel.setLabel(infoLabelContent + infoLabelDelay);
    }

    private TextModel<String> createPrintInfoTextModel() {
        final TextModel<String> model = new TextModel<String>(step.getInfoStr(VAR_TITLE_PRINT));
        model.addModifyListener(new TextModifyAdapter<String>() {
            @Override
            public void onExit() {
                super.onExit();
                step.setInfo(VAR_TITLE_PRINT, model.getText());
            }
        });
        return model;
    }

    private LabelModel createTitleLabelModel() {
        return new LabelModel(Messages.ResultatEtapeEditor_Classement + ' ' + getStep().getLib());
    }

    private Predicate<ObjResultat> createFilter() {
        return new Predicate<ObjResultat>() {
            @Override
            public boolean apply(final ObjResultat resultat) {
                if (filterBySearchModel != null && filterBySearchModel.isSelected() && matchSelection(resultat) ==
                        -1) {
                    return false;
                }
                return !(resultat.isAbandon() && hideAbandon || (resultat.isHorsClassement() || resultat.isDeclasse()
                ) && hideDeclasse);
            }
        };
    }

    public ButtonModel getChronosButtonModel() {
        return chronosButtonModel;
    }

    public ManifModel getManifModel() {
        return manifModel;
    }

    public StepModel getStepModel() {
        return stepModel;
    }

    @Override
    public void propertyChange(final PropertyChangeEvent event) {
        if (event.getSource() == stepModel.getStep() &&
                event.getPropertyName().equals(IData.VAR_CALCUL_DATA_MODIFIED)) {
            updateCalcul();
        }
    }

    public LabelModel getInfoLabel() {
        return infoLabel;
    }

    public int matchSelection(final ObjResultat resultat) {
        return manifModel.matchSelection(resultat.getDossard().getNum());
    }

    public TextModel<String> getSearchTextModel() {
        return manifModel.getSearchTextModel();
    }

    public ButtonModel getFilterBySearchModel() {
        return filterBySearchModel;
    }

    private ComboModel<String> createCategoriesComboModel() {
        final ComboModel<String> model = new ComboModel<String>();
        model.addWidgetListener(new ComboListener<String>() {
            @Override
            public void onChange() {
                // createCalculUpdater().update(calculResult, false);
                final Collection<ObjResultat> resultats =
                        step.getResultatsByEpreuve(withCategory(getFilterCategory(model.getText())), byCategory);
                tableModel.setRows(resultats);
                updateInfo();
            }

            @Override
            public void onSelectionChange(final String item) {
                // createCalculUpdater().update(calculResult, false);
                final Collection<ObjResultat> resultats =
                        step.getResultatsByEpreuve(withCategory(getFilterCategory(item)), byCategory);
                tableModel.setRows(resultats);
                categoriesComboModel.setItems(getCategoriesData());
                updateInfo();
            }

            @Override
            public void onActivate() {
            }
        });
        model.setItems(getCategoriesData());
        return model;
    }

    private TableModel<ObjResultat> createTableModel() {
        final TableModel<ObjResultat> tableModel = new TableModel<ObjResultat>(filter);
        for (final ColTable colTable : getColumnConfig()) {
            tableModel.addColumn(colTable.getChamp()).withAutoResize(true);
        }
        return tableModel;
    }

    public Collection<ColTable> getColumnConfig() {
        final Collection<ColTable> columns = newArrayList();
        final ObjConfig config;
        final ObjConfig advancedConfig;
        switch (mode) {
            case 1:
                config = step.getManif().getConfiguration().getConfig(RESULTATS_EXPERT);
                advancedConfig = step.getManif().getConfiguration().getConfig(RESULTATS_EXPERT_INTER);
                break;
            case 2:
                config = step.getManif().getConfiguration().getConfig(RESULTATS);
                advancedConfig = step.getManif().getConfiguration().getConfig(RESULTATS_INTER);
                break;
            default:
                config = step.getManif().getConfiguration().getConfig(RESULTATS);
                advancedConfig = null;
        }
        columns.addAll(config.getColTableAll());
        if (advancedConfig != null) {
            int nrEtapeInter = 0;
            for (final ObjStep stepInter : step.getStepsInter()) {
                for (final ColTable colTable : advancedConfig.getColTableAll()) {
                    final ColTableBuilder builder = new ColTableBuilder(colTable)
                            .withChamps(PREFIX_RES_INTER + nrEtapeInter + NR_ETAPE_SEPARATOR + colTable.getChamp())
                            .withLib(stepInter.getLib() + '/' + colTable.getLib()).temp();
                    columns.add(builder.create());
                }
                nrEtapeInter++;
            }
        }
        return columns;
    }

    private class AutoResizeTableListener implements AutoResizeListener {

        @Override
        public void autoResizeChanged(final boolean autoResize) {
            if (autoResize) {
                tableModel.autoResizeColumns();
                tableModel.removeEmptyColumn();
            }
        }

    }

    private class UpdateResultat implements ICalculResult {

        private final ObjStep step;

        private UpdateResultat(final ObjStep step) {
            this.step = step;
        }

        @Override
        public ObjStep getStep() {
            return step;
        }

        private String getCategorieSelected() {
            return getFilterCategory(categoriesComboModel.getText());
        }

        @Override
        public void updateFinish(final ObjStep step) {
            final Collection<ObjResultat> resultats =
                    step.getResultatsByEpreuve(withCategory(getCategorieSelected()), byCategory);
            tableModel.setRows(resultats);
            categoriesComboModel.setItems(getCategoriesData());
            refreshButtonModel.setEnable(true);
            updateInfo();
        }

    }
}
