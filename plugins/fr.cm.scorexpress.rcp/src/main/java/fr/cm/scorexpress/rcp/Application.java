package fr.cm.scorexpress.rcp;

import fr.cm.scorexpress.rcp.i18n.Messages;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

import java.io.PrintStream;

public class Application implements IApplication {

    public Object start(final IApplicationContext context) {
        MessageConsole console = new MessageConsole(Messages.getString("Console.name"), null);
        ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[]{console});
        ConsolePlugin.getDefault().getConsoleManager().showConsoleView(console);
        MessageConsoleStream stream = console.newMessageStream();
        System.setOut(new PrintStream(stream));
        System.setErr(new PrintStream(stream));

        final Display display = PlatformUI.createDisplay();
        try {
            final int returnCode = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());
            if (returnCode == PlatformUI.RETURN_RESTART) {
                return IApplication.EXIT_RESTART;
            }
        } finally {
            display.dispose();
            return IApplication.EXIT_OK;
        }
    }

    public void stop() {
        final IWorkbench workbench = PlatformUI.getWorkbench();
        if (workbench == null) {
            return;
        }
        final Display display = workbench.getDisplay();
        display.syncExec(new Runnable() {
            public void run() {
                if (!display.isDisposed()) {
                    workbench.close();
                }
            }
        });
    }
}
