package fr.cm.scorexpress.core.model;

import java.util.Collection;

public interface IUsers extends AbstractGetInfo<IData> {
    public Collection<IUser> getUsers();

    public boolean addUser(IUser p);

    public boolean removeUser(IUser p);

}