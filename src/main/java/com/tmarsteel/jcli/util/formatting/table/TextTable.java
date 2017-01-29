package com.tmarsteel.jcli.util.formatting.table;

import com.tmarsteel.jcli.util.formatting.OutOfRenderingSpaceException;
import com.tmarsteel.jcli.util.formatting.Renderable;
import com.tmarsteel.jcli.util.formatting.multiline.MultilineTextStrategy;
import com.tmarsteel.jcli.util.formatting.multiline.WordsplitMultilineStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Formats a text-based table in markdown format.
 */
public class TextTable implements Renderable
{
    /** The headings. If null, no heading should be output. */
    private List<Renderable> headerRow;

    /** The rows of the table. */
    private final List<List<Renderable>> rows = new ArrayList<>();

    /** Whether to output borders */
    private boolean hasBorders = true;

    private ColumnWidthCalculator columnWidthCalculator = new ColumnWidthCalculator() {};

    private MultilineTextStrategy multilineTextStrategy = WordsplitMultilineStrategy.getInstance();

    /**
     * @return {@code this}
     */
    public TextTable setMultilineTextStrategy(MultilineTextStrategy multilineTextStrategy) {
        this.multilineTextStrategy = multilineTextStrategy;
        return this;
    }

    /**
     * Sets the headings.
     * @return {@code this}
     */
    public TextTable setHeadings(String... headers) {
        ArrayList<Renderable> newHeaderRow = new ArrayList<>(headers.length);
        for (int i = 0;i < headers.length;i++) {
            newHeaderRow.set(i, multilineTextStrategy.renderableOf(headers[i]));
        }
        this.headerRow = newHeaderRow;

        return this;
    }

    /**
     * @return {@code this}
     */
    public TextTable setHeadings(List<Renderable> headerRow) {
        this.headerRow = headerRow;
        return this;
    }

    /**
     * Adds a row with the given column values to the table.
     * @return {@code this}
     */
    public TextTable addRow(String... cellValues) {
        ArrayList<Renderable> row = new ArrayList<>(cellValues.length);
        for (int i = 0;i < cellValues.length;i++) {
            row.set(i, multilineTextStrategy.renderableOf(cellValues[i]));
        }
        rows.add(row);

        return this;
    }

    /**
     * @param calculator The {@link ColumnWidthCalculator} to calculate column widths with. Must not be null.
     * @throws NullPointerException If {@code calculator} is null.
     */
    public void setColumnWidthCalculator(ColumnWidthCalculator calculator) {
        this.columnWidthCalculator = Objects.requireNonNull(calculator);
    }

    /**
     * Returns the rows of this table. Adding to the returned list reflects on this object.
     */
    public List<List<Renderable>> rows() {
        return this.rows;
    }

    @Override
    public String render(int maxWidth, char lineSeparator) {

    }

    private int calculateNumberOfColumns() {
        return
        Stream.concat(
            Stream.of(headerRow),
            rows.stream()
        )
            .mapToInt(List::size)
            .max().orElse(0);
    }

    private int[] determineColumnWidths(int tableMaxWidth) {
        int nColumns = calculateNumberOfColumns();
        if (nColumns <= 0) return new int[0];

        int[] columnTargets = new int[nColumns];
        int fixedColumnsSize = 0;
        int nFixedColumns = 0;
        for (int i = 0;i < nColumns;i++) {
            columnTargets[i] = this.columnWidthCalculator.getColumnWidth(i, nColumns);
            if (columnTargets[i] > 0) {
                fixedColumnsSize += columnTargets[i];
                nFixedColumns++;
            }
        }

        int nDynamicColumns = nColumns - nFixedColumns;

        // determine whether tableMaxWidth is actually enough
        int horizontalBorderSpace = hasBorders? nColumns + 1 : 0;
        int minimumDynamicColumnSpace = nDynamicColumns; // at least 1 space per dynamic column

        if (fixedColumnsSize + horizontalBorderSpace + minimumDynamicColumnSpace > tableMaxWidth ) {
            throw new OutOfRenderingSpaceException("maxWidth (= " + tableMaxWidth + ") is too small for this table to be rendered: " +
                "fixedColumns (= " + fixedColumnsSize + ") + borders (= " + horizontalBorderSpace + ") + dynamicColumns (= " + minimumDynamicColumnSpace + ") " +
                " > maxWidth (= " + tableMaxWidth + ")");
        }

        if (nDynamicColumns > 0) {
            int dynamicColumnTotalSpace = tableMaxWidth - fixedColumnsSize;
            int flooredSpacePerDynamicColumn = minimumDynamicColumnSpace / nDynamicColumns;
            if (flooredSpacePerDynamicColumn * nDynamicColumns == dynamicColumnTotalSpace) {
                // the space available for the dynamic columns can be evenly divided => nice!
                for (int i = 0;i < nColumns;i++) {
                    if (columnTargets[i] <= 0) {
                        columnTargets[i] = flooredSpacePerDynamicColumn;
                    }
                }

                return columnTargets;
            }
            else
            {
                // the space has to be divided up
                int remainingSpace = dynamicColumnTotalSpace - (flooredSpacePerDynamicColumn * nDynamicColumns);
                int[] dynamicColumnWidths = new int[nDynamicColumns]; // widths of the dynamic columns in columnTargets
                for (int i = 0;i < dynamicColumnWidths.length && remainingSpace > 0;i++) {
                    if (dynamicColumnWidths[i] == 0) {
                        dynamicColumnWidths[i] = dynamicColumnTotalSpace;
                    }
                    dynamicColumnWidths[i]++;
                    remainingSpace--;
                }

                // put the values from dynamicColumnWidths into their respective places in columnTargets and return that
                int k = 0;
                for (int i = 0;i < columnTargets.length;i++) {
                    if (columnTargets[i] <= 0) {
                        columnTargets[i] = dynamicColumnWidths[k++];
                    }
                }

                return columnTargets;
            }
        }
        else
        {
            // only fixed columns => columnTargets contains a valid return value
            return columnTargets;
        }
    }
}
