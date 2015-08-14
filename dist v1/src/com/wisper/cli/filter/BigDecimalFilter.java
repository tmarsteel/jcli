package com.wisper.cli.filter;

import com.wisper.cli.ParseException;
import java.math.BigDecimal;

/**
 * Filters input as an integer number.
 * @author Tobias Marstaller
 */
public class BigDecimalFilter implements ValueFilter
{
    protected BigDecimal minValue = null;
    protected BigDecimal maxValue = null;
    
    public BigDecimalFilter() {}
    
    public BigDecimalFilter(BigDecimal minValue, BigDecimal maxValue)
    {
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    @Override
    public Object parse(String value)
        throws ParseException
    {
        try
        {
            BigDecimal n = new BigDecimal(value);
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
            throw new ParseException("Decimal value required", ex);
        }
    }
}
