package com.tmarsteel.jcli.filter;

import com.tmarsteel.jcli.validation.ValidationException;
import java.io.IOException;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author Tobias Marstaller
 */
public class RegexFilterTest
{
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
    
    // -----------------

}
