package com.tmarsteel.jcli.filter;

import com.tmarsteel.jcli.ParseException;
import com.tmarsteel.jcli.validation.ValidationException;
import java.io.IOException;
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
public class IntegerFilterTest
{
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
    
    // --------------------
    
    private NodeList testNodes;
    
    @Before
    public void setUp()
        throws ParserConfigurationException, SAXException, IOException
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(false);
        dbf.setIgnoringComments(true);
        dbf.setIgnoringElementContentWhitespace(true);
        
        DocumentBuilder builder = dbf.newDocumentBuilder();
        
        Document testDocument = builder.parse(getClass().getResourceAsStream("IntegerFilterTest.xml"));
        
        testNodes = testDocument.getElementsByTagName("filter");
    }
    
    @Test
    public void testNodeConstructor()
        throws ParseException, ValidationException
    {
        if (testNodes == null)
        {
            fail("The test-document could not be loaded. See errors for #setUp()");
        }
        
        IntegerFilter filter = new IntegerFilter(testNodes.item(0));
        
        assertEquals(filter.getMinValue(), 10);
        assertEquals(filter.getMaxValue(), 2000);
    }
    
    @Test
    public void testNodeConstructorWithRadix()
        throws ParseException, ValidationException
    {
        if (testNodes == null)
        {
            fail("The test-document could not be loaded. See errors for #setUp()");
        }
        
        IntegerFilter filter = new IntegerFilter(testNodes.item(1));
        
        assertEquals(filter.getMinValue(), 10);
        assertEquals(filter.getMaxValue(), 2000);
        assertEquals(filter.getRadix(), 16);
    }
    
    @Test(expected = ValidationException.class)
    public void nodeConstructorShouldFailOnNonNumerical()
        throws ParseException, ValidationException
    {
        if (testNodes == null)
        {
            fail("The test-document could not be loaded. See errors for #setUp()");
        }

        IntegerFilter filter = new IntegerFilter(testNodes.item(2));
    }
}
