package com.tmarsteel.jcli.filter;

import com.tmarsteel.jcli.ParseException;
import com.tmarsteel.jcli.validator.XMLValidatorBuilder;
import java.math.BigInteger;
import org.w3c.dom.Node;

/**
 * Filters input as an integer number.
 * @author tmarsteel
 */
public class BigIntegerFilter implements Filter
{
    protected BigInteger minValue = null;
    protected BigInteger maxValue = null;
    protected int radix = 10;

    public BigIntegerFilter() {}

    public BigIntegerFilter(Node filterNode)
        throws ParseException
    {
        String[] minMaxRadix = XMLValidatorBuilder.XMLUtils.getMinMaxRadix(filterNode);

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

        this.minValue = minMaxRadix[0] == null? null : new BigInteger(minMaxRadix[0]);
        this.maxValue = minMaxRadix[1] == null? null : new BigInteger(minMaxRadix[1]);
    }

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
