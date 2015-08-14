package com.wisper.cli.filter;

import com.wisper.cli.ParseException;
import java.math.BigInteger;

/**
 * Filters input as an integer number.
 * @author Tobias Marstaller
 */
public class BigIntegerFilter implements ValueFilter
{
    protected BigInteger minValue = null;
    protected BigInteger maxValue = null;
    protected int radix = 10;
    
    public BigIntegerFilter() {}
    
    public BigIntegerFilter(int radix)
    {
        this.radix = radix;
    }
    
    public BigIntegerFilter(BigInteger minValue, BigInteger maxValue)
    {
        this.minValue = minValue;
        this.maxValue = maxValue;
    }
    
    public BigIntegerFilter(BigInteger minValue, BigInteger maxValue, int radix)
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
            BigInteger n = new BigInteger(value, radix);
            if (minValue != null && n.compareTo(minValue) < 0)
            {
                throw new ParseException("Value less than minimum");
            }
            if (maxValue != null && n.compareTo(maxValue) > 0)
            {
                throw new ParseException("Value greater than maximum");
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
