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
