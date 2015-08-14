package com.wisper.cli.filter;

import com.wisper.cli.ParseException;

/**
 * Represents a filter for option values.
 * @author Tobias Marstaller
 */
public interface ValueFilter
{    
    /**
     * Parses the given value to the type required by this filter.
     * @param value The value to be parsed.
     * @return The parsed value.
     * @throws ParseException If value does not match this filter.
     */
    public Object parse(String value)
        throws ParseException;
}
