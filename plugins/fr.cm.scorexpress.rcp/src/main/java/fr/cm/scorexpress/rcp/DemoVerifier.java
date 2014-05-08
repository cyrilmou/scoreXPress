package fr.cm.scorexpress.rcp;

import static fr.cm.scorexpress.applicative.ProjectManager.*;

public class DemoVerifier {
    private static final int LICENCE_VERIFY_PERIOD = 180000;

    private DemoVerifier() {
    }

    public static Thread createDemoVersionVerifier() {
        final Thread verifVersion = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    verifyLicence(VERIF_VERSION_NORMALE);
                    verifyLicence(VERIF_DATE);
                    try {
                        Thread.sleep(LICENCE_VERIFY_PERIOD);
                    } catch (InterruptedException ignored) {
                    }
                }
            }

        }, "VERIF_VERSION");
        verifVersion.start();
        return verifVersion;
    }
}
