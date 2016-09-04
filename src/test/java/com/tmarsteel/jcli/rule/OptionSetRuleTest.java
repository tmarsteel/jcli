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

import com.tmarsteel.jcli.*;
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
    private Argument argumentSet;
    private Argument argumentNotSet;
    private Validator subject;
    
    @Before
    public void setUp()
        throws ParseException
    {
        dummyInput = new Input(
            new Environment('\\', "-", "--"),
            new String[] { "--option", "value", "argumentSet" }
        );
        
        optionSet = new Option("option");
        flagNotSet = new Flag("flag");
        argumentSet = new Argument("argumentSet", 0);
        argumentNotSet = new Argument("argumentNotSet", 1);
        argumentNotSet.setRequired(false);

        subject = new Validator();

        subject.add(optionSet);
        subject.add(flagNotSet);
        subject.add(argumentSet);
        subject.add(argumentNotSet);
    }

    @Test
    public void testValidateSucceedsWithOptionSet()
        throws ValidationException
    {
        subject.add(new OptionSetRule(optionSet.getPrimaryIdentifier()));
        
        subject.parse(dummyInput);
    }

    @Test
    public void validateShouldSucceedWithArgumentSet()
        throws ValidationException
    {
        subject.add(new OptionSetRule(argumentSet.getIdentifier()));

        subject.parse(dummyInput);
    }

    @Test(expected = ValidationException.class)
    public void validateShouldFailWithArgumentNotSet()
        throws ValidationException
    {
        subject.add(new OptionSetRule(argumentNotSet.getIdentifier()));

        subject.parse(dummyInput);
    }

    @Test(expected=ValidationException.class)
    public void testValidateFailsWithOptionNotSet()
        throws ValidationException
    {
        subject.add(new OptionSetRule(flagNotSet.getPrimaryIdentifier()));
        
        subject.parse(dummyInput);
    }
}
