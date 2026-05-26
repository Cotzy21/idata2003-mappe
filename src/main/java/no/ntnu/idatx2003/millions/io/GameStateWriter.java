package no.ntnu.idatx2003.millions.io;

import java.io.IOException;
import java.io.Writer;

/**
 * Strategy interface for writing saved game states.
 *
 * <p>{@link JsonGameStateWriter} is the current implementation. Controllers
 * choose the writer strategy and pass DTOs to it, keeping persistence separate
 * from the domain model.</p>
 */
public interface GameStateWriter {

    /**
     * Writes a game state to a character stream.
     *
     * @param writer the output stream; must not be {@code null}
     * @param gameState the state to write; must not be {@code null}
     * @throws IOException if the stream cannot be written
     */
    void write(Writer writer, GameState gameState) throws IOException;
}
