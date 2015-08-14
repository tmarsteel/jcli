package com.wisper.cli;

/**
 * Represents a CLI environment.
 * @author Tobias Marstaller
 */
public class Environment
{
    public static final Environment WINDOWS = new Environment('^', "/", "/");
    public static final Environment UNIX = new Environment('\\', "-", "--");
    
    protected String flagMarker;
    protected String optionMarker;
    protected char escapeChar;

    public Environment(char escapeChar, String flagMarker, String optionMarker)
    {
        this.escapeChar = escapeChar;
        this.flagMarker = flagMarker;
        this.optionMarker = optionMarker;
    }
    
    /**
     * Returns the sequence of characters that flags are prefixed with.
     * For Example <code>--</code> for such flags: <code>--verbose</code>.
     * @return the sequence of characters that flags are prefixed with.
     */
    public String getFlagMarker()
    {
        return flagMarker;
    }

    /**
     * Sets the sequence of characters that flags are prefixed with.
     * For Example <code>--</code> for such flags: <code>--verbose</code>.
     * @param flagMarker The sequence of characters that flags are prefixed with.
     */
    public void setFlagMarker(String flagMarker)
    {
        this.flagMarker = flagMarker;
    }

    /**
     * Returns the sequence of characters that options are prefixed with.
     * For Example <code>-</code> for such options: <code>-infile foo.txt</code>.
     * @return the sequence of characters that options are prefixed with.
     */
    public String getOptionMarker()
    {
        return optionMarker;
    }

    /**
     * Sets the sequence of characters that options are prefixed with.
     * For Example <code>-</code> for such options: <code>-infile foo.txt</code>.
     * @param optionMarker The sequence of characters that options are prefixed with.
     */
    public void setOptionMarker(String optionMarker)
    {
        this.optionMarker = optionMarker;
    }

    /**
     * Returns the character used to escape other functional characters. On DOS/NT
     * systems this is <code>^</code> by default, <code>\</code> on UNIX-Systems.
     * @return The character used to escape other functional characters.
     */
    public char getEscapeChar()
    {
        return escapeChar;
    }

    /**
     * Sets the character used to escape other functional characters.
     * @param escapeChar The character used to escape other functional characters.
     */
    public void setEscapeChar(char escapeChar)
    {
        this.escapeChar = escapeChar;
    }
}
