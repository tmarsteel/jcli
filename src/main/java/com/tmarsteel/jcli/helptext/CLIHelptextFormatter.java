/*
 * Copyright (C) 2016 Tobias Marstaller
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package com.tmarsteel.jcli.helptext;

import com.tmarsteel.jcli.Argument;
import com.tmarsteel.jcli.Filtered;
import com.tmarsteel.jcli.Identifiable;
import com.tmarsteel.jcli.filter.*;
import com.tmarsteel.jcli.util.ClassHierarchyComparator;
import com.tmarsteel.jcli.util.formatting.Indentation;
import com.tmarsteel.jcli.util.formatting.ListFormatter;
import com.tmarsteel.jcli.util.formatting.Renderable;
import com.tmarsteel.jcli.util.formatting.multiline.MultilineTextStrategy;
import com.tmarsteel.jcli.util.formatting.multiline.WordsplitMultilineStrategy;
import com.tmarsteel.jcli.util.formatting.table.ColumnWidthCalculator;
import com.tmarsteel.jcli.util.formatting.table.TextTable;

import java.util.*;

import static com.tmarsteel.jcli.util.formatting.FormattingUtils.*;

/**
 * A {@link HelptextFormatter} suited for the needs of CLI interfaces.
 */
public class CLIHelptextFormatter implements FilterAwareHelptextFormatter<String>
{

    /**
     * The filter descriptors to be used.
     */
    private SortedMap<Class<?>, FilterDescriptor> filterDescriptors = new TreeMap<>(new ClassHierarchyComparator());

    /** This is fixed for now; maybe customizable in a later release */
    private final ListFormatter constraintListFormatter = new ListFormatter("-");

    /**
     * Maximum number of characters in the output lines.
     */
    private int maxWidth = 80;

    /**
     * The line separator to use
     */
    private char lineSeparator = '\n';

    private MultilineTextStrategy multilineTextStrategy = WordsplitMultilineStrategy.getInstance();

    /**
     * Returns the maximum number of characters {@link #format} will put into one output line.
     *
     * @return The maximum number of characters {@link #format} will put into one output line.
     */
    public int getMaxWidth()
    {
        return maxWidth;
    }

    /**
     * Sets the maximum number of characters {@link #format} should put into one output line. That number should be
     * greater than 50.
     *
     * @param maxWidth The maximum number of characters {@link #format} should put into one output line
     */
    public void setMaxWidth(int maxWidth)
    {
        this.maxWidth = maxWidth;
    }

    /**
     * Returns the line separator to use. Is used both when parsing input as well as when outputting.
     *
     * @return The line separator to use. Is used both when parsing input as well as when outputting.
     */
    public char getLineSeparator()
    {
        return lineSeparator;
    }

    /**
     * Sets the line separator to use. Is used both when parsing input as well as when outputting.
     *
     * @param lineSeparator the line separator to use. Is used both when parsing input as well as when outputting.
     */
    public void setLineSeparator(char lineSeparator)
    {
        this.lineSeparator = lineSeparator;
    }

    @Override
    public String format(Helptext t)
    {
        StringBuilder out = new StringBuilder(1000);

        // USAGE
        out.append("Usage: ");
        boolean isFirst = true;
        for (String example : t.usageExamples())
        {
            if (t.getExecutableName().length() + 1 + example.length() < maxWidth)
            {
                out.append(isFirst ? t.getExecutableName() : new String(new char[t.getExecutableName().length() + 7]).replace('\0', ' '));
                out.append(' ');
                out.append(example);
                out.append('\n');
                isFirst = false;
            }
        }

        // DESCRIPTION
        out.append(wrap(t.getProgramDescription()));
        out.append(lineSeparator);
        out.append(lineSeparator);

        // OPTIONS and FLAGS
        if (t.options().size() > 0)
        {
            out.append("-- Options --");
            out.append(lineSeparator);
            out.append(toTable(t.options()));
            out.append(lineSeparator);
        }
        if (t.flags().size() > 0)
        {
            out.append(lineSeparator);
            out.append(lineSeparator);
            out.append("-- Flags --");
            out.append(lineSeparator);
            out.append(toTable(t.flags()));
            out.append(lineSeparator);
        }
        if (t.arguments().size() > 0)
        {
            out.append(lineSeparator);
            out.append(lineSeparator);
            out.append("-- Arguments --");
            out.append(lineSeparator);
            out.append(toTable_Arguments(t.arguments()));
            out.append(lineSeparator);
        }

        if (t.getNotes() != null && !t.getNotes().isEmpty())
        {
            out.append(lineSeparator);
            out.append(lineSeparator);
            out.append(wrap(t.getNotes()));
            out.append(lineSeparator);
        }

        return out.toString();
    }

    @Override
    public <C extends Filter> void setFilterDescriptor(Class<C> filterClass, FilterDescriptor descriptor)
    {
        Objects.requireNonNull(filterClass);

        if (descriptor == null) {
            filterDescriptors.remove(filterClass);
        } else {
            filterDescriptors.put(filterClass, descriptor);
        }
    }

