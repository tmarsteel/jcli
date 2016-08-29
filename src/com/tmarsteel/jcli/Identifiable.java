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

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Superclass for flags and options that handles aliasing.
 * @author Tobias Marstaller
 */
public abstract class Identifiable
{
    private List<String> names;
    private String description;

    /**
     * Creates new identifiable; the first element in <code>names</code> becomes
     * the primary identifier (see {@link #getPrimaryIdentifier()}.
     * @param names The names of this identifiable
     * @throws IllegalArgumentException If names is empty.
     */
    public Identifiable(String... names)
    {
        this(Arrays.asList(names));
    }

    /**
     * Creates new identifiable; the first element in <code>names</code> becomes
     * the primary identifier (see {@link #getPrimaryIdentifier()}.
     * @param names The names of this identifiable
     * @throws IllegalArgumentException If names is empty.
     */
    public Identifiable(List<String> names)
    {
        if (names.isEmpty())
        {
            throw new IllegalArgumentException("At least one name must be given.");
        }

        this.names = names;
    }

    /**
     * Returns whether the given name identifies this flag/option.
     * @return Whether the given name identifies this flag/option.
     */
    public boolean isIdentifiedBy(String name)
    {
        return names.contains(name);
    }

    /**
     * Returns all the identifiers of this object.
     * @return All the identifiers of this object.
     */
    public String[] names() { return names.toArray(new String[names.size()]); }

    /**
     * Returns the primary identifier for this option.
     * @return The primary identifier for this option.
     */
    public String getPrimaryIdentifier()
    {
        return names.get(0);
    }

    /**
     * Returns whether any of this' identifiers is ambigous with any
     * of the identifiers of the given {@link Identifiable}
     * @return Whether any of this' identifiers is ambigous with any
     * of the identifiers of the given {@link Identifiable}
     */
    public boolean isAmbigousWith(Identifiable o)
    {
        Iterator<String> myIt = names.iterator();

        while (myIt.hasNext())
        {
            if (o.names.contains(myIt.next()))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Sets a string for this {@link Identifiable}.
     * @param desc A description for this {@link Identifiable}
     */
    public void setDescription(String desc)
    {
        this.description = desc;
    }

    /**
     * Returns a string representing this {@link Identifiable}.
     * @return A string representing this {@link Identifiable}.
     */
    public String getDescription()
    {
        return this.description;
    }
}
