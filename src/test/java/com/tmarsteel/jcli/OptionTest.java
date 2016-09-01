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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Tobias Marstaller
 */
public class OptionTest {
    
    public OptionTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testIsIdentifiedBy()
    {
        Option o = new Option("option", "o");
        
        assertTrue(o.isIdentifiedBy("option"));
        assertTrue(o.isIdentifiedBy("o"));
        assertFalse(o.isIdentifiedBy("foo"));
    }
    
    @Test
    public void testGetPrimaryIdentifier()
    {
        Option o = new Option("option", "o");
        
        assertEquals(o.getPrimaryIdentifier(), "option");
    }
    
    @Test
    public void testIsRequiredReturnsTrueWithoutDefaultValue()
    {
        Option o = new Option("option");
        
        assertTrue(o.isRequired());
    }
    
    @Test
    public void testIsRequiredReturnsFalseWithDefaultValue()
    {
        Option o = new Option(null, (Object) "default", "option");
        
        assertFalse(o.isRequired());
    }
    
    @Test
    public void testGetDefaultValue()
    {
        Option o = new Option(null, (Object) "default", "option");
        
        assertEquals(o.getDefaultValue(), "default");
    }
    
    @Test
    public void testIsAmbigousWithReturnsTrue()
    {
        Option o = new Option("option", "o");
        Option o2 = new Option("option2", "o");
        
        assertTrue(o.isAmbigousWith(o2));
        assertTrue(o2.isAmbigousWith(o));
    }
    
    public void testIsAmbigousWithRetrunsFalse()
    {
        Option o = new Option("option", "o");
        Option o2 = new Option("option2", "o2");
        
        assertFalse(o.isAmbigousWith(o2));
        assertFalse(o2.isAmbigousWith(o));
    }
}
