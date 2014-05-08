package fr.cm.scorexpress.ihm.editor.page;

import fr.cm.common.widget.button.ButtonAdapter;
import fr.cm.common.widget.button.ButtonModel;
import fr.cm.common.widget.composite.FormModel;
import fr.cm.scorexpress.core.model.*;
import fr.cm.scorexpress.core.model.impl.ObjStep;
import static fr.cm.scorexpress.core.model.impl.StepFactory.createStep;
import fr.cm.scorexpress.model.StepModel;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;

public class ParametrageStepPageModel implements PropertyChangeListener {
    private final StepModel stepModel;
    private final boolean dirty = false;
    private final FormModel title = new FormModel("");
    private final ButtonModel addStepButtonModel;
    private final ButtonModel removeStepButtonModel;
    private TreeViewer viewer = null;
    private Object selectedTreeElement = null;
    private final ButtonModel addPenalityButtonModel;
    private final ButtonModel addBaliseButtonModel;

    public ParametrageStepPageModel(final StepModel stepModel) {
        this.stepModel = stepModel;
        addStepButtonModel = createAddStepButton();
        addPenalityButtonModel = createAddPenalityButton();
        addBaliseButtonModel = createAddBaliseButton();
        removeStepButtonModel = createRemoveButton();
        stepModel.getStep().addPropertyChangeListener(this);
    }

    private ButtonModel createAddStepButton() {
        final ButtonModel button = new ButtonModel("");
        button.addWidgetListener(new ButtonAdapter() {
            public void click() {
                super.click();
                if (selectedTreeElement == null) {
                    return;
                }
                if (selectedTreeElement instanceof AbstractSteps) {
                    ((AbstractSteps) selectedTreeElement).addStep(createStep("new"));
                }
            }
        });
        return button;
    }

    private ButtonModel createAddPenalityButton() {
        final ButtonModel button = new ButtonModel("");
        button.addWidgetListener(new ButtonAdapter() {
            public void click() {
                super.click();
                if (selectedTreeElement == null) {
                    return;
                }
                if (selectedTreeElement instanceof ObjStep) {
                    ((ObjStep) selectedTreeElement).addPenalite(new ObjPenalite("new"));
                }
            }
        });
        return button;
    }

    private ButtonModel createAddBaliseButton() {
        final ButtonModel button = new ButtonModel("");
        button.addWidgetListener(new ButtonAdapter() {
            public void click() {
                super.click();
                if (selectedTreeElement == null) {
                    return;
                }
                if (selectedTreeElement instanceof ObjStep) {
                    ((AbstractBalises) selectedTreeElement)
                            .addBalise(new ObjBalise("31", Balise.TYPE_PAS_OBLIGATOIRE, ""));
                }
            }
        });
        return button;
    }

    public StepModel getStepModel() {
        return stepModel;
    }

    public void dispose() {
    }

    public boolean isDirty() {
        return dirty;
    }

    public Iterable<ObjPenalite> getDescendentPenalities() {
        return getDescendentPenalities(stepModel.getStep());
    }

    private ButtonModel createRemoveButton() {
        final ButtonModel button = new ButtonModel("");
        button.addWidgetListener(new ButtonAdapter() {
            public void click() {
                super.click();
                if (selectedTreeElement == null) {
                    return;
                }
                for (final Object selectedElement : ((IStructuredSelection) viewer.getSelection()).toList()) {
                    if (selectedElement instanceof ObjStep) {
                        final ObjStep selectedStep = (ObjStep) selectedElement;
                        ((AbstractSteps) selectedStep.getParent()).removeStep(selectedStep);
                    } else if (selectedElement instanceof ObjPenalite) {
                        final ObjPenalite selectedPenality = (ObjPenalite) selectedElement;
                        selectedPenality.getParent().removePenalite(selectedPenality);
                    } else if (selectedElement instanceof ObjBalise) {
                        final ObjBalise selectedBalise = (ObjBalise) selectedElement;
                        ((AbstractBalises) selectedBalise.getParent()).removeBalise(selectedBalise);
                    }
                }
            }
        });
        return button;
    }

    private static Collection<ObjPenalite> getDescendentPenalities(final ObjStep step) {
        final Collection<ObjPenalite> res = new ArrayList<ObjPenalite>();
        for (final ObjStep s : step.getSteps()) {
            res.addAll(getDescendentPenalities(s));
        }
        if (!step.getPenalites().isEmpty()) {
            res.addAll(step.getPenalites());
        }
        return res;
    }

    public FormModel getTitle() {
        return title;
    }

    public ButtonModel getAddStepButtonModel() {
        return addStepButtonModel;
    }

    public ButtonModel getRemoveStepButtonModel() {
        return removeStepButtonModel;
    }

    public void setViewer(final TreeViewer viewer) {
        this.viewer = viewer;
        viewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(final SelectionChangedEvent event) {
                selectedTreeElement = ((IStructuredSelection) event.getSelection()).getFirstElement();
            }
        });
    }

    public void propertyChange(final PropertyChangeEvent event) {
        if (viewer != null && !viewer.getTree().isDisposed()) {
            System.out.println("event: " + event.getPropertyName() + " - " + event.getSource());
            if (event.getOldValue() == null) {
                Display.getDefault().syncExec(new Runnable() {
                    public void run() {
                        viewer.refresh();
                    }
                });
            } else {
                viewer.refresh(event.getSource());
            }
        }
    }

    public ButtonModel getAddPenalityButtonModel() {
        return addPenalityButtonModel;
    }

    public ButtonModel getAddBaliseButtonModel() {
        return addBaliseButtonModel;
    }
}
