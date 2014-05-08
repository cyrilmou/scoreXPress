package fr.cm.framework;

import javax.swing.*;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Licence implements Serializable {

    private static final long serialVersionUID = 5656993671308671705L;

    private String version = null;

    private Date limiteDateLicence = null;

    private String nameAttribution = "Cyril Moutenet";

    final transient static private DateFormat sdf = new SimpleDateFormat(
            "dd/MM/yyyy");

    private final Date dateCreation = new Date();

    // private String licenceDate;

    public Licence() {
    }

    public void setLicenceDate(final String date) {
        try {
            limiteDateLicence = sdf.parse(date);
        } catch (Exception ex) {
        }
    }

    public static void main(final String[] arg) {
        try {
            final FileOutputStream fou = new FileOutputStream(arg[0]);
            final ObjectOutputStream dout = new ObjectOutputStream(fou);
            final Licence licence = new Licence();
            final String date = JOptionPane.showInputDialog("Creation d'une licence",
                    arg[1]);
            licence.setLicenceDate(date);
            licence.version = arg[2];
            dout.writeObject(licence);
            dout.flush();
            dout.close();
            System.out.println(arg[0] + " " + arg[1] + " a ete generee");
            System.out.println(licence);
            System.exit(0);
        } catch (IOException ex1) {
        }
    }

    public static Licence getLicence(final String fileName) {
        try {
            System.out.println(fileName);
            final FileInputStream fin = new FileInputStream(fileName);
            final ObjectInputStream din = new ObjectInputStream(fin);
            return (Licence) din.readObject();
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
        System.out.println(limiteDateLicence);
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
            System.out.println("Licence incorrect");
            return false;
        }
        System.out.println("Licence non trouvée " + fileName);
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
        return "Version " + version + " crée le " + sdf.format(dateCreation)
                + ",\n validité " + sdf.format(limiteDateLicence)
                + ", attribuée à " + nameAttribution;
    }

    /**
     * @return nameAttribution
     */
    public String getNameAttribution() {
        return nameAttribution;
    }

    /**
     * @param nameAttribution nameAttribution à définir
     */
    public void setNameAttribution(final String nameAttribution) {
        this.nameAttribution = nameAttribution;
    }
}
