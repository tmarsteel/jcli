package com.tmarsteel.jcli.filter;

import com.tmarsteel.jcli.validator.ValidationException;
import static org.junit.Assert.*;
import org.junit.Test;

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
}
