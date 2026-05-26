package no.ntnu.idatx2003.millions.io;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import no.ntnu.idatx2003.millions.util.Validate;

import java.io.IOException;
import java.io.Writer;

/**
 * JSON implementation of {@link GameStateWriter}.
 */
public class JsonGameStateWriter implements GameStateWriter {
    private final ObjectMapper objectMapper;

    /**
     * Creates a JSON game state writer.
     */
    public JsonGameStateWriter() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    /**
     * Writes a saved game state as formatted JSON.
     *
     * @param writer the output stream; must not be {@code null}
     * @param gameState the state to write; must not be {@code null}
     * @throws IOException if the stream cannot be written
     */
    @Override
    public void write(Writer writer, GameState gameState) throws IOException {
        Validate.requireNonNull(writer, "writer");
        Validate.requireNonNull(gameState, "gameState");
        objectMapper.writeValue(writer, gameState);
    }
}
