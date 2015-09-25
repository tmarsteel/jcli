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
    public void shouldFindOptionAlias()
        throws ParseException, ValidationException
    {
        Input input = new Input(
            env,
            new String[]{ "-flag", "--o", "value", "argument" }
        );
        
        Option o = new Option("option", "o");
        
        Validator v = new Validator(env);
        v.add(o);
        
        Validator.ValidatedInput vI = v.parse(input);
        
        assertEquals(vI.getOption("o"), "value");
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
}
