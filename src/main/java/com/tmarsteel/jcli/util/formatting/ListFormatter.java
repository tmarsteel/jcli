package com.tmarsteel.jcli.util.formatting;

import com.tmarsteel.jcli.util.formatting.multiline.MultilineTextStrategy;
import com.tmarsteel.jcli.util.formatting.multiline.WordsplitMultilineStrategy;

import java.util.Objects;

/**
 * Formats a given list of strings as a list. Optionally, limits the number of characters a line can be wide, splits the
 * text using a {@link MultilineTextStrategy} and indents the lines of the items.
 *
 * Basic output:
 * <pre>
 * &lt;---            maximum width           ---&gt;
 * - Item 1
 * - Item 2
 * - Item 3
 * - Item 4s text overflows the maximum width. The
 *   second and all following lines are thus
 *   indented by 2 spaces.
 * * Item 5
 * </pre>
 */
public final class ListFormatter
{
    private String bulletPoint;
    private MultilineTextStrategy multilineTextStrategy;
    private final Indentation multilineIndentation;

    public ListFormatter() {
        this(WordsplitMultilineStrategy.getInstance());
    }

    public ListFormatter(String bulletPoint) {
        this(bulletPoint, WordsplitMultilineStrategy.getInstance());
    }

    public ListFormatter(String bulletPoint, MultilineTextStrategy multilineTextStrategy) {
        this.multilineTextStrategy = Objects.requireNonNull(multilineTextStrategy);
        this.bulletPoint = bulletPoint;
        this.multilineIndentation = new Indentation(' ', bulletPoint.length() + 1);
    }

    public ListFormatter(MultilineTextStrategy multilineTextStrategy) {
        this("-", multilineTextStrategy);
    }

    public void setMultilineTextStrategy(MultilineTextStrategy multilineTextStrategy) {
        this.multilineTextStrategy = Objects.requireNonNull(multilineTextStrategy);
    }

    public void setBulletPoint(String bulletPoint) {
        this.bulletPoint = bulletPoint;
    }

    /**
     * Formats the given items as a list.
     * @param items The items to format
     * @param maxWidth The maximum width per line (including bulletpoint and spacer between bulletpoint and item text)
     * @param lineSeparator The line separator between the lines
     * @return The formatted liste, without trailing newline
     */
    public String format(Iterable<String> items, int maxWidth, char lineSeparator) {
        StringBuilder out = new StringBuilder();
        for (String item : items) {
            out.append(bulletPoint);
            out.append(' ');

            if (item.length() + bulletPoint.length() + 1 <= maxWidth) {
                out.append(item);
                out.append(lineSeparator);
            }
            else {
                String rawText = multilineTextStrategy.wrap(item, maxWidth - bulletPoint.length() - 1, lineSeparator);
                out.append(multilineIndentation.indent(
                    rawText,
                    Indentation.Strategy.INDENT_SECOND_TO_LAST,
                    lineSeparator
                ));
            }
        }

        return out.toString().trim();
    }

    /**
     * Returns a {@link Renderable} that renders the given items with the configuration of this {@link ListFormatter}
     * @param items The items to format
     * @return A {@link Renderable} that delegates to {@link #format(Iterable, int, char)}
     */
    public Renderable renderableOf(final Iterable<String> items) {
        return (maxWidth, lineSeparator) -> format(items, maxWidth, lineSeparator);
    }
}
