package com.tmarsteel.jcli.filter;

import com.tmarsteel.jcli.validation.ValidationException;
import com.tmarsteel.jcli.validation.configuration.XMLValidatorConfigurator;
import org.w3c.dom.Node;

/**
 * Filters input as an integer number.
 * @author tmarsteel
 */
public class DecimalFilter implements Filter
{
    protected double minValue = Long.MIN_VALUE;
    protected double maxValue = Long.MAX_VALUE;

    public DecimalFilter() {}

    public DecimalFilter(Node filterNode)
        throws ValidationException
    {
        String[] minMax = XMLValidatorConfigurator.XMLUtils.getMinMax(filterNode);
        this.minValue = XMLValidatorConfigurator.XMLUtils.asDouble(minMax[0]);
        this.maxValue = XMLValidatorConfigurator.XMLUtils.asDouble(minMax[1]);
    }

    public DecimalFilter(double minValue, double maxValue)
    {
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    @Override
    public Object parse(String value)
        throws ValidationException
    {
        try
        {
            Double n = Double.parseDouble(value);
            if (n < minValue)
            {
                throw new ValidationException("Value less than minimum (" + Double.toString(minValue) + ')');
            }
            if (n > maxValue)
            {
                throw new ValidationException("Value greater than maximum (" + Double.toString(maxValue) + ')');
            }
            return n;
        }
        catch (NumberFormatException ex)
        {
            throw new ValidationException("Decimal value required", ex);
        }
    }

    public double getMinValue()
    {
        return minValue;
    }

    public void setMinValue(double minValue)
    {
        this.minValue = minValue;
    }

    public double getMaxValue()
    {
        return maxValue;
    }

    public void setMaxValue(double maxValue)
    {
        this.maxValue = maxValue;
    }
    
    
}
