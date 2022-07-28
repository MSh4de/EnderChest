package eu.mshade.enderchest.execptions;

public class ChunkNotReadableError extends Exception {

    public ChunkNotReadableError(String message) {
        super(message);
    }

    public ChunkNotReadableError(String message, Throwable cause) {
        super(message, cause);
    }
}
