package com.tmarsteel.jcli.rule;

/**
 * Combines multiple rules with a logical connection.
 * @author Tobias Marstaller
 */
public abstract class CombinedRule extends Rule
{
    protected Rule[] rules;
    
    public CombinedRule(Rule[] rules)
    {
        this.rules = rules;
    }
}
