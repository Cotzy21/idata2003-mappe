package no.ntnu.idatx2003.millions.observer;

/**
 * Receives updates from an observable domain object.
 *
 * @param <T> the observed source type
 */
@FunctionalInterface
public interface Observer<T> {

    /**
     * Handles an update from the observed source.
     *
     * @param source the source that changed; never {@code null}
     */
    void update(T source);
}
