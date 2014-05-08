/**
 *
 */
package fr.cm.scorexpress.core.model;

import java.util.Collection;

public interface IUserChronos {

    public boolean addUserChronos(ObjUserChronos chrono);

    public boolean removeUserChronos(ObjUserChronos chrono);

    public Collection<ObjUserChronos> getUserChronos();

}
