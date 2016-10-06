package com.tmarsteel.jcli.helptext;

import com.tmarsteel.jcli.filter.Filter;

/**
 * A {@link HelptextFormatter} that is aware of the various {@link Filter}s associated with the
 * {@link com.tmarsteel.jcli.Option}s and {@link com.tmarsteel.jcli.Argument}s included in a helptext.
 * <br>
 * This interface describes the methods to register the necessary {@link FilterDescriptor}s.
 */
public interface FilterAwareHelptextFormatter<R> extends HelptextFormatter<R>
{
    /**
     * Assures that filters that are of class {@code C} (or any subclass) are described by the given
     * {@link FilterDescriptor}. The more concrete {@code C} is, the higher it is prioritized when a
     * filter could be described by more than one {@link FilterDescriptor}.
     * @param <C> The type of flter to describe with the given {@link FilterDescriptor}
     */
    <C extends Filter> void setFilterDescriptor(Class<C> filterClass, FilterDescriptor<? super C> descriptor);
}
