package no.ntnu.idatx2003.millions.io;

import java.io.IOException;
import java.io.Reader;

/**
 * Strategy interface for reading saved game states.
 *
 * <p>{@link JsonGameStateReader} is the current implementation. Controllers
 * depend on this interface so another save format can be added without
 * changing model code.</p>
 */
public interface GameStateReader {

    /**
     * Reads a game state from a character stream.
     *
     * @param reader the input stream; must not be {@code null}
     * @return the parsed game state
     * @throws IOException if the stream cannot be read or parsed
     */
    GameState read(Reader reader) throws IOException;
}
