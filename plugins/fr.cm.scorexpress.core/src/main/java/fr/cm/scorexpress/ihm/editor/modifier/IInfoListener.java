package fr.cm.scorexpress.ihm.editor.modifier;

import fr.cm.scorexpress.core.model.AbstractGetInfo;

public interface IInfoListener {
	public void modify(AbstractGetInfo element, String property, Object value);
}
