package no.ntnu.idatx2003.millions.io;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import no.ntnu.idatx2003.millions.util.Validate;

import java.io.IOException;
import java.io.Reader;

/**
 * JSON implementation of {@link GameStateReader}.
 */
public class JsonGameStateReader implements GameStateReader {
    private final ObjectMapper objectMapper;

    /**
     * Creates a JSON game state reader.
     */
    public JsonGameStateReader() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Reads a saved game state from JSON.
     *
     * @param reader the input stream; must not be {@code null}
     * @return the parsed game state
     * @throws IOException if the stream cannot be read or parsed
     */
    @Override
    public GameState read(Reader reader) throws IOException {
        Validate.requireNonNull(reader, "reader");
        return objectMapper.readValue(reader, GameState.class);
    }
}
