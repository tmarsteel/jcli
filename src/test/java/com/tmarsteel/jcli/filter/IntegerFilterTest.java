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
import com.tmarsteel.jcli.XMLTest;
import com.tmarsteel.jcli.validation.ValidationException;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * @author Tobias Marstaller
 */
public class IntegerFilterTest extends XMLTest
{
    public IntegerFilterTest()
    {
        this.testXML = "IntegerFilterTest.xml";
        this.testNodesName = "filter";
    }
    
    @Test
    public void testParseSucceedsWithoutLimit()
        throws ValidationException
    {
        IntegerFilter bdf = new IntegerFilter();
        
        String number = "123456789";
        
        Object ret = bdf.parse(number);
        
        assertTrue("IntegerFilter did not return an instance of java.lang.Long", Long.class.isAssignableFrom(ret.getClass()));
        assertEquals(String.valueOf(ret), number);
    }
    
    @Test
    public void testParseSucceedsWithLimit()
        throws ValidationException
    {
        IntegerFilter bdf = new IntegerFilter(
            1000005478L,
            2000005478L
        );
        
        String number = "1500056789";
        
        Object ret = bdf.parse(number);
        
        assertTrue("IntegerFilter did not return an instance of java.lang.Long", Long.class.isAssignableFrom(ret.getClass()));
        assertEquals(String.valueOf(ret), number);
    }
    
    @Test(expected=ValidationException.class)
    public void testParseFailsWithInputBelowLimit()
        throws ValidationException
    {
        IntegerFilter bdf = new IntegerFilter(
            1000005478L,
            2000005478L
        );
        
        String number = "58879";
        
        Object ret = bdf.parse(number);
    }
    
    @Test(expected=ValidationException.class)
    public void testParseFailsWithInputAboveLimit()
        throws ValidationException
    {
        IntegerFilter bdf = new IntegerFilter(
            1000005478L,
            2000005478L
        );
        
        String number = "2500005478";
        
        Object ret = bdf.parse(number);
    }
    
    @Test(expected=ValidationException.class)
    public void testParseFailsWithNonNumericInputWithoutLimit()
        throws ValidationException
    {
        IntegerFilter bdf = new IntegerFilter();
        
        String number = "10938209380aaaaa";
        
        Object ret = bdf.parse(number);
    }
    
    @Test(expected=ValidationException.class)
    public void testParseFailsWithNonNumericInputWithLimit()
        throws ValidationException
    {
        IntegerFilter bdf = new IntegerFilter(
            1000005478L,
            2000005478L
        );
        
        String number = "10938209380aaaaa";
        
        Object ret = bdf.parse(number);
    }
    
    @Test
    public void testNodeConstructor()
        throws ParseException, ValidationException
    {
        IntegerFilter filter = new IntegerFilter(testNodes.item(0));
        
        assertEquals(filter.getMinValue(), 10);
        assertEquals(filter.getMaxValue(), 2000);
    }
    
    @Test
    public void testNodeConstructorWithRadix()
        throws ParseException, ValidationException
    {
        IntegerFilter filter = new IntegerFilter(testNodes.item(1));
        
        assertEquals(filter.getMinValue(), 10);
        assertEquals(filter.getMaxValue(), 2000);
        assertEquals(filter.getRadix(), 16);
    }
    
    @Test(expected = ValidationException.class)
    public void nodeConstructorShouldFailOnNonNumerical()
        throws ParseException, ValidationException
    {
        IntegerFilter filter = new IntegerFilter(testNodes.item(2));
    }
}
