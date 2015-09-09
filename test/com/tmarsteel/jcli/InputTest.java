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
        
        assertEquals(i.getOption(option1), "value1");
        assertEquals(i.getOption(option2), "value2");
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
        
        assertEquals(i.getOption(option1), "value1");
        assertEquals(i.getOption(option2), "value2");
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
        
        assertEquals(i.getOption(option1), "value1");
        assertEquals(i.getOption(option2), "value2");
        
        assertEquals(i.getArgument(0), "value1");
        assertEquals(i.getArgument(1), "value2");
        assertNull(i.getArgument(2));
    }
}
