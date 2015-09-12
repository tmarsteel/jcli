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
        if (testNodes == null)
        {
            fail("The test-document could not be loaded. See errors for #setUp()");
        }
        
        DecimalFilter filter = new DecimalFilter(testNodes.item(0));
        
        assertEquals(10.1D, filter.getMinValue(), 0.1D);
        assertEquals(2000.978765487D, filter.getMaxValue(), 0.1D);
    }
    
    @Test(expected = ValidationException.class)
    public void nodeConstructorShouldFailOnNonNumerical()
        throws ValidationException
    {
        if (testNodes == null)
        {
            fail("The test-document could not be loaded. See errors for #setUp()");
        }
        
        DecimalFilter filter = new DecimalFilter(testNodes.item(1));
    }
}
