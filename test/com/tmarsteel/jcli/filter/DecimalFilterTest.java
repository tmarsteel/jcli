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

import com.tmarsteel.jcli.XMLTest;
import com.tmarsteel.jcli.validation.ValidationException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 * @author Tobias Marstaller
 */
public class DecimalFilterTest extends XMLTest
{ 
    public DecimalFilterTest()
    {
        this.testXML = "DecimalFilterTest.xml";
        this.testNodesName = "filter";
    }
    
    @Test
    public void testParseSucceedsWithoutLimit()
        throws ValidationException
    {
        DecimalFilter bdf = new DecimalFilter();
        
        String number = "124124.5677";
        
        Object ret = bdf.parse(number);
        
        assertTrue("DecimalFilter did not return an instance of java.lang.Double", Double.class.isAssignableFrom(ret.getClass()));
        assertEquals(String.valueOf(ret), number);
    }
    
    @Test
    public void testParseSucceedsWithLimit()
        throws ValidationException
    {
        DecimalFilter bdf = new DecimalFilter(
            1.0054f,
            2.0054f
        );
        
        String number = "1.5054";
        
        Object ret = bdf.parse(number);
        
        assertTrue("DecimalFilter did not return an instance of java.lang.Double", Double.class.isAssignableFrom(ret.getClass()));
        assertEquals(String.valueOf(ret), number);
    }

    @Test(expected=ValidationException.class)
    public void testParseFailsWithInputBelowLimit()
        throws ValidationException
    {
        DecimalFilter bdf = new DecimalFilter(
            1.0054f,
            2.0054f
        );
        
        String number = "0.0054";
        
        Object ret = bdf.parse(number);
    }
    
    @Test(expected=ValidationException.class)
    public void testParseFailsWithInputAboveLimit()
        throws ValidationException
    {
        DecimalFilter bdf = new DecimalFilter(
            1.0054f,
            2.0054f
        );
        
        String number = "2.5054";
        
        Object ret = bdf.parse(number);
    }
    
    @Test(expected=ValidationException.class)
    public void testParseFailsWithNonNumericInputWithoutLimit()
        throws ValidationException
    {
        DecimalFilter bdf = new DecimalFilter();
        
        String number = "1.09380aaaaa";
        
        Object ret = bdf.parse(number);
    }
    
    @Test(expected=ValidationException.class)
    public void testParseFailsWithNonNumericInputWithLimit()
        throws ValidationException
    {
        DecimalFilter bdf = new DecimalFilter(
            1.0054f,
            2.0054f
        );
        
        String number = "1.09380aaaaa";
        
        Object ret = bdf.parse(number);
    }
    
    @Test
    public void testNodeConstructor()
        throws ValidationException
    {
        DecimalFilter filter = new DecimalFilter(testNodes.item(0));
        
        assertEquals(10.1D, filter.getMinValue(), 0.1D);
        assertEquals(2000.978765487D, filter.getMaxValue(), 0.1D);
    }
    
    @Test(expected = ValidationException.class)
    public void nodeConstructorShouldFailOnNonNumerical()
        throws ValidationException
    {
        DecimalFilter filter = new DecimalFilter(testNodes.item(1));
    }
}
