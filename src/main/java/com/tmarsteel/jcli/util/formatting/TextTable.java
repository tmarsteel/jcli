package com.tmarsteel.jcli.util.formatting;

import com.tmarsteel.jcli.util.formatting.multiline.MultilineTextStrategy;
import com.tmarsteel.jcli.util.formatting.multiline.WordsplitMultilineStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Formats a text-based table in markdown format.
 */
public class TextTable implements Renderable
{
    /**
     * The headings. If null, no heading should be output.
     */
    private Iterable<Renderable> headerRow;

    /**
     * The rows of the table.
     */
    private final List<Iterable<Renderable>> rows = new ArrayList<>();

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
    public TextTable setHeadings(Iterable<Renderable> headerRow) {
        this.headerRow = headerRow;
        return this;
    }

    /**
     * Adds a row with the given column values to the table.
     * @return {@coce this}
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
     * Returns the rows of this table. Adding to the returned list reflects on this object.
     */
    public List<Iterable<Renderable>> rows() {
        return this.rows;
    }

    @Override
    public String render(int maxWidth, char lineSeparator) {
        // TODO
    }
}
