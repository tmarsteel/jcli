package com.wisper.cli.rule;

import com.wisper.cli.CLIParser;
import com.wisper.cli.RuleNotMetException;

/**
 * Combines multiple rules with a logical and connection: all rules have to be met.
 * @author Tobias Marstaller
 */
public class AndRule extends CombinedRule
{   
    public AndRule(Rule[] rules)
    {
        super(rules);
    }
    
    @Override
    public void validate(CLIParser intent, CLIParser.ValidatedInput params)
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
