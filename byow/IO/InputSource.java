package byow.IO;

/**
 * Created by hug.
 */
public interface InputSource {
    char getNextKey();
    boolean possibleNextInput();

    default char getNextKeyCleaned() {
        return Character.toUpperCase(getNextKey());
    }
}
