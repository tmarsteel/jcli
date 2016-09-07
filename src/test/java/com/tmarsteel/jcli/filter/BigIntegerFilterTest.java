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
import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Tobias Marstaller
 */
public class BigIntegerFilterTest
{
    @Test
    public void testParseSucceedsWithoutLimit()
        throws ValidationException
    {
        BigIntegerFilter bdf = new BigIntegerFilter();
        
        String number = "43212345678951251251252435345746745455234121";
        
        Object ret = bdf.parse(number);
        
        assertTrue("BigIntegerFilter did not return an instance of java.math.BigInteger", BigInteger.class.isAssignableFrom(ret.getClass()));
        assertEquals(((BigInteger) ret).toString(), number);
    }
    
    @Test
    public void testParseSucceedsWithLimit()
        throws ValidationException
    {
        BigIntegerFilter bdf = new BigIntegerFilter(
            new BigInteger("100000000000000000056789"),
            new BigInteger("200000000000000000056789")
        );
        
        String number = "150000000000000000056789";
        
        Object ret = bdf.parse(number);
        
        assertTrue("BigIntegerFilter did not return an instance of java.math.BigInteger", BigInteger.class.isAssignableFrom(ret.getClass()));
        assertEquals(((BigInteger) ret).toString(), number);
    }
    
    @Test(expected=ValidationException.class)
    public void testParseFailsWithInputBelowLimit()
        throws ValidationException
    {
        BigIntegerFilter bdf = new BigIntegerFilter(
            new BigInteger("100000000000000000056789"),
            new BigInteger("200000000000000000056789")
        );
        
        String number = "050000000000000000056789";
        
        Object ret = bdf.parse(number);
    }
    
    @Test(expected=ValidationException.class)
    public void testParseFailsWithInputAboveLimit()
        throws ValidationException
    {
        BigIntegerFilter bdf = new BigIntegerFilter(
            new BigInteger("100000000000000000056789"),
            new BigInteger("200000000000000000056789")
        );
        
        String number = "250000000000000000056789";
        
        Object ret = bdf.parse(number);
    }
    
    @Test(expected=ValidationException.class)
    public void testParseFailsWithNonNumericInputWithoutLimit()
        throws ValidationException
    {
        BigIntegerFilter bdf = new BigIntegerFilter();
        
        String number = "10938209380aaaaa";
        
        Object ret = bdf.parse(number);
    }
    
    @Test(expected=ValidationException.class)
    public void testParseFailsWithNonNumericInputWithLimit()
        throws ValidationException
    {
        BigIntegerFilter bdf = new BigIntegerFilter(
            new BigInteger("100000000000000000056789"),
            new BigInteger("200000000000000000056789")
        );
        
        String number = "10938209380aaaaa";
        
        Object ret = bdf.parse(number);
    }
}
