package com.tmarsteel.jcli.helptext;

import com.tmarsteel.jcli.filter.Filter;

import java.util.List;

/**
 * Creates a human-readable String based on a given {@link com.tmarsteel.jcli.filter.Filter} that describes that given
 * filters constraints and conversion properties.
 */
public interface FilterDescriptor<E extends Filter> {
    /**
     * Returns a list of human-readable strings each describing one of the constraints the described filter
     * enforces on the values parsed by it. Does not return null.
     */
    <T extends E> List<String> describe(T filter);
}
