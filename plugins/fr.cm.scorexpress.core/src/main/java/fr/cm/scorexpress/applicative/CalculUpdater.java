/**
 *
 */
package fr.cm.scorexpress.applicative;

import fr.cm.scorexpress.core.model.impl.ObjStep;
import fr.cm.scorexpress.core.model.impl.StepUtils;
import java.util.Date;
import java.util.LinkedList;

import static com.google.common.collect.Lists.newLinkedList;

public class CalculUpdater implements Runnable {
    private static CalculUpdater             instance = null;
    private final  Object                    sync     = new Object();
    private final  LinkedList<ICalculResult> results  = newLinkedList();

    private CalculUpdater() {
    }

    public static CalculUpdater createCalculUpdater() {
        if (instance == null) {
            instance = new CalculUpdater();
            final Thread thread = new Thread(instance, "CalculUpdater");
            thread.start();
        }
        return instance;
    }

    public void update(final ICalculResult calculResult) {
        synchronized (sync) {
            results.add(calculResult);
            sync.notify();
        }
    }

    private synchronized void calcul(final ICalculResult calculResult) {
        final ObjStep step = calculResult.getStep();
        final Date debut = new Date();
        StepUtils.updateResultat(step);
        final long milliseconde = new Date(new Date().getTime() - debut.getTime()).getTime();
        System.out.print("Calcul des resultats de " + step + " en "); //$NON-NLS-1$
        System.out.println(milliseconde + " ms"); //$NON-NLS-1$
        calculResult.updateFinish(step);
    }

    @Override
    public void run() {
        while (true) {
            try {
                final ICalculResult result;
                synchronized (sync) {
                    while (results.isEmpty()) {
                        sync.wait();
                    }
                    result = results.pop();
                    Thread.sleep(1000);
                }
                calcul(result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
