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
import com.tmarsteel.jcli.Flag;
import com.tmarsteel.jcli.Option;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Data-Object that holds all that is needed for a helptext.
 */
public class Helptext
{
    private String executableName = "<executable>";

    /**
     * general usage examples, omitting the &lt;executable&gt; part
     */
    private final Set<String> usageExamples = new HashSet<>(Arrays.asList("[-flags] [--options values] arguments..."));

    /**
     * Program description, displayed along the {@link #usageExamples} (as in e.g. {@code ls --help}).
     */
    private String programDescription = "";

    /**
     * Notes displayed at the bottom of the helptext
     */
    private String notes;

    /**
     * All options known to this helptext
     */
    private final Set<Option> options = new HashSet<>();

    /**
     * All flags known to this helptext
     */
    private final Set<Flag> flags = new HashSet<>();

    /**
     * All the arguments knonw to this helptext
     */
    private final Set<Argument> arguments = new HashSet<>();

    public String getExecutableName()
    {
        return executableName;
    }

    public void setExecutableName(String executableName)
    {
        this.executableName = executableName;
    }

    public Set<String> usageExamples()
    {
        return usageExamples;
    }

    public String getProgramDescription()
    {
        return programDescription;
    }

    public void setProgramDescription(String programDescription)
    {
        this.programDescription = programDescription;
    }

    public Set<Flag> flags() {
        return flags;
    }

    public Set<Option> options()
    {
        return options;
    }

    public Set<Argument> arguments()
    {
        return arguments;
    }

    public String getNotes()
    {
        return notes;
    }

    public void setNotes(String notes)
    {
        this.notes = notes;
    }
}
