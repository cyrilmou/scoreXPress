package fr.cm.scorexpress.core.model.impl;

import fr.cm.scorexpress.core.model.ObjDossard;
import fr.cm.scorexpress.core.model.ObjManifestation;

public interface IVisitor {

	public String visite(Object element);

	public String visite(ObjStep step);

	public String visite(ObjEpreuve epreuve);

	public String visite(ObjManifestation manif);

	public String visite(ObjDossard dossard);

}
