package com.tmarsteel.jcli.filter;

import com.tmarsteel.jcli.validation.ValidationException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * @author Tobias Marstaller
 */
public class DecimalFilterTest
{ 
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
}
