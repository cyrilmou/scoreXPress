/**
 *
 */
package fr.cm.scorexpress.applicative;

import fr.cm.scorexpress.core.model.impl.ObjStep;

/**
 * @author Cyril
 * @date 23 oct. 08
 */
public interface ICalculResult {

	public ObjStep getStep();

	public void updateFinish(ObjStep step);
}
