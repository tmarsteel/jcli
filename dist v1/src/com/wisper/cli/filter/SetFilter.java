package com.wisper.cli.filter;

import com.wisper.cli.ParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

/**
 * Accepts only values from a given set of possible values.
 * @author Tobias Marstaller
 */
public class SetFilter implements ValueFilter
{
    protected Collection<String> options;
    protected boolean caseSensitive = false;
    
    public SetFilter(String... options)
    {
        this(Arrays.asList(options));
    }
    
    public SetFilter(boolean caseSensitive, String... options)
    {
        this(options);
        this.caseSensitive = caseSensitive;
    }
    
    public SetFilter(Collection<String> options)
    {
        if (options.isEmpty())
        {
            throw new IllegalArgumentException("Need to specify at least one value");
        }
        this.options = options;
    }
    
    public SetFilter(boolean caseSensitive, Collection<String> options)
    {
        this(options);
        this.caseSensitive = caseSensitive;
    }

    @Override
    public Object parse(String value)
        throws ParseException
    {
        final Iterator<String> it = options.iterator();
        while (it.hasNext())
        {
            final String cur = it.next();
            if (caseSensitive)
            {
                if (cur.equals(value))
                {
                    return cur;
                }
            }
            else
            {
                if (cur.equalsIgnoreCase(value))
                {
                    return cur;
                }
            }
        }
        throw new ParseException(value + " is not a possible value.");
    }
    
}
