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
import java.io.IOException;
import java.math.BigInteger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author Tobias Marstaller
 */
public class BigIntegerFilterTest extends XMLTest
{
    public BigIntegerFilterTest()
    {
        this.testXML = "BigIntegerFilterTest.xml";
        this.testNodesName = "filter";
    }
    
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
    
    @Test
    public void testNodeConstructor()
        throws ParseException
    {
        BigIntegerFilter filter = new BigIntegerFilter(testNodes.item(0));
        
        assertEquals(filter.getMinValue().toString(), "10");
        assertEquals(filter.getMaxValue().toString(), "2000");
    }
    
    @Test
    public void testNodeConstructorWithRadix()
        throws ParseException
    {
        BigIntegerFilter filter = new BigIntegerFilter(testNodes.item(1));
        
        assertEquals(filter.getMinValue().toString(10), "10");
        assertEquals(filter.getMaxValue().toString(10), "2000");
        assertEquals(filter.getRadix(), 16);
    }
    
    @Test(expected = NumberFormatException.class)
    public void nodeConstructorShouldFailOnNonNumerical()
        throws ParseException
    {
        BigIntegerFilter filter = new BigIntegerFilter(testNodes.item(2));
    }
}
