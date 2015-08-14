package com.tmarsteel.jcli;

import com.tmarsteel.jcli.filter.ValueFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Represents a Option or Flag, e.g. <code>--log-level 2</code> or <code>-verbose</code>
 * @author Tobias Marstaller
 */
public class Option
{
    protected final ArrayList<String> names = new ArrayList<>();
    protected final boolean isFlag;
    protected ValueFilter valueFilter;
    protected Object defaultValue;
    
    /**
     * Constructs a new flag.
     * @param names The names of the option.
     */
    public Option(String... names)
    {
        this(true, names);
    }
    
    /**
     * Constructs a new option that is <b>not required</b>, whichs values are
     * checked by <code>filter</code> and that defaults to <code>defaultValue</code>.
     * @param names The names of the option.
     */
    public Option(ValueFilter filter, Object defaultValue, String... names)
    {
        this(false, names);
        this.valueFilter = filter;
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
     * Constructs a new option that is <b>not required</b> and whichs values are
     * checked by <code>filter</code>.
     * @param names The names of the option.
     */
    public Option(ValueFilter filter, String... names)
    {
        this(filter, null, names);
    }
    
    /**
     * Constructs a new option or flag.
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
     * Constructs a new option that defaults to <code>defaultValue</code> and
     * that accepts any value.
     * @param names The names of the option or flag.
     * @param defaultValue The value this option should default to.
     */
    public Option(Object defaultValue, String... names)
    {
        this(false, names);
        this.defaultValue = defaultValue;
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
        return names.get(0) + ' ' + (isFlag? "flag" : "option");
    }
}
