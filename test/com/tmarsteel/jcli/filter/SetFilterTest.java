package com.tmarsteel.jcli.filter;

import com.tmarsteel.jcli.validation.ValidationException;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Tobias Marstaller
 */
public class SetFilterTest
{
    @Test
    public void testParseCI()
        throws ValidationException
    {
        SetFilter filter = new SetFilter("a", "b");
        
        // this aspect is very important: always return the strings configured,
        // never those from the input
        assertEquals(filter.parse("a"), "a");
        assertEquals(filter.parse("A"), "a");
    }
    
    @Test
    public void testParseCS()
        throws ValidationException
    {
        SetFilter filter = new SetFilter("a", "b");
        
        assertEquals(filter.parse("A"), "a");
    }
    
    @Test(expected=ValidationException.class)
    public void testParseCIFailsOnNonExistent()
        throws ValidationException
    {
        SetFilter filter = new SetFilter("a", "b");
        
        filter.parse("c");
    }
    
    @Test(expected=ValidationException.class)
    public void testParseCSFailsOnCaseError()
        throws ValidationException
    {
        SetFilter filter = new SetFilter(true, "a", "b");
        
        filter.parse("A");
    }
    
    @Test(expected=ValidationException.class)
    public void testParseCSFailsOnNonExistent()
        throws ValidationException
    {
        SetFilter filter = new SetFilter(true, "a", "b");
        
        filter.parse("C");
    }
}
