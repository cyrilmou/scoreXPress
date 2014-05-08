package fr.cm.scorexpress.ihm.table;

import fr.cm.scorexpress.core.model.*;
import static fr.cm.scorexpress.core.model.ColTableUtils.isBooleanType;
import fr.cm.scorexpress.ihm.editor.modifier.AbstractGetInfoCellModifier;
import fr.cm.scorexpress.ihm.editor.modifier.BooleanCellEditor;
import fr.cm.scorexpress.ihm.editor.modifier.MyComboBoxCellEditor;
import fr.cm.scorexpress.ihm.editor.modifier.MyTextCellEditor;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;

import java.util.Collection;
import java.util.Iterator;

public class TableModifier<T extends IData> {
    private final TableViewer viewer;
    private final AbstractCategories manif;
    private final ObjConfig config;

    private TableModifier(final TableViewer viewer, final AbstractCategories manif, final ObjConfig config) {
        this.viewer = viewer;
        this.manif = manif;
        this.config = config;
        initModifier();
    }

    public static <D extends IData> TableModifier<D> createTableModifier(final TableViewer viewer,
                                                                         final ObjManifestation manif,
                                                                         final ObjConfig config) {
        return new TableModifier<D>(viewer, manif, config);
    }

    private void initModifier() {
        final String[] prop = new String[config.getColTable().size()];
        final CellEditor[] editors = new CellEditor[prop.length];
        int i = 0;
        for (Iterator<ColTable> iter = config.getColTable().iterator(); iter.hasNext(); i++) {
            final ColTable colTable = iter.next();
            prop[i] = colTable.getChamp();
            if (isBooleanType(colTable)) {
                final CellEditor editor = new BooleanCellEditor(viewer.getTable(), SWT.CHECK);
                editors[i] = editor;
            } else if (colTable.getChoix() != null) {
                final String choix = colTable.getChoix();
                editors[i] = new MyComboBoxCellEditor(viewer.getTable(), generateChoiceList(manif, choix));

            } else {
                editors[i] = new MyTextCellEditor(viewer.getTable());
            }
        }
        viewer.setColumnProperties(prop);
        viewer.setCellModifier(new AbstractGetInfoCellModifier<T>(viewer, config));
        viewer.setCellEditors(editors);
    }

    private static String[] generateChoiceList(final AbstractCategories manif, final String choix) {
        if (choix == null) {
            return new String[0];
        }
        String[] datas = new String[0];
        if (StringUtils.equals(IDossards.CHOIX_CATEGORIES, choix)) {
            final Collection<ObjCategorie> cats = manif.getCategories();
            datas = new String[cats.size()];
            int j = 0;
            for (Iterator<ObjCategorie> iterator = cats.iterator(); iterator.hasNext(); j++) {
                final ObjCategorie element = iterator.next();
                datas[j] = element.getNom();
            }
        }
        return datas;
    }

}
