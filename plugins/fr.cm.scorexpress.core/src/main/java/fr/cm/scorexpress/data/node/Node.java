/**
 *
 */
package fr.cm.scorexpress.data.node;

/**
 * Noeud visitor
 *
 * @author Cyril
 * @date 15 déc. 07
 */
public interface Node{
	enum DOCUMENT {DOCUMENT_NODE, ELEMENT_NODE};

	/**
	 * Ajout un fils n
	 *
	 * @param n
	 */
	public void addChild(Node n);

	/**
	 * Identifie le noeud parent n
	 *
	 * @param n
	 */
	public void setParent(Node n);

	/**
	 * Recupere le fils n
	 *
	 * @param n
	 * @return
	 */
	public Node getChild(int n);

	/**
	 * Nombre de fils
	 *
	 * @return
	 */
	public int getNumChild();

	/**
	 * Viste du noeud
	 *
	 * @param visitor
	 * @param data
	 * @return
	 */
	public Object accept(IManifVisitor visitor, Object data);


}
