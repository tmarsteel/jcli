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
public class InputTest {
   
    private Environment env;
    
    public InputTest()
    {
        env = new Environment('\\', "-", "--");
    }
    
    @Test
    public void testAddStringArrayWithFalgsOnly()
        throws ParseException
    {
        Input i = new Input();
        Flag flag1 = new Flag("flag1");
        Flag flag2 = new Flag("flag2", "f2");
        
        i.add(env, new String[] {"-flag1", "-f2"});
        
        assertTrue(i.containsFlag(flag1));
        assertTrue(i.containsFlag(flag2));
    }
    
    @Test
    public void testAddStringArrayWithOptionsOnly()
        throws ParseException
    {
        Input i = new Input();
        Option option1 = new Option("option1");
        Option option2 = new Option("option2", "o2");
        
        i.add(env, new String[] {"--option1", "value1", "--option2", "value2"});
        
        assertEquals("value1", i.getOption(option1).get(0));
        assertEquals("value2", i.getOption(option2).get(0));
    }
    
    @Test
    public void testAddStringArrayWithArgumentsOnly()
        throws ParseException
    {
        Input i = new Input();
        
        i.add(env, new String[] {"value1", "value2"});
        
        assertEquals(i.getArgument(0), "value1");
        assertEquals(i.getArgument(1), "value2");
        assertNull(i.getArgument(2));
    }
    
    @Test
    public void testAddStringArrayWithFlagsAndOptions()
        throws ParseException
    {
        Input i = new Input();
        Flag flag1 = new Flag("flag1");
        Flag flag2 = new Flag("flag2", "f2");
        
        Option option1 = new Option("option1");
        Option option2 = new Option("option2", "o2");
        
        i.add(env, new String[] {"-flag1", "--option1", "value1", "--option2", "value2", "-f2"});
        
        assertTrue(i.containsFlag(flag1));
        assertTrue(i.containsFlag(flag2));
        
        assertEquals("value1", i.getOption(option1).get(0));
        assertEquals("value2", i.getOption(option2).get(0));
    }
    
    @Test
    public void testAddStringArrayWithAllElements()
        throws ParseException
    {
        Input i = new Input();
        Flag flag1 = new Flag("flag1");
        Flag flag2 = new Flag("flag2", "f2");
        
        Option option1 = new Option("option1");
        Option option2 = new Option("option2", "o2");
        
        i.add(env, new String[] {"-flag1", "--option1", "value1", "--option2", "value2", "-f2", "value1", "value2"});
        
        assertTrue(i.containsFlag(flag1));
        assertTrue(i.containsFlag(flag2));
        
        assertEquals("value1", i.getOption(option1).get(0));
        assertEquals("value2", i.getOption(option2).get(0));
        
        assertEquals("value1", i.getArgument(0));
        assertEquals("value2", i.getArgument(1));
        assertNull(i.getArgument(2));
    }
}
