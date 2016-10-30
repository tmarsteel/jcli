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

import com.tmarsteel.jcli.validation.ValidationException;
import java.math.BigInteger;

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

    /**
     * Returns the lower boundary or null if none is set.
     */
    public BigInteger getMinValue()
    {
        return minValue;
    }

    /**
     * Sets the lower boundary. Set {@code null} to remove the lower boundary.
     */
    public void setMinValue(BigInteger minValue)
    {
        this.minValue = minValue;
    }

    /**
     * Returns the upper boundary or null if none is set.
     */
    public BigInteger getMaxValue()
    {
        return maxValue;
    }

    /**
     * Sets the upper boundary. Set {@code null} to remove the upper boundary.
     */
    public void setMaxValue(BigInteger maxValue)
    {
        this.maxValue = maxValue;
    }
}
