package fr.cm.scorexpress.applicative;

import java.util.Date;

public class LicenceFactory {
    public static Licence VALID_LICENCE = new Licence("DEFAULT", new Date(
            new Date().getTime() + 24 * 3600000), Licence.MODE_NORMAL);
}
