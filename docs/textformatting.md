# Text formatting tools

This library includes basic text formatting tools to aid in building rich CLI applications. This page describes
the usage and concepts.

## `Renderable`
The text formatting works by the same principle as many UI frameworks do. It is split into components that
can be rendered in a given size. The `Renderable` interface has one method:

    public String render(int maxWidth, char lineSeparator)

Whenever a text block needs to be reused in a width-constrained environment where the exact maximum width
is not known at the time when the component is being built, this interface has usage.
See the next paragraph for a good example.

## Text wrapping
Code that wraps text at word boundaries (or within words if necessary) to make the text fit a given maximum width:

    Lorem ipsum dolor sit amet -> wrap to 15 chars/line -> Lorem ipsum
                                                           dolor sit amet


    WordwrapMultilineStrategy.getInstance().wrap("Lorem ipsum dol...", 15, '\n');

`MultilineTextStrategy` is a simple interface for that function. All other code in the library uses it so that more
sophisticated algorithsm for text wrapping can be implemented when necessary.

There is a method `renderableOf(String)` with a default implementation on that interface. It returns a `Renderable` that,
wenn its `render` method is invoked, passes the call on to the `wrap` method of the `MultilineTextStrategy` instance.
This allows users to pass Strings around as `Renderables` that can be squeezed into shape where necessary:

    // in one part of the code, a string for display in a text interface is defined
    Renderable descriptionRenderable = WordwrapMultilineStrategy.getInstance().renderableOf(lengthyDescrition);

    // now, that instance of Renderable is passed to other code that wants to display the text
    // in a column of with 50 chars:

    String wrappedText = descriptionRenderable.render(50, '\n');

## ListFormatter
The name says it all. In goes a `List` of `String`s and out comes a `Renderable` (or even the final string if so desired):

    List<String> list = Arrays.asList("Foo", "Bar", "Lorem ipsum dolor");

    ListFormatter listForamtter = new ListFormatter("*");
    // overloads exist to pass in the desired bullet point and multiline strategy

    Renderable listRenderable = listFormatter.renderableOf(list);
    String renderedString = listFormatter.format(list, 15, '\n');

`listRenderable.render(15, '\n')` yields this string:

    - Foo
    - Bar
    - Lorem ipsum
      dolor

## `TextTable`
TextTable can be used to build text-based tables. To build one it only takes the raw table data as a `List<List<Renderable>>`:

    TextTable table = new TextTable();
    table.addRow(renderableRow1Column1, renderableRow1Column2, renderableRow1Column3)
         .addRow(renderableRow2Column1, renderableRow2Column2, renderableRow2Column3)
         .addRow(renderableRow3Column1, renderableRow3Column2, renderableRow3Column3);

The table itself is a `Renderable` and can be reused or rendered to fit a certain width. The look is either that one
known from MySQL CLI or without borders:

    +-----+-------+-------+   or without borders:   Foo  Bar  Price
    | Foo | Bar   | Price |
    +-----+-------+-------+                         123  Hey!!   $3
    | 123 | Hey!! |    $3 |
    +-----+-------+-------+                         ABC  o.0    $50
    | ABC | o.0   |   $50 |
    +-----+-------+-------+

See `TextTable#setHasBorders(boolean)` and `TextTable#hasBorders()` for borders and `TextTable#setHeadings(Renderable...)`
for headings.

`TextTable` has shortcut methods if you want to use simple text only. The use `WordsplitMultilineStrategy` to build the
`Renderable`s:

     table.setHeadings("Column 1", "Column 2", "Column 3")
          .addRow("Cell", "Cell", "Cell")
          .addRow("Cell", "Cell", "Cell");

### Column widths
`TextTable` distinguishes between columns with fixed width and dynamic columns. When rendering, it calculates the space
needed for the fixed-width columns. The remaining space is equally split up between the remaining dynamic columns.

By default, all columns are dynamic. Defining the widths of fixed columns as well as employing a more intelligent algorithm
for assigning widths to columns works by the same mechanic: a `ColumnWidthCalculator`. Again, this is a SAM interface:

    /**
     * Given the total number of columns in a table and the index of one particular column, returns the desired
     * width of that column.
     *
     * @param columnIndex The index of the column to determine the width for.
     * @param nColumns    Total number of columns in the table
     * @return Width of the column. 0 + negative numbers indicate dynamic width (split across all dynamic columns).
     */
    int getColumnWidth(int columnIndex, int nColumns)

An instance of this interface can be passed to a `TextTable`:

    TextTable table = new TextTable();
    table.setColumnWidthCalculator(new MySuperCustomColumnWidthCalculator());

In most cases, the split-all-nonstatic-space-equally-across-all-dynamic-columns approach should work. In that case you
can use the static method `ColumnWidthCalculator.ofValues(int...)`. It will return the values given from its
`getColumnWidth` method in the order you specified:

    table.setColumnWidthCalculator(ColumnWidthCalculator.ofValues(30, 30, -1, 50));
    // column 1 + 2 -> fixed width of 30
    // column 4 -> fixed width of 50
    // column 3 gets all the space that is left

    table.setColumnWidthCalculator(ColumnWidthCalculator.ofValues(-1, 30, -1, 50));
    // column 2 -> fixed width of 30
    // column 4 -> fixed width of 50
    // with a maximum size of 120 chars that leaves 40 chars for the borders, padding and the two dynamic columns
    // that would be 13 chars for borders and padding, leaving 27 for the two columns
    // 27 / 2 = 13 with 1 remaing
    // the remaining characters are split across the columns but at least one column will be smaller than others
    // in this case, column 1 gets 14 chars and column 3 gets 13
    // note that, with a maximum size of 140 it would be 24 and 23 chars for column 1 and 3, respectively

If you wanted to implement your own algorithm to calculate optimal widths for columns you can use that interface. E.g.
pass your table data to your algorithm and have it determine optimal column widths. Those can then be passed on to
`TextTable` with that interface.