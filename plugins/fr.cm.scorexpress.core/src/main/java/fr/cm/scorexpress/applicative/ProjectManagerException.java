package fr.cm.scorexpress.applicative;

public class ProjectManagerException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private final String message;

    public ProjectManagerException(final String message, final Throwable e) {
        super(e.getCause());
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
