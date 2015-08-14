package com.wisper.cli.filter;

import com.wisper.cli.ConfiguredCLIParser;
import com.wisper.cli.ParseException;
import org.w3c.dom.Node;

/**
 * Filters input as an integer number.
 * @author Tobias Marstaller
 */
public class DecimalFilter implements ValueFilter
{
    protected double minValue = Long.MIN_VALUE;
    protected double maxValue = Long.MAX_VALUE;

    public DecimalFilter() {}

    public DecimalFilter(Node filterNode)
        throws ParseException
    {
        String[] minMax = ConfiguredCLIParser.XMLUtils.getMinMax(filterNode);
        this.minValue = ConfiguredCLIParser.XMLUtils.asDouble(minMax[0]);
        this.maxValue = ConfiguredCLIParser.XMLUtils.asDouble(minMax[1]);
    }

    public DecimalFilter(double minValue, double maxValue)
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
            Double n = Double.parseDouble(value);
            if (n < minValue)
            {
                throw new ParseException("Value less than minimum (" + Double.toString(minValue) + ')');
            }
            if (n > maxValue)
            {
                throw new ParseException("Value greater than maximum (" + Double.toString(maxValue) + ')');
            }
            return n;
        }
        catch (NumberFormatException ex)
        {
            throw new ParseException("Decimal value required", ex);
        }
    }
}
