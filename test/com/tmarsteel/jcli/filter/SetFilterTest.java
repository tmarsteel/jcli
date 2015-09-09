package com.tmarsteel.jcli.filter;

import com.tmarsteel.jcli.ParseException;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Tobias Marstaller
 */
public class SetFilterTest
{
    @Test
    public void testParseCI()
        throws ParseException
    {
        SetFilter filter = new SetFilter("a", "b");
        
        // this aspect is very important: always return the strings configured,
        // never those from the input
        assertEquals(filter.parse("a"), "a");
        assertEquals(filter.parse("A"), "a");
    }
    
    @Test
    public void testParseCS()
        throws ParseException
    {
        SetFilter filter = new SetFilter("a", "b");
        
        assertEquals(filter.parse("A"), "a");
    }
    
    @Test(expected=ParseException.class)
    public void testParseCIFailsOnNonExistent()
        throws ParseException
    {
        SetFilter filter = new SetFilter("a", "b");
        
        filter.parse("c");
    }
    
    @Test(expected=ParseException.class)
    public void testParseCSFailsOnCaseError()
        throws ParseException
    {
        SetFilter filter = new SetFilter(true, "a", "b");
        
        filter.parse("A");
    }
    
    @Test(expected=ParseException.class)
    public void testParseCSFailsOnNonExistent()
        throws ParseException
    {
        SetFilter filter = new SetFilter(true, "a", "b");
        
        filter.parse("C");
    }
}
