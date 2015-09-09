package com.tmarsteel.jcli;

import com.tmarsteel.jcli.filter.Filter;
import java.util.HashMap;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Tobias Marstaller
 */
public class ArgumentTest {
    
    public ArgumentTest() {
    }

    @Test
    public void parseShouldReturnValueWithoutFilter()
        throws ParseException
    {
        Argument a = new Argument("name", 0);
        
        String testValue = "ölaksjdfgös8274124...";
        
        assertEquals(a.parse(testValue), testValue);
    }
    
    @Test
    public void parseShouldInvokeFilter()
        throws ParseException
    {
        final Map<String,Boolean> expectedCalls = new HashMap<>();
        
        Filter filterMock = (value) -> {
            expectedCalls.put("parse", true);
            return value;
        };
        
        Argument a = new Argument("name", 0, null, filterMock);
        
        String testValue = "ölaksjdfgös8274124...";
        
        assertEquals(a.parse(testValue), testValue);
        assertTrue("com.tmarsteel.jcli.Argument#parse(String) did not call com.tmarsteel.jcli.filter.Filter#parse(String)", expectedCalls.containsKey("parse") && expectedCalls.get("parse"));
    }
}
