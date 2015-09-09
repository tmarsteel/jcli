package com.tmarsteel.jcli.rule;

import com.tmarsteel.jcli.Validator;
import com.tmarsteel.jcli.RuleNotMetException;

/**
 * Combines multiple rules with a logical and connection: all rules have to be met.
 * @author tmarsteel
 */
public class AndRule extends CombinedRule
{   
    public AndRule(Rule... rules)
    {
        super(rules);
    }
    
    @Override
    public void validate(Validator intent, Validator.ValidatedInput params)
        throws RuleNotMetException
    {
        for (Rule r:rules)
        {
            r.validate(intent, params);
        }
    }
    
    public String toString()
    {
        String str = "(All of these conditions have to be met:\n";
        for (Rule r:rules)
        {
            str += r + "\n";
        }
        return str + ")";
    }
}
