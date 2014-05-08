package fr.cm.scorexpress.ihm.view.dnd;

import fr.cm.scorexpress.core.model.ObjPenalite;
import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;

import java.io.*;

public class PenaliteTransfer extends ByteArrayTransfer {
    private static final PenaliteTransfer instance = new PenaliteTransfer();
    private static final String TYPE_NAME = "penality-transfer-format";
    private static final int TYPEID = registerType(TYPE_NAME);

    private PenaliteTransfer() {
    }

    public static PenaliteTransfer getInstance() {
        return instance;
    }

    protected static ObjPenalite fromByteArray(final byte[] bytes) {
        try {
            final ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes));
            return readStep(in);
        } catch (IOException e) {
            return null;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    protected int[] getTypeIds() {
        return new int[]{TYPEID};
    }

    protected String[] getTypeNames() {
        return new String[]{TYPE_NAME};
    }

    protected void javaToNative(final Object object, final TransferData transferData) {
        final byte[] bytes = toByteArray((ObjPenalite) object);
        if (bytes != null) {
            super.javaToNative(bytes, transferData);
        }
    }

    protected Object nativeToJava(final TransferData transferData) {
        final byte[] bytes = (byte[]) super.nativeToJava(transferData);
        return fromByteArray(bytes);
    }

    protected static byte[] toByteArray(final ObjPenalite penality) {
        final ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        byte[] bytes = null;
        try {
            final ObjectOutputStream out = new ObjectOutputStream(byteOut);
            /* write number of markers */
            writePenality(penality, out);
            out.close();
            bytes = byteOut.toByteArray();
        } catch (IOException e) {
            // when in doubt send nothing
            e.printStackTrace();
        }
        return bytes;
    }

    private static ObjPenalite readStep(final ObjectInputStream dataIn) throws IOException,
            ClassNotFoundException {
        return (ObjPenalite) dataIn.readObject();
    }

    private static void writePenality(final ObjPenalite penalite, final ObjectOutputStream dataOut) throws IOException {
        dataOut.writeObject(penalite);
    }
}
