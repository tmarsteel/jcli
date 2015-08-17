package com.tmarsteel.jcli.rule;

/**
 * Combines multiple rules with a logical connection.
 * @author tmarsteel
 */
public abstract class CombinedRule extends BaseRule
{
    protected BaseRule[] rules;
    
    public CombinedRule(BaseRule[] rules)
    {
        this.rules = rules;
    }
}
