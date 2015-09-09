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
public class NotRuleTest
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
    
    @Test(expected=RuleNotMetException.class)
    public void testValidateFailsWithSucceedingRule()
        throws RuleNotMetException
    {
        NotRule rule = new NotRule((intent, input) -> {
            return;
        });
        
        rule.validate(intent, input);
    }
    
    @Test
    public void testValidateSucceedsWithFailingRule()
        throws RuleNotMetException
    {
        NotRule rule = new NotRule((intent, input) -> {
            throw new RuleNotMetException("Testfail");
        });
        
        rule.validate(intent, input);
    }
}
