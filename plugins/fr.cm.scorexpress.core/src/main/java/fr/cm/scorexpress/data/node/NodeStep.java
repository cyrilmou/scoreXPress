package fr.cm.scorexpress.data.node;

public class NodeStep extends NodeFactory {
    public Integer _id;
    public String  _name;
    public String  _start_date;
    public String  _start_time;
    public Boolean _cumulersousetape = false;
    public Boolean _calcul           = true;
    public String _balisedepart;
    public String _balisearrivee;
    public String  _desc            = "";
    public String  _importfilename  = "";
    public String  _categoryfilter  = "";
    public Boolean _classementinter = false;
    public Boolean _arretchrono     = false;
    public Boolean _epreuve         = false;
    public String  _lastimport      = "";
    public Boolean _importauto      = false;
    public Boolean _penalityseizure = false;

    public Object accept(final IManifVisitor visitor, final Object data) {
        return visitor.visiteStep(this, data);
    }
}
