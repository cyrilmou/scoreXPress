package fr.cm.scorexpress.ihm.view.dnd;

import fr.cm.scorexpress.core.model.impl.ObjStep;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;

public class StepTransfer2 extends Transfer {
    private static final StepTransfer2 instance  = new StepTransfer2();
    private static final String        TYPE_NAME = "step-java-transfer-format";
    private static final int           TYPEID    = registerType(TYPE_NAME);
    private ObjStep step;

    public static StepTransfer2 getStepTransfer() {
        return instance;
    }

    private StepTransfer2() {
    }


    @Override
    public TransferData[] getSupportedTypes() {
        final int[] types = getTypeIds();
        final TransferData[] data = new TransferData[types.length];
        for (int i = 0; i < types.length; i++) {
            data[i] = new TransferData();
            data[i].type = types[i];
        }
        return data;
    }

    @Override
    public boolean isSupportedType(final TransferData transferData) {
        if (transferData == null) {
            return false;
        }
        final int[] types = getTypeIds();
        for (final int type : types) {
            if (transferData.type == type) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected int[] getTypeIds() {
        return new int[]{TYPEID};
    }

    @Override
    protected String[] getTypeNames() {
        return new String[]{TYPE_NAME};
    }

    @Override
    protected void javaToNative(final Object object, final TransferData transferData) {
        step = (ObjStep) object;
    }

    @Override
    protected Object nativeToJava(final TransferData transferData) {
        return step;
    }

}
