package com.tmarsteel.jcli.util.formatting.table;

public interface ColumnWidthCalculator
{
    /**
     * Returns a {@link ColumnWidthCalculator} that returns the given values for the columns with the
     * respective index. E.g. the calculator resulting from an invocation of {@code ofValues(10, 20, 30)}
     * returns the columns widths 10, 20 and 30 for the column indexes 0, 1 and 2 respectively.
     */
    static ColumnWidthCalculator ofValues(int... values) {
        return (cIndex, nCs) -> {
            try {
                return values[cIndex];
            }
            catch (ArrayIndexOutOfBoundsException ex)
            {
                throw new UnsupportedOperationException("Column #" + cIndex + " not defined", ex);
            }
        };
    }

    /**
     * Given the total number of columns in a table and the index of one particular column, returns the desired
     * width of that column.
     *
     * @param columnIndex The index of the column to determine the width for.
     * @param nColumns    Total number of columns in the table
     * @return Width of the column. 0 + negative numbers indicate dynamic width (split across all dynamic columns).
     */
    public int getColumnWidth(int columnIndex, int nColumns);
}
