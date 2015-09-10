package com.tmarsteel.jcli;

import com.tmarsteel.jcli.filter.Filter;

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
     * Sets the default value of this option; unless <code>defV</code> is null,
     * the option is also made non-required.
     * @param defV The default value for this option.
     */
    public void setDefaultValue(Object defV)
    {
        this.defaultValue = defV;
        
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
