package fr.cm.scorexpress.ihm.view.dnd;

import fr.cm.scorexpress.core.model.impl.ObjStep;
import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;

import java.io.*;

public class StepTransfer extends ByteArrayTransfer {
    private static final StepTransfer instance = new StepTransfer();
    private static final String TYPE_NAME = "step-transfer-format";
    private static final int TYPEID = registerType(TYPE_NAME);

    public static StepTransfer getStepTransfer() {
        return instance;
    }

    protected ObjStep fromByteArray(final byte[] bytes) {
        try {
            return readStep(new ObjectInputStream(new ByteArrayInputStream(bytes)));
        } catch (IOException e) {
            return null;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private StepTransfer() {
    }

    protected int[] getTypeIds() {
        return new int[]{TYPEID};
    }

    protected String[] getTypeNames() {
        return new String[]{TYPE_NAME};
    }

    protected void javaToNative(final Object object, final TransferData transferData) {
        final byte[] bytes = toByteArray((ObjStep) object);
        if (bytes != null)
            super.javaToNative(bytes, transferData);
    }

    protected Object nativeToJava(final TransferData transferData) {
        final byte[] bytes = (byte[]) super.nativeToJava(transferData);
        return fromByteArray(bytes);
    }

    private ObjStep readStep(final ObjectInputStream dataIn) throws IOException, ClassNotFoundException {
        return (ObjStep) dataIn.readObject();
    }

    protected byte[] toByteArray(final ObjStep step) {
        final ByteArrayOutputStream byteOut = new ByteArrayOutputStream();

        byte[] bytes = null;
        try {
            final ObjectOutputStream out = new ObjectOutputStream(byteOut);
            /* write number of markers */
            writeStep(step, out);
            out.close();
            bytes = byteOut.toByteArray();
        } catch (IOException e) {
            // when in doubt send nothing
            e.printStackTrace();
        }
        return bytes;
    }

    private void writeStep(final ObjStep step, final ObjectOutputStream dataOut) throws IOException {
        dataOut.writeObject(step);
    }
}
