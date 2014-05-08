package fr.cm.scorexpress.applicative;

public class ProjectManagerFactory {
    private static ProjectManager projectManager = null;
    private static AutoImportProcess process;

    private ProjectManagerFactory() {
    }

    public static ProjectManager getProjectManager() {
        if (projectManager == null) {
            projectManager = new ProjectManager();
            process = new AutoImportProcess(projectManager);
            process.start();
        }
        return projectManager;
    }

    public static AutoImportProcess getAutoImportProcess() {
        return process;
    }
}
