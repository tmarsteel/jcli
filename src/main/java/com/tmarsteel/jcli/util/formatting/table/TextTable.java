package com.tmarsteel.jcli.util.formatting.table;

import com.tmarsteel.jcli.util.formatting.OutOfRenderingSpaceException;
import com.tmarsteel.jcli.util.formatting.Renderable;
import com.tmarsteel.jcli.util.formatting.multiline.MultilineTextStrategy;
import com.tmarsteel.jcli.util.formatting.multiline.WordsplitMultilineStrategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.tmarsteel.jcli.util.formatting.FormattingUtils.padRight;

/**
 * Formats a text-based table very similar to those printed by sql server CLIs.
 */
public class TextTable implements Renderable
{
    /** The headings. If both {@link #headerRow} and {@link #headerRowAsStrings} are null, no heading should be output. */
    private List<Renderable> headerRow;

    /** Header row as strings. If both {@link #headerRow} and {@link #headerRowAsStrings} are null, no heading should be output. */
    private List<String> headerRowAsStrings;

    /** The rows of the table. */
    private final List<List<Renderable>> rows = new ArrayList<>();

    /** Whether borders should be rendered */
    private boolean hasBorders = true;

    private ColumnWidthCalculator columnWidthCalculator = (index, nColumns) -> -1;

    private MultilineTextStrategy multilineTextStrategy = WordsplitMultilineStrategy.getInstance();

    /** @return Whether borders are rendered */
    public boolean hasBorders()
    {
        return hasBorders;
    }

    /** Sets whether borders should be rendered */
    public void setHasBorders(boolean hasBorders)
    {
        this.hasBorders = hasBorders;
    }

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
        this.headerRow = null;
        this.headerRowAsStrings = Arrays.asList(headers);

