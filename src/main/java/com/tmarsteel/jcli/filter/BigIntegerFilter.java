/* 
 * Copyright (C) 2015 Tobias Marstaller
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.tmarsteel.jcli.filter;

import com.tmarsteel.jcli.ParseException;
import com.tmarsteel.jcli.validation.ValidationException;
import com.tmarsteel.jcli.validation.configuration.XMLValidatorConfigurator;
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
        String[] minMaxRadix = XMLValidatorConfigurator.XMLUtils.getMinMaxRadix(filterNode);

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
        
        this.minValue = minMaxRadix[0] == null? null : new BigInteger(minMaxRadix[0], radix);
        this.maxValue = minMaxRadix[1] == null? null : new BigInteger(minMaxRadix[1], radix);
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
        throws ValidationException
    {
        try
        {
            BigInteger n = new BigInteger(value, radix);
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
            throw new ValidationException("Integer value with radix " + radix + " required", ex);
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

    public BigInteger getMinValue()
    {
        return minValue;
    }

    public void setMinValue(BigInteger minValue)
    {
        this.minValue = minValue;
    }

    public BigInteger getMaxValue()
    {
        return maxValue;
    }

    public void setMaxValue(BigInteger maxValue)
    {
        this.maxValue = maxValue;
    }
}
