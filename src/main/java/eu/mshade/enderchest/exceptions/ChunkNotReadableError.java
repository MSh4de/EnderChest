package eu.mshade.enderchest.exceptions;

public class ChunkNotReadableError extends Exception {

    public ChunkNotReadableError(String message) {
        super(message);
    }

    public ChunkNotReadableError(String message, Throwable cause) {
        super(message, cause);
    }
}
