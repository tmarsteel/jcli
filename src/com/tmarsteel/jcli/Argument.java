package com.tmarsteel.jcli;

import com.tmarsteel.jcli.filter.ValueFilter;

/**
 * Represents any parameter passed to the program besides flags and options.
 * @author tmarsteel
 */
public class Argument
{
    protected ValueFilter valueFilter;
    protected Object defaultValue;
    protected final int index;
    protected final String accessName;
    protected boolean required = false;
    
    /**
     * Constructs a new argument placed at index <code>index</code> that can be
     * accessed by <code>accessName</code> on code level.
     * @param accessName The name this argument should be accessed with.
     * @param index index The index this argument is placed at.
     * @throws IllegalArgumentException If index is less than 0.
     */
    public Argument(String accessName, int index)
    {
        this.index = index;
        this.accessName = accessName;
    }
    
    /**
     * Constructs a new argument placed at index <code>index</code> that can be
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
    }
    
    /**
     * Constructs a new argument placed at index <code>index</code> that can be
     * accessed by <code>accessName</code> on code level.
     * @param accessName The name this argument should be accessed with.
     * @param index index The index this argument is placed at.
     * @param defaultValue The default value for this argument.
     * @param filter A filter to validate input for this argument.
     * @throws IllegalArgumentException If index is less than 0.
     */
    public Argument(String accessName, int index, Object defaultValue,
        ValueFilter filter)
    {
        this(accessName, index, defaultValue);
        this.valueFilter = filter;
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
     * @throws ParseException If value does not match this filter.
     */
    public Object parse(String input)
        throws ParseException
    {
        return valueFilter == null? input : valueFilter.parse(input);
    }

    public boolean isRequired()
    {
        return required;
    }

    public void setRequired(boolean required)
    {
        this.required = required;
    }
}
