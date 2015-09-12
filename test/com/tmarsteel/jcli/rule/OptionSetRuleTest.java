package com.tmarsteel.jcli.rule;

import com.tmarsteel.jcli.Environment;
import com.tmarsteel.jcli.Flag;
import com.tmarsteel.jcli.Input;
import com.tmarsteel.jcli.Option;
import com.tmarsteel.jcli.ParseException;
import com.tmarsteel.jcli.validator.RuleNotMetException;
import com.tmarsteel.jcli.validator.Validator;
import com.tmarsteel.jcli.filter.Filter;
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
        throws ParseException
    {
        Validator v = newValidator();
        v.add(new OptionSetRule(optionSet.getPrimaryIdentifier()));
        
        v.parse(dummyInput);
    }
    
    @Test(expected=ParseException.class)
    public void testValidateFailsWithOptionNotSet()
        throws ParseException
    {
        Validator v = newValidator();
        v.add(new OptionSetRule(flagNotSet.getPrimaryIdentifier()));
        
        v.parse(dummyInput);
    }
}
