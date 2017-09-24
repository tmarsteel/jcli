package com.tmarsteel.jcli.util.formatting.table.builder;

import com.tmarsteel.jcli.util.formatting.multiline.MultilineTextStrategy;
import com.tmarsteel.jcli.util.formatting.multiline.WordsplitMultilineStrategy;
import com.tmarsteel.jcli.util.formatting.table.ColumnWidthCalculator;
import com.tmarsteel.jcli.util.formatting.table.TextTable;

public class TextTableBuilder<SelfType extends TextTableBuilder>
{
    public static <T> ObjectTextTableBuilder<T> byGettersOf(Class<T> typeClass, GetterMethodToHeadingConverter nameConverter) {
        return new GetterBasedTextTableBuilder<>(typeClass, nameConverter);
    }

    public static <T> ObjectTextTableBuilder<T> byGettersOf(Class<T> typeClass) {
        return byGettersOf(typeClass, CamelCaseGetterMethodToHeadingConverter.getInstance());
    }

    protected MultilineTextStrategy multilineTextStrategy;
    protected ColumnWidthCalculator columnWidthCalculator;
    protected boolean showHeadings = true;

    /**
     * @return {@code this}
     */
    public SelfType withHeadings() {
        this.showHeadings = true;
        return (SelfType) this;
    }

    /**
     * @return {@code this}
     */
    public SelfType withoutHeadings() {
        this.showHeadings = false;
        return (SelfType) this;
    }

    /**
     * @return {@code this}
     * @see TextTable#setColumnWidthCalculator(ColumnWidthCalculator)
     */
    public SelfType calculatineColumnWidthsUsing(ColumnWidthCalculator calculator) {
        this.columnWidthCalculator = calculator;
        return (SelfType) this;
    }

    /**
     * @param widths see {@link ColumnWidthCalculator#ofValues(int...)}
     * @return {@link this}
     * @see TextTable#setColumnWidthCalculator(ColumnWidthCalculator)
     * @see ColumnWidthCalculator#ofValues(int...)
     */
    public SelfType withColumnWidths(int... widths) {
        this.columnWidthCalculator = ColumnWidthCalculator.ofValues(widths);
        return (SelfType) this;
    }

    /**
     * @return {@code this}
     * @see TextTable#setMultilineTextStrategy(MultilineTextStrategy)
     */
    public SelfType withMultilineStrategy(MultilineTextStrategy multilineStrategy) {
        this.multilineTextStrategy = multilineStrategy;
        return (SelfType) this;
    }

    public TextTable build() {
        TextTable table = new TextTable();

        if (this.multilineTextStrategy != null) {
            table.setMultilineTextStrategy(multilineTextStrategy);
        }
        else {
            table.setMultilineTextStrategy(WordsplitMultilineStrategy.getInstance());
        }

        if (this.columnWidthCalculator != null) {
            table.setColumnWidthCalculator(columnWidthCalculator);
        }

        return table;
    }
}
