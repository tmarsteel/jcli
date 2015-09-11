package com.tmarsteel.jcli.filter;

import com.tmarsteel.jcli.ParseException;
import com.tmarsteel.jcli.validator.XMLParserBuilder;
import java.math.BigInteger;
import org.w3c.dom.Node;

/**
 * Filters input as an integer number.
 * @author tmarsteel
 */
public class IntegerFilter implements Filter
{
    protected long minValue = Long.MIN_VALUE;
    protected long maxValue = Long.MAX_VALUE;
    protected int radix = 10;

    public IntegerFilter() {}

    /**
     * Creates a new filter from a DOM node. <br>
     * Node structure:
     * Minimum, maximum and radix may be specified by &lt;min&gt;, &lt;max&gt;
     * and &lt;radix&gt; subtags respectively.
     * @param filterNode
     * @throws ParseException If any of the given subtags (min, max, radix) does
     * not contain an integer number.
     */
    public IntegerFilter(Node filterNode)
        throws ParseException
    {
        String[] minMaxRadix = XMLParserBuilder.XMLUtils.getMinMaxRadix(filterNode);

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

       this.minValue = minMaxRadix[0] == null? Long.MIN_VALUE : XMLParserBuilder.XMLUtils.asLong(minMaxRadix[0]);
       this.maxValue = minMaxRadix[1] == null? Long.MAX_VALUE : XMLParserBuilder.XMLUtils.asLong(minMaxRadix[1]);
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
