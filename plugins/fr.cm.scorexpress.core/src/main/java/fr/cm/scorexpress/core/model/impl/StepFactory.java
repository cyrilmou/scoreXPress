package fr.cm.scorexpress.core.model.impl;

public class StepFactory {
    private StepFactory() {
    }

    public static ObjStep createStep(final String lib) {
        return new ObjStep(lib);
    }

    public static ObjStep createStep(final String order, final String lib) {
        return new ObjStep(order, lib);
    }

    public static ObjStep createStep(final String order, final String lib, final String numero) {
        return new ObjStep(order, lib, numero);
    }
}
