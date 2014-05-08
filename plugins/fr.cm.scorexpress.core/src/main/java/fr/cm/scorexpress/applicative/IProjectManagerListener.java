package fr.cm.scorexpress.applicative;

interface IProjectManagerListener {

	public void addProjectManagerListener(IProjectManager listener);

	public void removeProjectManagerListener(IProjectManager listener);

	public void fireProjectManagerListener();
}
