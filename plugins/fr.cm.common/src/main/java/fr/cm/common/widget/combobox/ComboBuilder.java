package fr.cm.common.widget.combobox;

import fr.cm.common.widget.CommonControlBuilder;
import fr.cm.common.widget.MyToolkit;
import static fr.cm.common.widget.SwtUtil.proxySwt;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;

public class ComboBuilder<T> extends CommonControlBuilder<CCombo, ComboBuilder<T>> {
    private final ComboViewer viewer;
    private final ComboModel<T> model;

    ComboBuilder(final MyToolkit toolkit, final Composite parent, final ComboModel<T> model, final int style) {
        super(toolkit, toolkit.createComboBox(parent, style));
        viewer = new ComboViewer(control);
        this.model = model;

        final ModifyListener modifyListener = new ModifyListener() {
            public void modifyText(final ModifyEvent e) {
                if (!control.getText().equals(model.getText())) {
                    // model.modifyText(control.getText());
                }
                // System.out.println("modifyText " + control.getText());
            }
        };
        final ISelectionChangedListener sectionChangedListener = new ISelectionChangedListener() {
            public void selectionChanged(final SelectionChangedEvent event) {
                final Object selectedElement = ((IStructuredSelection) event.getSelection()).getFirstElement();
                if (selectedElement != null) {
                    model.selectionChanged((T) selectedElement);
                    model.modifyText(((ILabelProvider) viewer.getLabelProvider()).getText(selectedElement));
                } else {
                    model.modifyText(control.getText());
                }
            }
        };
        viewer.addSelectionChangedListener(sectionChangedListener);
        control.addModifyListener(modifyListener);

        this.model.addStateListener(
                proxySwt(
                        new ComboStateListener() {
                            public void textChange() {
                                control.setText(model.getText());
                            }

                            public void dataChange() {
                                control.removeModifyListener(modifyListener);
                                viewer.refresh();
                                control.addModifyListener(modifyListener);
                            }

                            public void onSelection() {
                                if (model.getIndexSelection() != -1) {
                                    viewer.removeSelectionChangedListener(sectionChangedListener);
                                    control.removeModifyListener(modifyListener);
                                    control.select(model.getIndexSelection());
                                    control.addModifyListener(modifyListener);
                                    viewer.addSelectionChangedListener(sectionChangedListener);
                                }
                            }

                            public void onEnable(final boolean enable) {
                                control.setEnabled(enable);
                            }
                        }));
        viewer.setContentProvider(
                new ArrayContentProvider() {
                    public Object[] getElements(final Object inputElement) {
                        return model.getItems().toArray();
                    }
                });
        viewer.setInput(model);
    }

    public static <T> ComboBuilder<T> createCombo(
            final MyToolkit toolkit, final Composite parent, final ComboModel<T> model, final int style) {
        return new ComboBuilder<T>(toolkit, parent, model, style);
    }

    public ComboBuilder<T> withRenderer(final ComboLabelProvider<T> renderer) {
        viewer.setLabelProvider(renderer);
        viewer.setComparator(
                new ViewerComparator() {
                    @SuppressWarnings("unchecked")
                    public int compare(final Viewer viewer, final Object e1, final Object e2) {
                        return model.compare(renderer, (T) e1, (T) e2);
                    }

                    public boolean isSorterProperty(final Object element, final String property) {
                        return super.isSorterProperty(element, property);
                    }
                });
        viewer.refresh();
        return this;
    }
}
