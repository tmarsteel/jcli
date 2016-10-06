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

import com.tmarsteel.jcli.filter.Filter;
import com.tmarsteel.jcli.validation.ValidationException;

/**
 * Represents any parameter passed to the program besides flags and options.
 * @author tmarsteel
 */
public class Argument
{
    /**
     * Value filter
     */
    protected Filter valueFilter;

    /**
     * Default value
     */
    protected Object defaultValue;

    /**
     * The index of this argument in the argument list
     */
    protected final int index;

    /**
     * Identifier of this argument for index-independent access
     */
    protected final String accessName;

    /**
     * A description used for helptexts
     */
    protected String description = "";

    /**
     * Whether this argument is required to be specified
     */
    protected boolean required = false;

    /**
     * Whether this argument accepts multiple values
     */
    protected boolean variadic = false;
    
    /**
     * Constructs a new required argument placed at index <code>index</code> that can be
     * accessed by <code>accessName</code> on code level.
     * @param accessName The name this argument should be accessed with.
     * @param index index The index this argument is placed at.
     * @throws IllegalArgumentException If index is less than 0.
     */
    public Argument(String accessName, int index)
    {
        this.index = index;
        this.accessName = accessName;
        this.required = true;
    }
    
    /**
     * Constructs a new non-required argument placed at index <code>index</code> that can be
     * accessed by <code>accessName</code> on code level.
     * @param accessName The name this argument should be accessed with.
     * @param index index The index this argument is placed at.
     * @param defaultValue The default value for this argument.
     * @throws IllegalArgumentException If index is less than 0.
     */
    public Argument(String accessName, int index, Object defaultValue)
    {
        this(accessName, index);
        this.defaultValue = defaultValue;
        this.required = false;
    }
    
    /**
     * Constructs a new required argument placed at index <code>index</code> that can be
     * accessed by <code>accessName</code> on code level.
     * @param accessName The name this argument should be accessed with.
     * @param index index The index this argument is placed at.
     * @param filter A filter to validate input for this argument.
     * @throws IllegalArgumentException If index is less than 0.
     */
    public Argument(String accessName, int index, Filter filter)
    {
        this(accessName, index, null, filter);
        this.required = true;
    }
    
    /**
     * Constructs a new non-required argument placed at index <code>index</code> that can be
     * accessed by <code>accessName</code> on code level.
     * @param accessName The name this argument should be accessed with.
     * @param index index The index this argument is placed at.
     * @param defaultValue The default value for this argument.
     * @param filter A filter to validate input for this argument.
     * @throws IllegalArgumentException If index is less than 0.
     */
    public Argument(String accessName, int index, Object defaultValue,
        Filter filter)
    {
        this.accessName = accessName;
        this.index = index;
        this.valueFilter = filter;
        this.required = false;
        
        if (valueFilter != null)
        {
            if (defaultValue instanceof String)
            {
                try
                {
                    this.defaultValue = filter.parse((String) defaultValue);
                }
                catch (ValidationException ex)
                {
                    this.defaultValue = defaultValue;
                }
            }
            else
            {
                this.defaultValue = defaultValue;
            }
        }
    }
    
    /**
     * Returns the index this argument is placed at.
     * @return The index this argument is placed at.
     */
    public int getIndex()
    {
        return index;
    }
    
    /**
     * Returns the string this argument can be accessed by on code level.
     * @return The string this argument can be accessed by on code level.
     */
    public String getIdentifier()
    {
        return accessName;
    }
    
    /**
     * Returns the default value for this argument or <code>null</code> if none
     * is set.
     * @return The default value for this argument.
     */
    public Object getDefaultValue()
    {
        return defaultValue;
    }
    
    /**
     * Parses the given value to the type required by this argument.
     * @param input The value to be parsed.
     * @return The parsed value.
     * @throws ValidationException If value does not match this filter.
     */
    public Object parse(String input)
        throws ValidationException
    {
        return valueFilter == null? input : valueFilter.parse(input);
    }

    /**
     * Returns a human-readable description of this argument.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets a human-readable description of this argument.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns whether this arugment must be specified in inputs.
     */
    public boolean isRequired()
    {
        return required;
    }

    /**
     * Sets whether this argument must be specified in inputs.
     */
    public void setRequired(boolean required)
    {
        this.required = required;
    }

    /**
     * Returns whether inputs can specify multiple values for this argument
     */
    public boolean isVariadic()
    {
        return variadic;
    }

    /**
     * Sets whether inputs can specify multiple values for this argument.
     */
    public void setVariadic(boolean variadic)
    {
        this.variadic = variadic;
    }

    /**
     * Returns the filter configured for this argument, if any. Otherwise null.
     */
    public Filter getFilter() {
        return this.valueFilter;
    }
}
