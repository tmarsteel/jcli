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

import com.tmarsteel.jcli.XMLTest;
import com.tmarsteel.jcli.validation.ValidationException;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Tobias Marstaller
 */
public class SetFilterTest extends XMLTest
{
    public SetFilterTest()
    {
        this.testXML = "SetFilterTest.xml";
        this.testNodesName = "filter";
    }
    
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
    
    @Test
    public void testNodeConstructor_A()
        throws ValidationException
    {
        SetFilter filter = new SetFilter(testNodes.item(0));
        
        Collection<String> expectedOptions = Arrays.asList(new String[]{ "foo", "bar" });
        
        assertEquals(filter.options(), expectedOptions);
        assertFalse(filter.isCaseSensitive());
    }
    
    @Test
    public void testNodeConnstructor_B()
        throws ValidationException
    {
        SetFilter filter = new SetFilter(testNodes.item(1));
        
        Collection<String> expectedOptions = Arrays.asList(new String[]{ "foo", "bar" });
        
        assertEquals(filter.options(), expectedOptions);
        assertTrue(filter.isCaseSensitive());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void nodeConstructorShouldFailOnEmptyOptionSet()
        throws ValidationException
    {
        SetFilter filter = new SetFilter(testNodes.item(2));
    }
}
