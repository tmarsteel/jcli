package com.tmarsteel.jcli.filter;

import com.tmarsteel.jcli.XMLTest;
import com.tmarsteel.jcli.validation.ValidationException;
import java.util.regex.Pattern;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Tobias Marstaller
 */
public class RegexFilterTest extends XMLTest
{
    public RegexFilterTest()
    {
        testXML = "RegexFilterTest.xml";
        testNodesName = "filter";
    }
    
    @Test
    public void testParseSucceedsWithoutReturnGroup()
        throws ValidationException
    {
        RegexFilter filter = new RegexFilter(Pattern.compile("^a.+$"));
        
        assertEquals(filter.parse("abc"), "abc");
    }
    
    @Test
    public void testParseSucceedsWithReturnGroup()
        throws ValidationException
    {
        RegexFilter filter = new RegexFilter(Pattern.compile("^a(.+)$"));
        filter.setReturnGroup(1);
        
        assertEquals(filter.parse("abc"), "bc");
    }
    
    @Test(expected=ValidationException.class)
    public void testParseFailesWithoutReturnGroup()
        throws ValidationException
    {
        RegexFilter filter = new RegexFilter(Pattern.compile("^a.+$"));
        
        filter.parse("bc");
    }
    
    @Test(expected=ValidationException.class)
    public void testParseFailesWithReturnGroup()
        throws ValidationException
    {
        RegexFilter filter = new RegexFilter(Pattern.compile("^a(.+)$"));
        filter.setReturnGroup(1);
        
        filter.parse("bc");
    }
    
    @Test
    public void testNodeConstructor()
        throws ValidationException
    {
        RegexFilter f = new RegexFilter(testNodes.item(0));
        
        assertEquals(f.getPattern().pattern(), "^a(.+)f$");
        assertEquals(f.getReturnGroup(), 1);
    }
    
    @Test(expected = ValidationException.class)
    public void nodeConstructorShouldFailOnNonNumericReturnGroup()
        throws ValidationException
    {
        RegexFilter f = new RegexFilter(testNodes.item(1));
    }
}
