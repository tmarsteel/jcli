package com.wisper.cli.rule;

import com.wisper.cli.CLIParser;
import com.wisper.cli.RuleNotMetException;

/**
 *
 * @author Tobse
 */
public class NotRule extends CombinedRule
{
    public NotRule(Rule[] negates)
    {
        super(negates);
    }

    @Override
    public void validate(CLIParser intent, CLIParser.ValidatedInput params)
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
