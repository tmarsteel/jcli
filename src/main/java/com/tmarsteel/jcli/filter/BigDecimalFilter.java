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
import java.math.BigDecimal;

/**
 * Filters input as an integer number.
 * @author tmarsteel
 */
public class BigDecimalFilter implements Filter
{
    private BigDecimal minValue = null;
    private BigDecimal maxValue = null;

    public BigDecimalFilter() {}

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

    /**
     * Returns the lower boundary or null if none is set.
     */
    public BigDecimal getMinValue()
    {
        return minValue;
    }

    /**
     * Sets the lower boundary. Set {@code null} to remove the lower boundary.
     */
    public void setMinValue(BigDecimal minValue)
    {
        this.minValue = minValue;
    }

    /**
     * Returns the upper boundary or null if none is set.
     */
    public BigDecimal getMaxValue()
    {
        return maxValue;
    }

    /**
     * Sets the upper boundary. Set {@code null} to remove the upper boundary.
     */
    public void setMaxValue(BigDecimal maxValue)
    {
        this.maxValue = maxValue;
    }
}
