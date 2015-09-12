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
