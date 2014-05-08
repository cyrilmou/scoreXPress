package fr.cm.common.widget;

import org.eclipse.swt.widgets.Display;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class SwtUtil {
    private SwtUtil() {
    }

    @SuppressWarnings({"TypeMayBeWeakened", "AnonymousInnerClass"})
    public static <A> A proxySwt(final A instance) {
        return (A) Proxy.newProxyInstance(
                instance.getClass().getClassLoader(), instance.getClass().getInterfaces(), new InvocationHandler() {
                    final private Boolean sync = false;
                    private boolean run = false;
                    private Object result;

                    public Object invoke(
                            final Object proxy, final Method method, final Object[] parameters) throws Throwable {
                        run = false;
                        Display.getDefault().syncExec(
                                new Runnable() {
                                    public void run() {
                                        try {
                                            result = method.invoke(instance, parameters);
                                        } catch (InvocationTargetException e) {
                                            e.getTargetException().printStackTrace();
                                        } catch (IllegalAccessException e) {
                                            e.printStackTrace();
                                        }
                                        synchronized (sync) {
                                            run = true;
                                            sync.notifyAll();
                                        }
                                    }
                                });
                        synchronized (sync) {
                            while (!run) {
                                sync.wait();
                            }
                        }
                        return result;
                    }
                });
    }
}
