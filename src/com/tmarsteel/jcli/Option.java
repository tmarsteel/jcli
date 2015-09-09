package com.tmarsteel.jcli;

import com.tmarsteel.jcli.filter.Filter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a Option or Flag, e.g. <code>--log-level 2</code> or <code>-verbose</code>
 * @author tmarsteel
 */
public class Option
{
    protected final List<String> names = new ArrayList<>();
    protected final boolean isFlag;
    protected Filter valueFilter;
    protected Object defaultValue;
    
    /**
     * Constructs a new required option.
     * @param names The names of the option.
     */
    public Option(String... names)
    {
        this(false, names);
    }
    
    /**
     * Constructs a new required option or flag.
     * @param names The names of the option or flag.
     */
    public Option(boolean isFlag, String... names)
    {
        if (names.length == 0)
        {
            throw new IllegalArgumentException("At least one identifier is required");
        }
        this.names.addAll(Arrays.asList(names));
        this.isFlag = isFlag;
    }
    
    /**
     * Constructs a new required option
     * @param filter The filter to use, nullable
     * @param names The names of the option.
     */
    public Option(Filter filter, String... names)
    {
        this(false, names);
        this.valueFilter = filter;
    }
    
    /**
     * Constructs a new option that is <b>not required</b>, whichs values are
     * checked by <code>filter</code> and that defaults to <code>defaultValue</code>.
     * @param names The names of the option.
     */
    public Option(Filter filter, Object defaultValue, String... names)
    {
        this(filter, names);
        
        if (defaultValue != null && defaultValue instanceof String)
        {
            try
            {
                this.defaultValue = parse((String) defaultValue);
            }
            catch (ParseException ex)
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
     * Returns whether the given name identifies this flag/option.
     * @return Whether the given name identifies this flag/option.
     */
    public boolean isIdentifiedBy(String name)
    {
        return names.contains(name);
    }
    
    /**
     * Returns the primary identifier for this option.
     * @return The primary identifier for this option.
     */
    public String getPrimaryIdentifier()
    {
        return names.get(0);
    }
    
    /**
     * Returns whether any of this options/flags aliases is ambigous with any
     * of the aliases of the given flag/option.
     * @return Whether any of this options/flags aliases is ambigous with any
     * of the aliases of the given flag/option.
     */
    public boolean isAmbigousWith(Option option)
    {
        final Iterator<String> myNames = names.iterator();
        while (myNames.hasNext())
        {
            if (option.isIdentifiedBy(myNames.next()))
            {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Returns whether this instance represents a flag or an option.
     * @return Whether this instance represents a flag or an option.
     */
    public boolean isFlag()
    {
        return isFlag;
    }
    
    /**
     * Parses the given value to the type required by this option.
     * @param value The value to be parsed.
     * @return The parsed value.
     * @throws ParseException If value does not match this filter.
     */
    public Object parse(String value)
        throws ParseException
    {
        return valueFilter == null? value : valueFilter.parse(value);
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
     * Returns whether this option is required to be set.
     * @return Whether this option is required to be set.
     */
    public boolean isRequired()
    {
        return defaultValue == null;
    }
    
    @Override
    public String toString()
    {
        return this.getPrimaryIdentifier() + ' ' + (isFlag? "flag" : "option");
    }
}
