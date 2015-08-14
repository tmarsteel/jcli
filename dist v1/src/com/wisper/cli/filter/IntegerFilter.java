package com.wisper.cli.filter;

import com.wisper.cli.ParseException;

/**
 * Filters input as an integer number.
 * @author Tobias Marstaller
 */
public class IntegerFilter implements ValueFilter
{
    protected long minValue = Long.MIN_VALUE;
    protected long maxValue = Long.MAX_VALUE;
    protected int radix = 10;
    
    public IntegerFilter() {}
    
    public IntegerFilter(int radix)
    {
        this.radix = radix;
    }
    
    public IntegerFilter(long minValue, long maxValue)
    {
        this.minValue = minValue;
        this.maxValue = maxValue;
    }
    
    public IntegerFilter(long minValue, long maxValue, int radix)
    {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.radix = radix;
    }

    @Override
    public Object parse(String value)
        throws ParseException
    {
        try
        {
            Long n = Long.parseLong(value, radix);
            if (n < minValue)
            {
                throw new ParseException("Value less than minimum (" + Long.toString(minValue, radix) + ')');
            }
            if (n > maxValue)
            {
                throw new ParseException("Value greater than maximum (" + Long.toString(maxValue, radix) + ')');
            }
            return n;
        }
        catch (NumberFormatException ex)
        {
            throw new ParseException("Integer value with radix " + radix + " required", ex);
        }
    }

    public int getRadix()
    {
        return radix;
    }

    public void setRadix(int radix)
    {
        this.radix = radix;
    }
}
