/**
 *
 */
package fr.cm.scorexpress.core.model;

import java.util.Collection;

public interface IUserChronos {

    boolean addUserChronos(ObjUserChronos chrono);

    boolean removeUserChronos(ObjUserChronos chrono);

    Collection<ObjUserChronos> getUserChronos();

    ObjUserChronos getUserChronosByDossard(String dossard);
}
