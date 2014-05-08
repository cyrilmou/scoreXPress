package fr.cm.scorexpress.model;

import fr.cm.common.widget.combobox.ComboListener;
import fr.cm.common.widget.combobox.ComboModel;
import fr.cm.scorexpress.core.model.ObjBalise;

import static fr.cm.scorexpress.core.model.Balise.*;
import static java.util.Arrays.asList;

public class BaliseModel {
    private       ObjBalise  balise;
    private final ComboModel typeBaliseComboModel;

    public BaliseModel(final ObjBalise balise) {
        this.balise = balise;
        typeBaliseComboModel = createCombo();
    }

    private ComboModel createCombo() {
        final ComboModel<String> model = new ComboModel<String>();
        final String[] typeBalises = {TYPE_PAS_OBLIGATOIRE, TYPE_OBLIGATOIRE, TYPE_BONUS, TYPE_ORDONNEE, TYPE_PENALITY};
        model.setItems(asList(typeBalises));
        model.addWidgetListener(new ComboListener<String>() {
            @Override
            public void onChange() {
            }

            @Override
            public void onSelectionChange(final String item) {
                balise.setType(item);
            }

            @Override
            public void onActivate() {
            }
        });
        return model;
    }

    public ComboModel getTypeBaliseComboModel() {
        return typeBaliseComboModel;
    }

    @Deprecated
    public void setBalise(final ObjBalise balise) {
        this.balise = balise;
    }
}
