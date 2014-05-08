package fr.cm.scorexpress.applicative;

import static javax.swing.JOptionPane.showInputDialog;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Licence implements Serializable {

    private static final long serialVersionUID = 5656993671308671705L;
    private String version;
    private Date limiteDateLicence;

    private static final transient DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private final Date dateCreation = new Date();
    public static final int MODE_DEMONSTRATION = 1;
    public static final int MODE_NORMAL = 2;

    private int mode = MODE_DEMONSTRATION;

    public Licence() {
    }

    Licence(final String version, final Date limiteDate, final int mode) {
        this.version = version;
        limiteDateLicence = limiteDate;
        this.mode = mode;
    }

    public void setLicenceDate(final String date) {
        try {
            limiteDateLicence = sdf.parse(date);
        } catch (Exception ex) {
        }
    }

    public static void main(final String[] arg) {
        try {
            final Licence licence = new Licence();
            final String date = showInputDialog("Creation d'une licence",
                    "12/02/2000");
            if (date == null)
                return;
            licence.setLicenceDate(date);
            licence.version = showInputDialog("Version", "Bêta release 6");
            if (licence.version == null)
                return;
            final String mode = showInputDialog("Mode", "1");
            if (mode == null)
                return;
            licence.mode = Integer.parseInt(mode);
            final FileOutputStream fou = new FileOutputStream("licence.lic");
            final ObjectOutputStream dout = new ObjectOutputStream(fou);
            dout.writeObject(licence);
            dout.flush();
            dout.close();
            System.out.println(licence);
            System.exit(0);
        } catch (IOException ex1) {
            ex1.printStackTrace();
        } catch (Exception e) {
        }
    }

    public static Licence getLicence(final String fileName) {
        final File f = new File(fileName);
        if (!f.exists()) {
            System.err.println(f.getAbsolutePath());
            return null;
        }
        try {
            System.out.println(f.getAbsolutePath());
            final FileInputStream fin = new FileInputStream(f);
            final ObjectInputStream din = new ObjectInputStream(fin);
            final Licence res = (Licence) din.readObject();
            din.close();
            return res;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public boolean isValide() {
        if (new Date().after(dateCreation) || new Date().equals(dateCreation)) {
            if (new Date().before(limiteDateLicence))
                return true;
        }
        return false;
    }

    public static boolean isLicenceValide(final String fileName) {
        final Licence licence = getLicence(fileName);
        if (licence != null) {
            if (new Date().after(licence.dateCreation)
                    || new Date().equals(licence.dateCreation)) {
                if (new Date().before(licence.limiteDateLicence))
                    return true;
            }
            System.err.println("Licence incorrect");
            return false;
        }
        System.err.println("Licence non trouvée " + fileName);
        return false;
    }

    public String getVersion() {
        return version;
    }

    public Date getLimiteDateLicence() {
        return limiteDateLicence;
    }

    public Date getDateCreation() {
        return dateCreation;
    }

    @Override
    public String toString() {
        return "Version \"" + version + "\" crée " + sdf.format(dateCreation)
                + ", validité " + sdf.format(limiteDateLicence);
    }

    public int getMode() {
        return mode;
    }

    public void setMode(final int mode) {
        this.mode = mode;
    }
}
