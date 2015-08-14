package com.wisper.cli.filter;

import com.wisper.cli.ConfiguredCLIParser;
import com.wisper.cli.ParseException;
import java.math.BigInteger;
import org.w3c.dom.Node;

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

     public IntegerFilter(Node filterNode)
        throws ParseException
    {
        String[] minMaxRadix = ConfiguredCLIParser.XMLUtils.getMinMaxRadix(filterNode);

        try
        {
            if (minMaxRadix[2] == null)
            {
                radix = 10;
            }
            else
            {
                radix = Integer.parseInt(minMaxRadix[2]);
            }
        }
        catch (NumberFormatException ex)
        {
            throw new ParseException("Invalid radix: " + minMaxRadix[2]);
        }

       this.minValue = minMaxRadix[0] == null? Long.MIN_VALUE : ConfiguredCLIParser.XMLUtils.asLong(minMaxRadix[0]);
       this.maxValue = minMaxRadix[1] == null? Long.MAX_VALUE : ConfiguredCLIParser.XMLUtils.asLong(minMaxRadix[1]);
    }

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
