/* 
 * Copyright (C) 2015 Tobias Marstaller
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.tmarsteel.jcli;

import com.tmarsteel.jcli.filter.Filter;
import com.tmarsteel.jcli.validation.ValidationException;
import java.util.HashMap;
import java.util.Map;
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
        throws ValidationException
    {
        Argument a = new Argument("name", 0);
        
        String testValue = "ölaksjdfgös8274124...";
        
        assertEquals(a.parse(testValue), testValue);
    }
    
    @Test
    public void parseShouldInvokeFilter()
        throws ValidationException
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
