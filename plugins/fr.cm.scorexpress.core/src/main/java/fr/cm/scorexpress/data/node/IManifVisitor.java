/**
 *
 */
package fr.cm.scorexpress.data.node;

public interface IManifVisitor {

    public Object visiteNode(Node parent, Object data);

    public Object visiteCategories(NodeCategories parent, Object data);

    public Object visiteCategory(NodeCategory parent, Object data);

    public Object visiteTeam(NodeTeam parent, Object data);

    public Object visiteTeams(NodeTeams parent, Object data);

    public Object visiteUser(NodeUser parent, Object data);

    public Object visiteUsers(NodeUsers parent, Object data);

    public Object visiteChronos(NodeChronos parent, Object data);

    public Object visiteChrono(NodeChrono parent, Object data);

    public Object visiteManif(NodeManif parent, Object data);

    public Object visiteStep(NodeStep parent, Object data);

    public Object visiteStation(NodeStation parent, Object data);

    public Object visitePenalities(NodePenalities parent, Object data);

    public Object visitePenality(NodePenality parent, Object data);

    public Object visiteInscribe(NodeInscribe parent, Object data);

    public Object visiteColumn(NodeColumn parent, Object data);

    public Object visiteWatchList(NodeWatchlist parent, Object data);

    public Object visiteTime(NodeTime time, Object data);
}
