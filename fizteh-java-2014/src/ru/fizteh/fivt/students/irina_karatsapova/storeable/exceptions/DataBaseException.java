package ru.fizteh.fivt.students.irina_karatsapova.storeable.exceptions;

public class DataBaseException extends IllegalStateException {
    public DataBaseException() {
        super();
    }
    public DataBaseException(String message) {
        super(message);
    }
    public DataBaseException(String message, Throwable cause) {
        super(message, cause);
    }
    public DataBaseException(Throwable cause) {
        super(cause);
    }
}
