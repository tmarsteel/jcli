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
package com.tmarsteel.jcli.validation;

import com.tmarsteel.jcli.Argument;
import com.tmarsteel.jcli.Environment;
import com.tmarsteel.jcli.Flag;
import com.tmarsteel.jcli.Input;
import com.tmarsteel.jcli.Option;
import com.tmarsteel.jcli.ParseException;
import com.tmarsteel.jcli.rule.Rule;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.mockito.Mockito.*;

/**
 * @author Tobias Marstaller
 */
public class ValidatorTest
{
    private Environment env; 

    @Before
    public void setUp()
        throws ParseException
    {
        env = new Environment('\\', "-", "--");
    }
    
    @Test
    public void shouldKnowFlagByName()
    {
        Validator v = new Validator(env);
        Flag f = new Flag("flag");
        
        v.add(f);
        
        assertTrue(v.knowsFlag("flag"));
    }
    
    @Test
    public void shouldKnowOptionByName()
    {
        Validator v = new Validator(env);
        Option o = new Option("option");
        
        v.add(o);
        
        assertTrue(v.knowsOption("option"));
    }
    
    @Test
    public void shouldKnowArgumentByName()
    {
        Validator v = new Validator(env);
        Argument a = new Argument("arg", 0);
        
        v.add(a);
        
        assertTrue(v.knowsOption("arg"));
    }
    
    @Test
    public void shouldFindFlagPrimaryIdentifier()
        throws ParseException, ValidationException
    { 
        Input input = new Input(
            env,
            new String[]{ "-flag", "--option", "value", "argument" }
        );
        
        Flag f = new Flag("flag");
        
        Validator v = new Validator(env);
        v.add(f);
        
        Validator.ValidatedInput vI = v.parse(input);
        
        assertTrue(vI.isFlagSet(f.getPrimaryIdentifier()));
    }
    
    @Test
    public void shouldFindFlagAlias()
        throws ParseException, ValidationException
    {
        Input input = new Input(
            env,
            new String[]{ "-f", "--option", "value", "argument" }
        );
        
        Flag f = new Flag("flag", "f");
        
        Validator v = new Validator(env);
        v.add(f);
        
        Validator.ValidatedInput vI = v.parse(input);
        
        assertTrue(vI.isFlagSet("f"));
    }
    
    @Test
    public void shouldFindOptionPrimaryIdentifier()
        throws ParseException, ValidationException
    { 
        Input input = new Input(
            env,
            new String[]{ "-flag", "--option", "value", "argument" }
        );
        
        Option o = new Option("option");
        
        Validator v = new Validator(env);
        v.add(o);
        
        Validator.ValidatedInput vI = v.parse(input);
        
        assertEquals(vI.getOption(o.getPrimaryIdentifier()), "value");
    }
    
    @Test
    public void shouldFindArgumentByName()
        throws ParseException, ValidationException
    {
        Input input = new Input(
            env,
            new String[]{ "-flag", "--option", "value", "argument" }
        );
        
        Argument a = new Argument("someArg", 0);
        
        Validator v = new Validator(env);
        v.add(a);
        
        Validator.ValidatedInput vI = v.parse(input);
        
        assertEquals(vI.getOption("someArg"), "argument");
    }
    
    @Test
    public void parseShouldCallThroughOptionParse()
        throws ParseException, ValidationException
    {
        Validator v = new Validator(env);
        
        Option o = spy(new Option("o"));
        v.add(o);
        
        Input input = new Input(
            env,
            new String[] { "-flag", "--o", "value", "arg" }
        );
        
        v.parse(input);
        
        verify(o).parse("value");
    }
    
    @Test
    public void parseShouldCallThroughArgumentParse()
        throws ParseException, ValidationException
    {
        Validator v = new Validator(env);
        
        Argument a = spy(new Argument("arg", 0));
        v.add(a);
        
        Input input = new Input(
            env,
            new String[] { "-flag", "--o", "value", "argVal" }
        );
        
        v.parse(input);
        
        verify(a).parse("argVal");
    }
    
