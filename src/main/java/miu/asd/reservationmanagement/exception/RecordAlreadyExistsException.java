package miu.asd.reservationmanagement.exception;

public class RecordAlreadyExistsException extends RuntimeException {
    public RecordAlreadyExistsException(final String message) {
        super(message);
    }
}
