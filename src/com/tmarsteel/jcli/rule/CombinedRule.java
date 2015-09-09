package com.tmarsteel.jcli.rule;

/**
 * Combines multiple rules with a logical connection.
 * @author tmarsteel
 */
public abstract class CombinedRule extends BaseRule
{
    protected Rule[] rules;
    
    public CombinedRule(Rule... rules)
    {
        this.rules = rules;
    }
}
