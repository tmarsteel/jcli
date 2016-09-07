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
package com.tmarsteel.jcli.filter;

import com.tmarsteel.jcli.ParseException;
import com.tmarsteel.jcli.validation.ValidationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Accepts only values from a given set of possible values.
 * @author tmarsteel
 */
public class SetFilter implements Filter
{
    protected final Collection<String> options;
    protected boolean caseSensitive = false;

    /**
     * Creates a new case insensitive filter treating the given strings as valid.
     * @param options The strings to accept.
     */
    public SetFilter(String... options)
    {
        this(Arrays.asList(options));
    }

    /**
     * Creates a new filter treating the given strings as valid.
     * @param caseSensitive Whether the filter should be case sensitive
     * @param options The strings to accept.
     */
    public SetFilter(boolean caseSensitive, String... options)
    {
        this(options);
        this.caseSensitive = caseSensitive;
    }

    /**
     * Creates a new case insensitive filter treating the given strings as valid.
     * @param options The strings to accept.
     */
    public SetFilter(Collection<String> options)
    {
        if (options.isEmpty())
        {
            throw new IllegalArgumentException("Need to specify at least one value");
        }
        this.options = options;
    }

    /**
     * Creates a new filter treating the given strings as valid.
     * @param caseSensitive Whether the filter should be case sensitive
     * @param options The strings to accept.
     */
    public SetFilter(boolean caseSensitive, Collection<String> options)
    {
        this(options);
        this.caseSensitive = caseSensitive;
    }

    @Override
    public Object parse(String value)
        throws ValidationException
    {
        final Iterator<String> it = options.iterator();
        while (it.hasNext())
        {
            final String cur = it.next();
            if (caseSensitive)
            {
                if (cur.equals(value))
                {
                    return cur;
                }
            }
            else
            {
                if (cur.equalsIgnoreCase(value))
                {
                    return cur;
                }
            }
        }
        throw new ValidationException(value + " is not a possible value.");
    }

    /**
     * Returns whether this filter is case sensitive.
     * @return Whether this filter is case sensitive.
     */
    public boolean isCaseSensitive() 
    {
        return caseSensitive;
    }

    /**
     * Sets whether this filter is case sensitive.
     * @param caseSensitive Whether this filter is case sensitive.
     */
    public void setCaseSensitive(boolean caseSensitive)
    {
        this.caseSensitive = caseSensitive;
    }

    /**
     * Returns a collection of the options this filter treats as valid. Elements
     * can be added/removed from this collection at will and the filter will
     * adapt to the changes.
     * @return A collection of the options this filter treats as valid.
     */
    public Collection<String> options()
    {
        return this.options;
    }
}