    @Test
    public void parseShouldCallThroughRuleValidate()
        throws ParseException, ValidationException
    {
        Validator v = new Validator(env);
        Rule r = mock(Rule.class);     
        v.add(r);
        Input input = new Input(
            env,
            new String[] { "-flag", "--o", "value", "argVal" }
        );
        
        v.parse(input);
        
        verify(r).validate(any(Validator.class), (Validator.ValidatedInput) notNull());
    }
    
    @Test(expected = RuleNotMetException.class)
    public void parseShouldCallThroughRuleValidateAndPassException()
        throws ValidationException, ParseException
    {
        Validator v = new Validator(env);
        
        Rule r = mock(Rule.class);     
        doThrow(new RuleNotMetException("testmessage")).when(r).validate(any(Validator.class), (Validator.ValidatedInput) notNull());
        
        v.add(r);
        Input input = new Input(
            env,
            new String[] { "-flag", "--o", "value", "argVal" }
        );
        
        v.parse(input);
    }
    
    @Test
    public void resetShouldRemoveArguments()
    {
        Validator v = new Validator();
        
        Argument a = new Argument("arg", 0);
        
        v.add(a);
        
        v.reset();
        
        assertFalse(v.knowsOption("arg"));
    }
    
    @Test
    public void resetShouldRemoveOptions()
    {
        Validator v = new Validator();
        
        Option o = new Option("o");
        v.add(o);
        
        v.reset();
        
        assertFalse(v.knowsOption("o"));
    }
    
    @Test
    public void resetShouldRemoveFlags()
    {
        Validator v = new Validator();
        
        Flag f = new Flag("flag");
        v.add(f);
        
        v.reset();
        
        assertFalse(v.knowsOption("flag"));
    }

    @Test(expected = MisconfigurationException.class)
    public void shouldNotAllowMultipleVariadicArguments()
    {
        Validator v = new Validator();

        Argument arg1 = new Argument("arg1", 0);
        arg1.setVariadic(true);
        Argument arg2 = new Argument("arg2", 1);
        arg2.setVariadic(true);

        v.add(arg1);
        v.add(arg2);
    }

    @Test(expected = MisconfigurationException.class)
    public void shouldNotAllowVariadicArgumentAtNonGreatestIndex_NonVariadicAddedLast()
    {
        Validator v = new Validator();

        Argument arg1 = new Argument("arg1", 0);
        arg1.setVariadic(true);
        Argument arg2 = new Argument("arg2", 1);

        v.add(arg1); // DIFFERENCE TO THE FOLLOWING TEST: add order
        v.add(arg2);
    }

    @Test(expected = MisconfigurationException.class)
    public void shouldNotAllowVariadicArgumentAtNonGreatestIndex_VariadicAddedLast()
    {
        Validator v = new Validator();

        Argument arg1 = new Argument("arg1", 0);
        arg1.setVariadic(true);
        Argument arg2 = new Argument("arg2", 1);

        v.add(arg2); // DIFFERENCE TO THE PREVIOUS TEST: add order
        v.add(arg1);
    }

    @Test
    public void testVarargs()
        throws ParseException, ValidationException
    {
        Validator v = new Validator();

        Argument arg1 = new Argument("arg1", 0);
        Argument arg2 = new Argument("arg2", 1);
        arg2.setVariadic(true);

        v.add(arg1);
        v.add(arg2);

        Validator.ValidatedInput vi = v.parse(new String[]{"arg1value", "arg2value1", "arg2value2", "arg2value3"});
        List<Object> arg2Values = (List<Object>) vi.getOption("arg2");

        assertEquals("arg1value", vi.getOption("arg1"));
        assertEquals(3, arg2Values.size());
        assertEquals("arg2value1", arg2Values.get(0));
        assertEquals("arg2value2", arg2Values.get(1));
        assertEquals("arg2value3", arg2Values.get(2));
    }
}
