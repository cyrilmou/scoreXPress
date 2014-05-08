package fr.cm.scorexpress.ihm.editor.modifier;

import fr.cm.scorexpress.core.model.AbstractGetInfo;

public interface ISetInfoListener {
	public void addSetInfoListener(IInfoListener info);

	public void removeSetInfoListener(IInfoListener info);

	public void fireSetInfoListener(AbstractGetInfo element, String property,
			Object value);
}
