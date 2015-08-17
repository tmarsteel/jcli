package com.tmarsteel.jcli.rule;

import com.tmarsteel.jcli.CLIParser;
import com.tmarsteel.jcli.RuleNotMetException;

/**
 * Combines multiple rules with a logical or connection: at least one has to be met.
 * @author tmarsteel
 */
public class OrRule extends CombinedRule
{   
    public OrRule(BaseRule[] rules)
    {
        super(rules);
    }
    
    @Override
    public void validate(CLIParser intent, CLIParser.ValidatedInput params)
        throws RuleNotMetException
    {
        RuleNotMetException lastEx = null;
        for (BaseRule r:rules)
        {
            try
            {
                r.validate(intent, params);
                return;
            }
            catch (RuleNotMetException ex)
            {
                lastEx = ex;
            }
        }
        throw new RuleNotMetException(
            errorMessage != null? errorMessage : toString(),
        lastEx);
    }
    
    public String toString()
    {
        String str = "(At least one of these conditions have to be met:\n";
        for (BaseRule r:rules)
        {
            str += r + "\n";
        }
        return str + ")";
    }
}
