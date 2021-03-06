package com.tmarsteel.jcli.util.formatting.multiline;

import com.tmarsteel.jcli.util.formatting.Renderable;

/**
 * Takes an input string of arbitrary length. Outputs a string that does not have more than a fixed given amount of
 * characters per line.
 */
public interface MultilineTextStrategy
{
    /**
     * Reformats the given string so that it does not contain more than {@code maxWidth} characters per line.
     *
     * @param inputString The string to wrap
     * @param maxWidth The maximum number of characters per line
     * @return The wrapped string. Does not have a trailing newline.
     */
    String wrap(String inputString, int maxWidth, char lineSeparator);

    /**
     * @return A {@link Renderable} that delegates to {@link #wrap(String, int, char)}
     */
    default Renderable renderableOf(String inputString) {
        return (maxWidth, lineSeparator) -> this.wrap(inputString, maxWidth, lineSeparator);
    }
}
