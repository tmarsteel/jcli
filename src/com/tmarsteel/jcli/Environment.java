/* 
 * Copyright (C) 2015 Tobias Marstaller
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
package com.tmarsteel.jcli;

import java.io.File;

/**
 * Represents a CLI environment.
 * @author tmarsteel
 */
public class Environment
{
    public static final Environment DOS = new Environment('^', "/", "/");
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
    
    /**
     * Returns the environment this application is running in. This is one of the
     * classes constants <code>WINDOWS</code> or <code>UNIX</code>.
     */
    public static Environment getEnvironment()
    {
        return File.separatorChar == '/'? UNIX : DOS;
    }
}
