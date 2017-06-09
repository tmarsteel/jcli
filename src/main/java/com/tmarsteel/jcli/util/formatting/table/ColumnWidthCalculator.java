package com.tmarsteel.jcli.util.formatting.table;

public interface ColumnWidthCalculator
{
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
