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
package com.tmarsteel.jcli.filter;

import com.tmarsteel.jcli.validation.ValidationException;
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
}
