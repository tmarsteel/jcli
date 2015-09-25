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
 * Represents a Option or Flag, e.g. <code>--log-level 2</code> or <code>-verbose</code>
 * @author tmarsteel
 */
public class Option extends Identifiable
{
    private Filter valueFilter;
    private Object defaultValue;
    private boolean isRequired = false;
    
    /**
     * Constructs a new required option.
     * @param names The names of the option.
     */
    public Option(String... names)
    {
        this(null, null, names);
        this.isRequired = true;
    }
    
    /**
     * Constructs a new option that is <b>not required</b>, whichs values are
     * checked by <code>filter</code> and that defaults to <code>defaultValue</code>.
     * @param names The names of the option.
     */
    public Option(Filter filter, Object defaultValue, String... names)
    {
        super(names);
        this.valueFilter = filter;
        
        if (defaultValue != null && defaultValue instanceof String)
        {
            try
            {
                this.defaultValue = parse((String) defaultValue);
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
    
    /**
     * Parses the given value to the type required by this option.
     * @param value The value to be parsed.
     * @return The parsed value.
     * @throws ValidationException If value does not match this filter.
     */
    public Object parse(String value)
        throws ValidationException
    {
        return valueFilter == null? value : valueFilter.parse(value);
    }

    /**
     * Sets the default value of this option; unless <code>defV</code> is null,
     * the option is also made non-required.
     * @param defV The default value for this option.
     */
    public void setDefaultValue(Object defV)
    {
        if (defV instanceof String)
        {
            try
            {
                this.defaultValue = this.parse((String) defV);
            }
            catch (ValidationException ex)
            {
                this.defaultValue = defV;
            }
        }
        else
        {
            this.defaultValue = defV;
        }
        
        if (defV != null)
        {
            this.setRequired(false);
        }
    }
    
    /**
     * Returns the default value for this option or <code>null</code> if none is
     * set or this is a flag.
     * @return The default value for this option.
     */
    public Object getDefaultValue()
    {
        return defaultValue;
    }
    
    /**
     * Sets whether this option is required.
     * @param is Whether this option is required.
     */
    public void setRequired(boolean is)
    {
        this.isRequired = is;
    }
    
    /**
     * Returns whether this option is required to be set.
     * @return Whether this option is required to be set.
     */
    public boolean isRequired()
    {
        return isRequired;
    }
    
    @Override
    public String toString()
    {
        return this.getPrimaryIdentifier() + " option";
    }
}
