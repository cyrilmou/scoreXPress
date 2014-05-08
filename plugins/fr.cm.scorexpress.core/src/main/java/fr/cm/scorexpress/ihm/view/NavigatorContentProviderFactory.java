package fr.cm.scorexpress.ihm.view;

import org.eclipse.jface.viewers.ITreeContentProvider;

public class NavigatorContentProviderFactory {

	public static ITreeContentProvider createContentProvider() {
		return new NavigatorContentProvider();
	}
}
