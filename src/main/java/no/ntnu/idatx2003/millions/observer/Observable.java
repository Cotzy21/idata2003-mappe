package no.ntnu.idatx2003.millions.observer;

/**
 * Defines an observable domain object.
 *
 * @param <T> the source type observers receive
 */
public interface Observable<T> {

    /**
     * Adds an observer.
     *
     * @param observer the observer to add; must not be {@code null}
     * @return {@code true} if the observer was added
     */
    boolean addObserver(Observer<T> observer);

    /**
     * Removes an observer.
     *
     * @param observer the observer to remove; must not be {@code null}
     * @return {@code true} if the observer was removed
     */
    boolean removeObserver(Observer<T> observer);

    /**
     * Notifies all registered observers.
     */
    void notifyObservers();
}
