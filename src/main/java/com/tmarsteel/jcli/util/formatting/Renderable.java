package com.tmarsteel.jcli.util.formatting;

/**
 * A UI element that can be rendered to markdown.
 */
public interface Renderable
{
    /**
     * Renders this element. The result will not occupy more than {@code maxWidth} characters per line. Lines in the
     * output are separated using {@code lineSeparator}.
     * @param maxWidth Maximum characters per line
     * @param lineSeparator Line separator
     * @return The rendered element / component
     */
    String render(int maxWidth, char lineSeparator);
}
