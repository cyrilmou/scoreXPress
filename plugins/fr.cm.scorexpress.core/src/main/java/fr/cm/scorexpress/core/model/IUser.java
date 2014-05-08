package fr.cm.scorexpress.core.model;

public abstract class IUser extends IData<IData> {

    private static final long serialVersionUID = 3156180171038718312L;
    protected Integer id;

    public Object getInfoLocal(final String attribut) {
        return null;
    }

    public String getPrefix() {
        return null;
    }

    public boolean setInfoLocal(final String attribut, final Object val) {
        return false;
    }

    public abstract Object accept(final ElementModelVisitor visitor, final Object data);

    public Integer getId() {
        return id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }
}
