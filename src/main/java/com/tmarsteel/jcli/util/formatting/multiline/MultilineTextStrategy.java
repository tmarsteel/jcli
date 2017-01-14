package com.tmarsteel.jcli.util.formatting.multiline;

/**
 * Takes an input string of arbitrary length. Outputs a string that does not have more than a fixed given amount of
 * characters per line.
 */
public interface MultilineTextStrategy
{
    /**
     * Reformats the given string so that it does not contain more than {@code maxWidth} characters per line, splitting
     * at whitespaces if possible.
     *
     * @param inputString
     * @param maxWidth
     * @return The changed string
     * TODO: define whether a trailing linefeed is returned; adapt the formatters to it if necessary.
     */
    public String wrap(String inputString, int maxWidth, char lineSeparator);
}
