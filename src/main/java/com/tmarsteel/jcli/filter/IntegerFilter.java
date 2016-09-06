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
import com.tmarsteel.jcli.validation.configuration.xml.XMLValidatorConfigurator;
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
        throws ValidationException
    {
        try
        {
            Long n = Long.parseLong(value, radix);
            if (n < minValue)
            {
                throw new ValidationException("Value less than minimum (" + Long.toString(minValue, radix) + ')');
            }
            if (n > maxValue)
            {
                throw new ValidationException("Value greater than maximum (" + Long.toString(maxValue, radix) + ')');
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

    public long getMinValue()
    {
        return minValue;
    }

    public void setMinValue(long minValue)
    {
        this.minValue = minValue;
    }

    public long getMaxValue()
    {
        return maxValue;
    }

    public void setMaxValue(long maxValue)
    {
        this.maxValue = maxValue;
    }
    
    
}
