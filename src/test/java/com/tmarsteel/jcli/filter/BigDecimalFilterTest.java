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
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * @author Tobias Marstaller
 */
public class BigDecimalFilterTest
{
    @Test
    public void testParseSucceedsWithoutLimit()
        throws ValidationException
    {
        BigDecimalFilter bdf = new BigDecimalFilter();
        
        String number = "432123456789.51251251252435345746745455234121";
        
        Object ret = bdf.parse(number);
        
        assertTrue("BigDecimalFilter did not return an instance of java.math.BigDecimal", BigDecimal.class.isAssignableFrom(ret.getClass()));
        assertEquals(((BigDecimal) ret).toPlainString(), number);
    }
    
    @Test
    public void testParseSucceedsWithLimit()
        throws ValidationException
    {
        BigDecimalFilter bdf = new BigDecimalFilter(
            new BigDecimal("1.00000000000000000056789"),
            new BigDecimal("2.00000000000000000056789")
        );
        
        String number = "1.50000000000000000056789";
        
        Object ret = bdf.parse(number);
        
        assertTrue("BigDecimalFilter did not return an instance of java.math.BigDecimal", BigDecimal.class.isAssignableFrom(ret.getClass()));
        assertEquals(((BigDecimal) ret).toPlainString(), number);
    }

    @Test(expected=ValidationException.class)
    public void testParseFailsWithInputBelowLimit()
        throws ValidationException
    {
        BigDecimalFilter bdf = new BigDecimalFilter(
            new BigDecimal("1.00000000000000000056789"),
            new BigDecimal("2.00000000000000000056789")
        );
        
        String number = "0.50000000000000000056789";
        
        Object ret = bdf.parse(number);
    }
    
    @Test(expected=ValidationException.class)
    public void testParseFailsWithInputAboveLimit()
        throws ValidationException
    {
        BigDecimalFilter bdf = new BigDecimalFilter(
            new BigDecimal("1.00000000000000000056789"),
            new BigDecimal("2.00000000000000000056789")
        );
        
        String number = "2.50000000000000000056789";
        
        Object ret = bdf.parse(number);
    }
    
    @Test(expected=ValidationException.class)
    public void testParseFailsWithNonNumericInputWithoutLimit()
        throws ValidationException
    {
        BigDecimalFilter bdf = new BigDecimalFilter();
        
        String number = "1.0938209380aaaaa";
        
        Object ret = bdf.parse(number);
    }
    
    @Test(expected=ValidationException.class)
    public void testParseFailsWithNonNumericInputWithLimit()
        throws ValidationException
    {
        BigDecimalFilter bdf = new BigDecimalFilter(
            new BigDecimal("1.00000000000000000056789"),
            new BigDecimal("2.00000000000000000056789")
        );
        
        String number = "1.0938209380aaaaa";
        
        Object ret = bdf.parse(number);
    }
}
