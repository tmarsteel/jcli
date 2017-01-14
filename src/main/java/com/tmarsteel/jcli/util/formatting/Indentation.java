package com.tmarsteel.jcli.util.formatting;

/**
 * Models indentation information
 */
public class Indentation
{
    private final char indentationChar;
    private final int indentationAmount;

    public Indentation(short indentationAmount) {
        this(' ', indentationAmount);
    }

    public Indentation(char indentationChar, int indentationAmount) {
        this.indentationChar = indentationChar;
        this.indentationAmount = indentationAmount;
    }

    public char getIndentationChar()
    {
        return indentationChar;
    }

    public int getIndentationAmount()
    {
        return indentationAmount;
    }

    /**
     * Returns an indentation with the same {@link #indentationChar} as this and but {@code amount} added to the
     * {@link #indentationAmount}
     * @param amount The additional amount to indent by.
     * @return A new indentation with the additional {@link #indentationAmount}
     */
    public Indentation plus(short amount) {
        return new Indentation(this.indentationChar, (short) (this.indentationAmount + amount));
    }

    /**
     * Prepends each line of the given string by {@link #indentationAmount} {@link #indentationChar}s. Lines are
     * split and joint by occurences of {@code lineSeparator}.
     * @param toBeIndented The string to be indented
     * @param indentationStrategy What lines to indent
     * @param lineSeparator The line separator to use.
     * @return The indented string.
     */
    public String perform(String toBeIndented, Strategy indentationStrategy, char lineSeparator) {
        if (indentationStrategy == Strategy.NO_INDENTATION) return toBeIndented;
        String[] lines = toBeIndented.split("" + lineSeparator);

        if (lines.length == 0)
        {
            return toBeIndented;
        }

        String pad = new String(new char[indentationAmount]).replace('\0', indentationChar);
        StringBuilder out = new StringBuilder(toBeIndented.length() + (lines.length - 1) * indentationAmount);

        for (int i = 0; i < lines.length; i++)
        {
            if (i == 0 && indentationStrategy != Strategy.INDENT_SECOND_TO_LAST)
            {
                out.append(pad);
            }
            out.append(lines[i]);
            out.append(lineSeparator);
        }

        return out.toString();
    }

    public enum Strategy {
        /** Denotes that no indentation should be applied */
        NO_INDENTATION,

        /** Denotes that all lines should be indented */
        INDENT_ALL,

        /** Denotes that all lines except the first line should be indented */
        INDENT_SECOND_TO_LAST;
    }
}
