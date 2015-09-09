package com.tmarsteel.jcli.filter;

import com.tmarsteel.jcli.ParseException;
import java.util.regex.Pattern;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Tobias Marstaller
 */
public class RegexFilterTest
{
    @Test
    public void testParseSucceedsWithoutReturnGroup()
        throws ParseException
    {
        RegexFilter filter = new RegexFilter(Pattern.compile("^a.+$"));
        
        assertEquals(filter.parse("abc"), "abc");
    }
    
    @Test
    public void testParseSucceedsWithReturnGroup()
        throws ParseException
    {
        RegexFilter filter = new RegexFilter(Pattern.compile("^a(.+)$"));
        filter.setReturnGroup(1);
        
        assertEquals(filter.parse("abc"), "bc");
    }
    
    @Test(expected=ParseException.class)
    public void testParseFailesWithoutReturnGroup()
        throws ParseException
    {
        RegexFilter filter = new RegexFilter(Pattern.compile("^a.+$"));
        
        filter.parse("bc");
    }
    
    @Test(expected=ParseException.class)
    public void testParseFailesWithReturnGroup()
        throws ParseException
    {
        RegexFilter filter = new RegexFilter(Pattern.compile("^a(.+)$"));
        filter.setReturnGroup(1);
        
        filter.parse("bc");
    }
}
