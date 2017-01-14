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
        this(WordsplitMultilineStrategy.getInstance(), bulletPoint);
    }

    public ListFormatter(MultilineTextStrategy multilineTextStrategy) {
        this(multilineTextStrategy, "-");
    }

    public ListFormatter(MultilineTextStrategy multilineTextStrategy, String bulletPoint) {
        this.multilineTextStrategy = Objects.requireNonNull(multilineTextStrategy);
        this.bulletPoint = bulletPoint;
        this.multilineIndentation = new Indentation(' ', bulletPoint.length() + 1);
    }

    public void setMultilineTextStrategy(MultilineTextStrategy multilineTextStrategy) {
        this.multilineTextStrategy = Objects.requireNonNull(multilineTextStrategy);
    }

    public void setBulletPoint(String bulletPoint) {
        this.bulletPoint = bulletPoint;
    }

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

        return out.toString();
    }
}
