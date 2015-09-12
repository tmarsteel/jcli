package com.tmarsteel.jcli.filter;

import com.tmarsteel.jcli.validation.ValidationException;

/**
 * Represents a filter for option values.
 * @author tmarsteel
 */
public interface Filter
{
    /**
     * Parses the given value to the type required by this filter.
     * @param value The value to be parsed.
     * @return The parsed value.
     * @throws ValidationException If value does not match this filter.
     */
    public Object parse(String value)
        throws ValidationException;
}