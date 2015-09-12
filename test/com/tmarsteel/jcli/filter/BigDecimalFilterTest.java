package com.tmarsteel.jcli.filter;

import com.tmarsteel.jcli.validation.ValidationException;
import java.io.IOException;
import java.math.BigDecimal;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

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
    
    // --------------------
    
    private Document testDocument;
    
    @Before
    public void setUp()
        throws ParserConfigurationException, SAXException, IOException
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(false);
        dbf.setIgnoringComments(true);
        dbf.setIgnoringElementContentWhitespace(true);
        
        DocumentBuilder builder = dbf.newDocumentBuilder();
        
        testDocument = builder.parse(getClass().getResourceAsStream("BigDecimalFilterTest.xml"));
    }
    
    @Test
    public void testNodeConstructor()
    {
        if (testDocument == null)
        {
            fail("The test-document could not be loaded. See errors for #setUp()");
        }
        
        Node testNode = testDocument.getDocumentElement().getElementsByTagName("filter").item(0);
        
        BigDecimalFilter filter = new BigDecimalFilter(testNode);
        
        assertEquals(filter.getMinValue().toPlainString(), "10.1");
        assertEquals(filter.getMaxValue().toPlainString(), "2000.978765487");
    }
    
    @Test(expected = NumberFormatException.class)
    public void nodeConstructorShouldFailOnNonNumerical()
    {
        if (testDocument == null)
        {
            fail("The test-document could not be loaded. See errors for #setUp()");
        }
        
        Node testNode = testDocument.getDocumentElement().getElementsByTagName("filter").item(1);
        
        BigDecimalFilter filter = new BigDecimalFilter(testNode);
    }
}
