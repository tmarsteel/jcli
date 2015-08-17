package com.tmarsteel.jcli.filter;

import com.tmarsteel.jcli.parser.ConfiguredCLIParser;
import com.tmarsteel.jcli.ParseException;
import java.math.BigDecimal;
import org.w3c.dom.Node;

/**
 * Filters input as an integer number.
 * @author tmarsteel
 */
public class BigDecimalFilter implements ValueFilter
{
    protected BigDecimal minValue = null;
    protected BigDecimal maxValue = null;

    public BigDecimalFilter() {}

    public BigDecimalFilter(Node filterNode)
    {
        String[] minMax = ConfiguredCLIParser.XMLUtils.getMinMax(filterNode);
        this.minValue = minMax[0] == null? null : new BigDecimal(minMax[0]);
        this.maxValue = minMax[1] == null? null : new BigDecimal(minMax[1]);
    }

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
