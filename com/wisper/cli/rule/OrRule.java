package com.wisper.cli.rule;

import com.wisper.cli.CLIParser;
import com.wisper.cli.RuleNotMetException;

/**
 * Combines multiple rules with a logical or connection: at least one has to be met.
 * @author Tobias Marstaller
 */
public class OrRule extends CombinedRule
{   
    public OrRule(Rule[] rules)
    {
        super(rules);
    }
    
    @Override
    public void validate(CLIParser intent, CLIParser.ValidatedInput params)
        throws RuleNotMetException
    {
        RuleNotMetException lastEx = null;
        for (Rule r:rules)
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
        for (Rule r:rules)
        {
            str += r + "\n";
        }
        return str + ")";
    }
}
