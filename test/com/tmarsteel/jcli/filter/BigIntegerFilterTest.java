package com.tmarsteel.jcli.filter;

import com.tmarsteel.jcli.validation.ValidationException;
import java.math.BigInteger;
import static org.junit.Assert.*;
import org.junit.Test;

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
