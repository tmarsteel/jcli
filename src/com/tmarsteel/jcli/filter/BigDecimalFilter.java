package com.tmarsteel.jcli.filter;

import com.tmarsteel.jcli.validation.ValidationException;
import com.tmarsteel.jcli.validation.configuration.XMLValidatorConfigurator;
import java.math.BigDecimal;
import org.w3c.dom.Node;

/**
 * Filters input as an integer number.
 * @author tmarsteel
 */
public class BigDecimalFilter implements Filter
{
    private BigDecimal minValue = null;
    private BigDecimal maxValue = null;

    public BigDecimalFilter() {}

    public BigDecimalFilter(Node filterNode)
    {
        String[] minMax = XMLValidatorConfigurator.XMLUtils.getMinMax(filterNode);
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
        throws ValidationException
    {
        try
        {
            BigDecimal n = new BigDecimal(value);
            if (minValue != null && n.compareTo(minValue) < 0)
            {
                throw new ValidationException("Value less than minimum");
            }
            if (maxValue != null && n.compareTo(maxValue) > 0)
            {
                throw new ValidationException("Value greater than maximum");
            }
            return n;
        }
        catch (NumberFormatException ex)
        {
            throw new ValidationException("Decimal value required", ex);
        }
    }

    public BigDecimal getMinValue()
    {
        return minValue;
    }

    public void setMinValue(BigDecimal minValue)
    {
        this.minValue = minValue;
    }

    public BigDecimal getMaxValue()
    {
        return maxValue;
    }

    public void setMaxValue(BigDecimal maxValue)
    {
        this.maxValue = maxValue;
    }
}
