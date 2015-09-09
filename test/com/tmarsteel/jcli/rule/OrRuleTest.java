package com.tmarsteel.jcli.rule;

import com.tmarsteel.jcli.Input;
import com.tmarsteel.jcli.ParseException;
import com.tmarsteel.jcli.RuleNotMetException;
import com.tmarsteel.jcli.Validator;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Tobias Marstaller
 */
public class OrRuleTest
{
    protected Validator intent;
    protected Validator.ValidatedInput input;
    
    @Before
    public void setUp()
        throws ParseException
    {
        intent = new Validator();
        Input dummyInput = new Input();
        input = intent.parse(dummyInput);
    }
    
    @Test
    public void testValidateSucceedsWithTrueTrue()
        throws RuleNotMetException
    {
        OrRule rule = new OrRule((intent, input) -> {
            return;
        }, (intent, input) -> {
            return;
        });
        
        rule.validate(intent, input);
    }
    
    @Test
    public void testValidateSucceedsWithTrueFalse()
        throws RuleNotMetException
    {
        OrRule rule = new OrRule((intent, input) -> {
            return;
        }, (intent, input) -> {
            throw new RuleNotMetException("Testfail");
        });
        
        rule.validate(intent, input);
    }
    
    @Test(expected=RuleNotMetException.class)
    public void testValidateFailsWithFalseFalse()
        throws RuleNotMetException
    {
        OrRule rule = new OrRule((intent, input) -> {
            throw new RuleNotMetException("Testfail 1");
        }, (intent, input) -> {
            throw new RuleNotMetException("Testfail 2");
        });
        
        rule.validate(intent, input);
    }
}