        return this;
    }

    /**
     * @return {@code this}
     */
    public TextTable setHeadings(List<Renderable> headerRow) {
        this.headerRow = headerRow;
        this.headerRowAsStrings = null;
        return this;
    }

    /**
     * Adds a row with the given column values to the table.
     * @return {@code this}
     */
    public TextTable addRow(String... cellValues) {
        ArrayList<Renderable> row = new ArrayList<>(cellValues.length);
        for (int i = 0;i < cellValues.length;i++) {
            final String cellValue = cellValues[i];
            row.add((maxWidth, lineSeparator) -> multilineTextStrategy.wrap(cellValue, maxWidth, lineSeparator));
        }
        rows.add(row);

        return this;
    }

    /**
     * Adds a row with the given colun values to the table.
     * @return {@code this}
     */
    public TextTable addRow(Renderable... cellValues) {
        rows.add(Arrays.asList(cellValues));

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
        int[] columnWidths = determineColumnWidths(maxWidth);
        String rowSeparator = getRowSeparator(columnWidths);

        StringBuilder outputBuilder = new StringBuilder(rows.size() * columnWidths.length * 60);

        if (hasBorders)
        {
            outputBuilder.append(rowSeparator);
            outputBuilder.append(lineSeparator);
        }

        if (headerRow != null) {
            outputBuilder.append(render(headerRow, columnWidths, lineSeparator));
            outputBuilder.append(lineSeparator);
            outputBuilder.append(rowSeparator);
            outputBuilder.append(lineSeparator);
        }

        for (List<Renderable> row : rows) {
            outputBuilder.append(render(row, columnWidths, lineSeparator));
            outputBuilder.append(lineSeparator);
            outputBuilder.append(rowSeparator);
            outputBuilder.append(lineSeparator);
        }

        // cut the trailing lineSeparator
        outputBuilder.deleteCharAt(outputBuilder.length() - 1);

        // if no borders were rendered, there is one more empty line at the bottom (the empty line separator)
        // remove that, too
        if (!hasBorders) {
            outputBuilder.deleteCharAt(outputBuilder.length() - 1);
        }

        return outputBuilder.toString();
    }

    /**
     * Builds and returns a row separator for the given column definition.
     * @return The separator, e.g. for input [2, 4, 2] returns "+--+----+--+"
     */
    private String getRowSeparator(int[] columnWidths) {
        // no borders -> no line separators
        if (!hasBorders) return "";
        // generate the separator row
        StringBuilder rowSeparatorBuilder = new StringBuilder();
        rowSeparatorBuilder.append('+');
        for (int i = 0;i < columnWidths.length;i++) {
            // as many - as the column is wide + 2 for the cell padding
            rowSeparatorBuilder.append(new String(new char[columnWidths[i] + 2]).replace('\0', '-'));
            rowSeparatorBuilder.append('+');
        }

        return rowSeparatorBuilder.toString();
    }

    /**
     * Renders the given row using the given column widths.
     * @param row The row to renderStringRow
     * @param columnWidths The column definition
     * @return The row, without trailing linefeed
     */
    private String render(List<Renderable> row, int[] columnWidths, char lineSeparator) {
        if (row.size() != columnWidths.length) throw new IllegalArgumentException("Difference in number of cells and column widths");

        List<String> rowAsStrings = new ArrayList<>(columnWidths.length);
        for (int i = 0;i < columnWidths.length;i++) {
            Renderable cell = row.get(i);
            rowAsStrings.add(cell.render(columnWidths[i], lineSeparator));
        }

        return renderStringRow(rowAsStrings, columnWidths, lineSeparator);
    }

    /**
     * Renders the given row, assuming that none of the strings in the row exceed the respective length in
     * {@code columnWidths}.
     * @param rowStrings The strings of the row, in the order of the cells.
     */
    private String renderStringRow(List<String> rowStrings, int[] columnWidths, char lineSeparator) {
        if (rowStrings.size() != columnWidths.length) throw new IllegalArgumentException("Difference in number of cells and column widths");

        /*
         multiline rows are actually a table for themselves:

         | cell1 line 1 | cell2 line 1 | cell 3 line 1 |
         | cell1 line 2 | cell2 line 2 |               |

         This means that the cells have to be split by line, first
          */
        List<String[]> rows = new ArrayList<>(rowStrings.size());
        int maxNumberOfInnerCellLines = 0;
        for (int columnIndex = 0;columnIndex< columnWidths.length;columnIndex++) {
            String[] cellLines = rowStrings.get(columnIndex).split("" + lineSeparator);

            if (cellLines.length > maxNumberOfInnerCellLines) {
                maxNumberOfInnerCellLines = cellLines.length;
            }

            rows.add(cellLines);
        }

        // wen can now iterate over the lines and, line by line, build the columns
        StringBuilder outputBuilder = new StringBuilder();

        // define the border string depending on hasBorders
        final String borderTextLineStart = hasBorders? "| " : "";
        final String borderTextBetweenCellLines = hasBorders? " | ": "  ";
        final String borderTextLineEnd = hasBorders? " |" : "";

        // for each line, regardless of line boundaries within the cells
        for (int innerCellLineIndex = 0;innerCellLineIndex < maxNumberOfInnerCellLines;innerCellLineIndex++) {
            outputBuilder.append(borderTextLineStart);
            // render the current line in each cell
            for (int columnIndex = 0;columnIndex< columnWidths.length;columnIndex++)
            {
                String[] cellLines = rows.get(columnIndex);
                String cellLine = innerCellLineIndex >= cellLines.length? "" : cellLines[innerCellLineIndex];

                outputBuilder.append(padRight(cellLine, columnWidths[columnIndex]));

                if (columnIndex < columnWidths.length - 1) {
                    outputBuilder.append(borderTextBetweenCellLines);
                }
                else {
                    outputBuilder.append(borderTextLineEnd);
                }
            }

            // for each row but the last add a newline
            if (innerCellLineIndex < maxNumberOfInnerCellLines - 1) {
                outputBuilder.append(lineSeparator);
            }
        }

        return outputBuilder.toString();
    }

    /** @return A stream of all the header entries and the rows */
    private Stream<List<Renderable>> streamOfAll() {
        if (headerRow == null && headerRowAsStrings == null) {
            return rows.stream();
        }

        Stream<List<Renderable>> headerStream;
        if (headerRow != null) {
            headerStream = Stream.of(headerRow);
        } else {
            headerStream = Stream.of(headerRowAsStrings.stream().map(multilineTextStrategy::renderableOf).collect(Collectors.toList()));
        }

        return Stream.concat(
            headerStream,
            rows.stream()
        );
    }

    private int calculateNumberOfColumns() {
        return streamOfAll()
            .mapToInt(List::size)
            .max().orElse(0);
    }

    private int[] determineColumnWidths(int tableMaxWidth) {
        int nColumns = calculateNumberOfColumns();
        if (nColumns <= 0) return new int[0];

        int[] columnTargets = new int[nColumns];
        int fixedColumnsPureTextSpace = 0;
        int nFixedColumns = 0;
        for (int i = 0;i < nColumns;i++) {
            columnTargets[i] = this.columnWidthCalculator.getColumnWidth(i, nColumns);
            if (columnTargets[i] > 0) {
                fixedColumnsPureTextSpace += columnTargets[i];
                nFixedColumns++;
            }
        }

        int nDynamicColumns = nColumns - nFixedColumns;

        // determine whether tableMaxWidth is actually enough
        final int horizontalBorderAndPaddingSpace;
        if (hasBorders) {
            horizontalBorderAndPaddingSpace = 1 + nColumns * 3; // | cell text | cell text |
        }
        else {
            horizontalBorderAndPaddingSpace = (nColumns - 1) * 2; // cellText  cellText
        }
        int availableHorizontalTextSpace = tableMaxWidth - horizontalBorderAndPaddingSpace;
        int minimumDynamicColumnSpace = nDynamicColumns; // at least 1 space per dynamic column

        if (fixedColumnsPureTextSpace + minimumDynamicColumnSpace > availableHorizontalTextSpace) {
            throw new OutOfRenderingSpaceException("maxWidth (= " + tableMaxWidth + ") is too small for this table to be rendered: " +
                "fixedColumns (= " + fixedColumnsPureTextSpace + ") + borders and padding (= " + horizontalBorderAndPaddingSpace + ") + dynamicColumns (= " + minimumDynamicColumnSpace + ") " +
                " > maxWidth (= " + tableMaxWidth + ")");
        }

        if (nDynamicColumns > 0) {
            int dynamicColumnTotalSpace = availableHorizontalTextSpace - fixedColumnsPureTextSpace;
            int flooredSpacePerDynamicColumn = dynamicColumnTotalSpace / nDynamicColumns;

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
                for (int i = 0;i < dynamicColumnWidths.length;i++) {
                    if (dynamicColumnWidths[i] == 0) {
                        dynamicColumnWidths[i] = flooredSpacePerDynamicColumn;
                    }
                    if (remainingSpace > 0)
                    {
                        dynamicColumnWidths[i]++;
                        remainingSpace--;
                    }
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
