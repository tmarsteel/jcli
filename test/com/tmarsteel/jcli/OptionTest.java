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
