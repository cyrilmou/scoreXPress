package fr.cm.scorexpress.core.model;


public interface IStepControler {

    public abstract Object[] getCategories();

    public abstract Object[] getEtapes();

    public void addNewCategorie();

    public void removeCategorie(ObjCategorie cat);

}