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
package com.tmarsteel.jcli.rule;

import com.tmarsteel.jcli.Environment;
import com.tmarsteel.jcli.Flag;
import com.tmarsteel.jcli.Input;
import com.tmarsteel.jcli.Option;
import com.tmarsteel.jcli.ParseException;
import com.tmarsteel.jcli.validation.ValidationException;
import com.tmarsteel.jcli.validation.Validator;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Tobias Marstaller
 */
public class XorOptionsRuleTest
{
    private Input dummyInput;
    
    private Option optionSet;
    
    private Flag flagSet;
    private Flag flagNotSet;
    private Flag flag2NotSet;
    
    @Before
    public void setUp()
        throws ParseException
    {
        dummyInput = new Input(
            new Environment('\\', "-", "--"),
            new String[] { "-flag", "--option", "value" }
        );
        
        optionSet = new Option("option");
        
        flagSet = new Flag("flag");
        flagNotSet = new Flag("flag2");
        flag2NotSet = new Flag("flag3");
    }
    
    private Validator newValidator()
    {
        Validator v = new Validator();
        
        v.add(optionSet);
        
        v.add(flagSet);
        v.add(flagNotSet);
        
        return v;
    }
    
    @Test(expected=ValidationException.class)
    public void testFailsWithTrueTrue()
        throws ValidationException
    {
        Validator v = newValidator();
        v.add(new XorOptionsRule(flagSet, optionSet));
        
        v.parse(dummyInput);
    }
    
    @Test
    public void testSucceedsWithTrueFalse()
        throws ValidationException
    {
        Validator v = newValidator();
        v.add(new XorOptionsRule(flagSet, flagNotSet));
        
        v.parse(dummyInput);
    }
    
    @Test(expected=ValidationException.class)
    public void testFailsWithFalseFalse()
        throws ValidationException
    {
        Validator v = newValidator();
        v.add(new XorOptionsRule(flagNotSet, flag2NotSet));
        
        v.parse(dummyInput);
    }
}
