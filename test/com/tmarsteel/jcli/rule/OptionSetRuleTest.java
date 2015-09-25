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
public class OptionSetRuleTest
{
    private Input dummyInput;
    
    private Option optionSet;
    private Flag flagNotSet;
    
    @Before
    public void setUp()
        throws ParseException
    {
        dummyInput = new Input(
            new Environment('\\', "-", "--"),
            new String[] { "--option", "value" }
        );
        
        optionSet = new Option("option");
        flagNotSet = new Flag("flag");
    }
    
    private Validator newValidator()
    {
        Validator v = new Validator();
        
        v.add(optionSet);
        v.add(flagNotSet);
        
        return v;
    }
    
    @Test
    public void testValidateSucceedsWithOptionSet()
        throws ValidationException
    {
        Validator v = newValidator();
        v.add(new OptionSetRule(optionSet.getPrimaryIdentifier()));
        
        v.parse(dummyInput);
    }
    
    @Test(expected=ValidationException.class)
    public void testValidateFailsWithOptionNotSet()
        throws ValidationException
    {
        Validator v = newValidator();
        v.add(new OptionSetRule(flagNotSet.getPrimaryIdentifier()));
        
        v.parse(dummyInput);
    }
}
