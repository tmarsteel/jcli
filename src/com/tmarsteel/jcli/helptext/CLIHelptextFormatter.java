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

/**
 * A {@link HelptextFormatter} suited for the needs of CLI interfaces.
 */
public class CLIHelptextFormatter implements HelptextFormatter<String> {

    /**
     * Maximum number of characters in the output lines.
     */
    private int maxWidth;

    /**
     * The line separator to use
     */
    private char lineSeparator = '\n';

    /**
     * Returns the maximum number of characters {@link #format} will put into one output line.
     * @return The maximum number of characters {@link #format} will put into one output line.
     */
    public int getMaxWidth()
    {
        return maxWidth;
    }

    /**
     * Sets the maximum number of characters {@link #format} should put into one output line. That number should be
     * greater than 50.
     * @param maxWidth The maximum number of characters {@link #format} should put into one output line
     */
    public void setMaxWidth(int maxWidth)
    {
        this.maxWidth = maxWidth;
    }

    /**
     * Returns the line separator to use. Is used both when parsing input as well as when outputting.
     * @return The line separator to use. Is used both when parsing input as well as when outputting.
     */
    public char getLineSeparator()
    {
        return lineSeparator;
    }

    /**
     * Sets the line separator to use. Is used both when parsing input as well as when outputting.
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
        for (String example : t.usageExamples()) {
            if (t.getExecutableName().length() + 1 + example.length() < maxWidth) {
                out.append(isFirst? t.getExecutableName() : new String(new char[t.getExecutableName().length()]).replace('\0', ' '));
                out.append(' ');
                out.append(example);
                out.append('\n');
                isFirst = false;
            }
        }

        // DESCRIPTION
        out.append(wrap(t.getProgramDescription()));

        return out.toString();
    }

    /**
     * Indents all lines but the first one by {@code nSpaces} ' '  characters.
     * @param toBeIndented
     * @param nSpaces The number of spaces to indent by
     */
    protected String indentFromSecondLine(String toBeIndented, int nSpaces) {
        String[] lines = toBeIndented.split("" + lineSeparator);

        if (lines.length == 0) {
            return toBeIndented;
        }

        String pad = new String(new char[nSpaces]).replace('\0', ' ');
        StringBuilder out = new StringBuilder(toBeIndented.length() + (lines.length - 1) * nSpaces);

        for (int i = 0;i < lines.length;i++) {
            if (i != 0) {
                out.append(pad);
            }
            out.append(lines[i]);
        }

        return out.toString();
    }

    /**
     * Reformats the given string so that it does not contain more than {@link #maxWidth} characters per line, splitting
     * at whitespaces if possible.
     * @param inputString
     * @return The changed string
     */
    protected String wrap(String inputString) {
        return wrap(inputString, maxWidth);
    }

    /**
     * Reformats the given string so that it does not contain more than {@code maxWidth} characters per line, splitting
     * at whitespaces if possible.
     * @param inputString
     * @param maxWidth
     * @return The changed string
     */
    protected String wrap(String inputString, int maxWidth) {
        char[] input = inputString.toCharArray();

        StringBuilder finalOut = new StringBuilder(input.length + 15);

        StringBuilder currentLineBuilder = new StringBuilder(maxWidth);
        int lastWSSeenAtLinePos = -1; // position of the most recent whitespace in the current line
        char current;

        for (int inputPos = 0;inputPos < input.length;inputPos++) {
            current = input[inputPos];
            if (current == lineSeparator) {
                finalOut.append(currentLineBuilder);
                finalOut.append(lineSeparator);
                if (currentLineBuilder.length() != 0) currentLineBuilder = new StringBuilder(maxWidth);
                continue;
            } else if (Character.isWhitespace(current)) {
                lastWSSeenAtLinePos = currentLineBuilder.length();
                if (currentLineBuilder.length() == maxWidth) {
                    // luckily there is a space right where the line limit is ... :O
                    finalOut.append(currentLineBuilder);
                    finalOut.append(lineSeparator);
                    currentLineBuilder = new StringBuilder(maxWidth);
                    lastWSSeenAtLinePos = -1;
                    continue;
                } else {
                    // do not output leading whitespace onto a new line
                    if (currentLineBuilder.length() != 0) {
                        currentLineBuilder.append(current);
                        continue;
                    }
                }
            }

            if (currentLineBuilder.length() == maxWidth) {
                // we just hit the line limit mid-word
                // try to break at the most recent whitespace

                if (lastWSSeenAtLinePos == -1) {
                    // there is no whitespace in the line - break mid-word
                    finalOut.append(currentLineBuilder);
                    finalOut.append(lineSeparator);
                    currentLineBuilder = new StringBuilder(maxWidth);
                    currentLineBuilder.append(current);
                    continue;
                } else {
                    String afterBreak = currentLineBuilder.substring(lastWSSeenAtLinePos + 1);
                    finalOut.append(currentLineBuilder.substring(0, lastWSSeenAtLinePos));
                    finalOut.append(lineSeparator);
                    currentLineBuilder = new StringBuilder(maxWidth);
                    currentLineBuilder.append(afterBreak);
                    currentLineBuilder.append(current);
                    lastWSSeenAtLinePos = -1;
                    continue;
                }
            }

            currentLineBuilder.append(current);
        }

        finalOut.append(currentLineBuilder);
        return finalOut.toString().trim();
    }
}
