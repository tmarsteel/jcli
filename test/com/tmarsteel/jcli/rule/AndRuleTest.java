package com.tmarsteel.jcli.rule;

import com.tmarsteel.jcli.Input;
import com.tmarsteel.jcli.ParseException;
import com.tmarsteel.jcli.validator.RuleNotMetException;
import com.tmarsteel.jcli.validator.ValidationException;
import com.tmarsteel.jcli.validator.Validator;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Tobias Marstaller
 */
public class AndRuleTest
{    
    protected Validator intent;
    protected Validator.ValidatedInput input;
    
    @Before
    public void setUp()
        throws ParseException, ValidationException
    {
        intent = new Validator();
        Input dummyInput = new Input();
        input = intent.parse(dummyInput);
    }
    
    @Test
    public void testValidate()
        throws RuleNotMetException
    {
        AndRule rule = new AndRule((intent, input) -> {
            return;
        }, (intent, input) -> {
            return;
        });
        
        rule.validate(intent, input);
    }
    
    @Test(expected=RuleNotMetException.class)
    public void testValidateFails()
        throws RuleNotMetException
    {
        AndRule rule = new AndRule((intent, input) -> {
            return;
        }, (intent, input) -> {
            throw new RuleNotMetException("Testfail");
        });
        
        rule.validate(intent, input);
    }
}
