package no.ntnu.idatx2003.millions.view;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.control.TableColumn;

/**
 * Factory methods for table columns used by the main view.
 */
final class TableColumns {

    private TableColumns() {
        // Utility class.
    }

    /**
     * Creates a string table column.
     *
     * @param title the column title
     * @param textProvider maps a row value to display text
     * @param width the proportional maximum width
     * @param <T> the row type
     * @return the table column
     */
    static <T> TableColumn<T, String> column(String title, TextProvider<T> textProvider, double width) {
        TableColumn<T, String> column = new TableColumn<>(title);
        column.setCellValueFactory(cell -> new ReadOnlyStringWrapper(textProvider.get(cell.getValue())));
        column.setMaxWidth(1f * Integer.MAX_VALUE * width);
        return column;
    }

    @FunctionalInterface
    interface TextProvider<T> {
        String get(T value);
    }
}
