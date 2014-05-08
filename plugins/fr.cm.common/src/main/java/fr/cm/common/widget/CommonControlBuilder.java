package fr.cm.common.widget;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

public abstract class CommonControlBuilder<C extends Control, B extends ControlBuilder<B>> implements ControlBuilder<B> {
    protected final MyToolkit toolkit;
    protected final C control;

    protected CommonControlBuilder(final MyToolkit toolkit, final C control) {
        this.toolkit = toolkit;
        this.control = control;
    }

    protected void addListener() {
        control.addDisposeListener(
                new DisposeListener() {
                    public void widgetDisposed(final DisposeEvent disposeEvent) {
                        Display.getCurrent().syncExec(
                                new Runnable() {
                                    public void run() {

                                    }
                                });
                    }
                });
    }

    public B withLayoutData(final Object layoutData) {
        control.setLayoutData(layoutData);
        return (B) this;
    }

    public void dispose(){

    }
}
