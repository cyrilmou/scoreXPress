package fr.cm.scorexpress.core.model;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import fr.cm.scorexpress.core.model.impl.ControlerManifestation;
import fr.cm.scorexpress.core.model.impl.ObjStep;
import fr.cm.scorexpress.core.model.impl.StepUtils;
import org.apache.commons.lang.StringUtils;

import java.util.*;

public class ObjManifestation extends IData<IData> implements IUsers, AbstractCategories, AbstractSteps {
    private static final long serialVersionUID = -4666251239223261193L;

    private String fileName;
    private String nom;
    private Date date = new Date();
    private final Collection<IUser> users = newArrayList();
    private final Collection<ObjCategorie> categories = newArrayList();
    private final List<ObjStep> steps = newArrayList();
    private final ObjConfiguration configuration = new ObjConfiguration();
    private final HashMap<ObjStep, String> importFileName = newHashMap();
    private String description;
    private int nextIdUser = 1;
    public static final String VAR_PREFIX = "MANIF_";
    public static final String VAR_LIB = "LIB";

    public ObjManifestation(final String nom) {
        this.nom = nom;
        setControler(new ControlerManifestation(this));
    }

    public Date getDate() {
        return date;
    }

    public String getNom() {
        return nom;
    }

    public void setDate(final Date date) {
        this.date = date;
    }

    public void setNom(final String nom) {
        this.nom = nom;
        hasChanged(getControler(), this, VAR_LIB);
    }

    public boolean addStep(final ObjStep step) {
        step.setParent(this);
        final boolean res = steps.add(step);
        hasAdd(getControler(), step);
        return res;
    }

    public boolean removeStep(final ObjStep step) {
        final boolean res = steps.remove(step);
        hasRemove(getControler(), step);
        return res;
    }

    public void addCategorie(final ObjCategorie cat) {
        cat.setParent(this);
        if (!categories.contains(cat)) {
            categories.add(cat);
            hasAdd(getControler(), cat);
        }
    }

    public boolean removeCategorie(final ObjCategorie cat) {
        final boolean res = categories.remove(cat);
        if (res) {
            hasRemove(getControler(), cat);
        }
        return res;
    }

    public Collection<ObjStep> getSteps() {
        return steps;
    }

    public Collection<IUser> getUsers() {
        return users;
    }

    public Collection<ObjCategorie> getCategories() {
        return categories;
    }

    public ObjConfiguration getConfiguration() {
        return configuration;
    }

    public Object getInfoLocal(final String attribut) {
        if (VAR_LIB.equalsIgnoreCase(attribut)) {
            return nom;
        }
        return null;
    }

    public boolean setInfoLocal(final String attribut, final Object val) {
        if (VAR_LIB.equalsIgnoreCase(attribut)) {
            nom = "" + val;
            return true;
        }
        return false;
    }

    public String getPrefix() {
        return VAR_PREFIX;
    }

    public boolean addUser(final IUser user) {
        user.setParent(this);
        users.add(user);
        if (user.getId() == null) {
            user.setId(nextIdUser);
        }
        if (user.getId() >= nextIdUser) {
            nextIdUser = user.getId() + 1;
        }
        return true;
    }

    public boolean removeUser(final IUser user) {
        return users.remove(user);
    }

    public void trierEpreuve() {
        Collections.sort(steps, StepUtils.getComparatorNumero());
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(final String fileName) {
        this.fileName = fileName;
    }

    public void setImportFileName(final ObjStep etape, final String fileName) {
        importFileName.put(etape, fileName);
    }

    public String getImportFileName(final ObjStep etape) {
        if (etape == null || etape.getImportFileName() == null) {
            return null;
        }
        return importFileName.get(etape);
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getDecription() {
        return description;
    }

    public Object accept(final ElementModelVisitor visitor, final Object data) {
        return visitor.visite(this, data);
    }

    public ControlerManifestation getControlerManif() {
        return (ControlerManifestation) controler;
    }

    public IUser getUser(final String id) {
        for (final IUser user1 : users) {
            if (StringUtils.equals(id, "" + user1.getId())) {
                return user1;
            }
        }
        return null;
    }

    public String getLib() {
        return getNom();
    }
}
