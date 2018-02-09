package org.simbasecurity.manager.service.rest.error;

public class SimbaManagerException extends RuntimeException {
    private String errorKey;
    private String message;

    public SimbaManagerException(String errorKey, String message) {
        super(message);
        this.errorKey = errorKey;
        this.message = message;
    }

    public String getErrorKey() {
        return errorKey;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public SimbaManagerErrorRepresentation toRepresentation() {
        return new SimbaManagerErrorRepresentation(this.errorKey, this.message);
    }

    public static class SimbaManagerErrorRepresentation {
        private String errorkey;
        private String message;

        private SimbaManagerErrorRepresentation(String errorkey, String message) {
            this.errorkey = errorkey;
            this.message = message;
        }

        public String getErrorkey() {
            return errorkey;
        }

        public String getMessage() {
            return message;
        }
    }
}
