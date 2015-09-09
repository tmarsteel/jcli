package com.tmarsteel.jcli.rule;

import com.tmarsteel.jcli.Validator;
import com.tmarsteel.jcli.RuleNotMetException;

/**
 *
 * @author tmarsteel
 */
public class NotRule extends CombinedRule
{
    public NotRule(Rule... negates)
    {
        super(negates);
    }

    @Override
    public void validate(Validator intent, Validator.ValidatedInput params)
        throws RuleNotMetException
    {
        for (Rule rule : rules)
        {
            boolean met = false;
            try
            {
                rule.validate(intent, params);
                met = true;
            }
            catch (RuleNotMetException ex) {}

            if (met)
            {
                throw new RuleNotMetException(
                    errorMessage != null? errorMessage : "This rule may not be met: " + rule
                );
            }
        }
    }
}