    /**
     * Indents all lines but the first one by {@code nSpaces} ' '  characters.
     *
     * @param toBeIndented
     * @param nSpaces      The number of spaces to indent by
     */
    protected String indentFromSecondLine(String toBeIndented, int nSpaces)
    {
       return (new Indentation(nSpaces)).indent(toBeIndented, Indentation.Strategy.INDENT_SECOND_TO_LAST, lineSeparator);
    }

    protected String toTable(Collection<? extends Identifiable> identifiables)
    {
        final int leftColWidth = getLongestNameLength(identifiables);

        TextTable textTable = new TextTable();
        textTable.setHasBorders(false);
        textTable.setColumnWidthCalculator(ColumnWidthCalculator.ofValues(leftColWidth, -1));

        identifiables
            .stream()
            // sort by primary identifier ascending
            .sorted(Comparator.comparing(Identifiable::getPrimaryIdentifier))
            .forEach(identifiable ->
            {
                String oneNamePerLine = join(identifiable.names(), "" + lineSeparator);
                Renderable namesRenderable = WordsplitMultilineStrategy.getInstance().renderableOf(oneNamePerLine);
                Renderable descriptionRenderable = (descriptionMaxWidth, lineSeparator) ->
                {
                    // description
                    StringBuilder description = new StringBuilder(wrap(identifiable.getDescription(), descriptionMaxWidth));
                    if (description.length() == 0)
                    {
                        description.append("<no description>");
                    }

                    // constraints
                    if (identifiable instanceof Filtered)
                    {
                        List<String> constraintDescriptions = getConstraintDescriptions(((Filtered) identifiable).getFilter());
                        if (!constraintDescriptions.isEmpty())
                        {
                            description.append(lineSeparator);
                            description.append(lineSeparator);
                            description.append("Constraints:");
                            description.append(lineSeparator);
                            description.append(constraintListFormatter.format(constraintDescriptions, maxWidth, lineSeparator));
                        }
                    }

                    return description.toString();
                };

                textTable.addRow(namesRenderable, descriptionRenderable);
            });

        return textTable.render(maxWidth, lineSeparator);
    }

    protected String toTable_Arguments(Collection<? extends Argument> args)
    {
        // the left column is the arg index with a # prepended
        int leftColWidth = Integer.toString(args.size(), 10).length() + 1;

        TextTable textTable = new TextTable();
        textTable.setHasBorders(false);
        textTable.setColumnWidthCalculator(ColumnWidthCalculator.ofValues(leftColWidth, -1));

        args.stream()
            .sorted(Comparator.comparing(Argument::getIdentifier))
            .forEach(arg ->
            {
                Renderable indexRenderable = WordsplitMultilineStrategy.getInstance().renderableOf("#" + arg.getIndex());
                Renderable descriptionRenderable = (descriptionMaxWidth, lineSeparator) -> {
                    StringBuilder description = new StringBuilder(wrap(arg.getDescription(), descriptionMaxWidth));

                    if (arg.getFilter() != null) {
                        List<String> constraintDescriptions = getConstraintDescriptions(arg.getFilter());
                        if (!constraintDescriptions.isEmpty()) {
                            description.append(lineSeparator);
                            description.append(lineSeparator);
                            description.append("Constraints:");
                            description.append(lineSeparator);
                            description.append(constraintListFormatter.format(constraintDescriptions, descriptionMaxWidth, lineSeparator));
                        }
                    }

                    return description.toString();
                };

                textTable.addRow(indexRenderable, descriptionRenderable);
            });

        return textTable.render(maxWidth, lineSeparator);
    }

    protected String wrap(String inputString)
    {
        return wrap(inputString, maxWidth);
    }

    protected String wrap(String inputString, int maxWidth) {
        return this.multilineTextStrategy.wrap(inputString, maxWidth, lineSeparator);
    }

    /**
     * Returns the constraints of the given filter as returned by the appropriate filter descriptor chosen from
     * {@link #filterDescriptors}.
     */
    protected List<String> getConstraintDescriptions(Filter filter) {
        if (filter == null) {
            return Collections.emptyList();
        }

        for (Map.Entry<Class<?>, FilterDescriptor> entry : filterDescriptors.entrySet())
        {
            if (entry.getKey().isAssignableFrom(filter.getClass())) {
                // first entry that is abstract enough for the filter
                List<String> constraints = entry.getValue().describe(filter);
                if (constraints == null) {
                    constraints = Collections.emptyList();
                }
                return constraints;
            }
        }

        return Collections.emptyList();
    }

    protected int getLongestNameLength(Collection<? extends Identifiable> identifiables)
    {
        int max = 0;
        for (Identifiable e : identifiables)
        {
            int localMax = Arrays.stream(e.names()).mapToInt(String::length).max().getAsInt();
            if (localMax > max)
            {
                max = localMax;
            }
        }

        return max;
    }
}
