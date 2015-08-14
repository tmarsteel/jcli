package com.wisper.cli.rule;

import com.wisper.cli.CLIParser;
import com.wisper.cli.RuleNotMetException;

/**
 * Only accepts input when exactly one of the specified rules are met.
 * @author Tobias Marstaller
 */
public class XorRule extends CombinedRule
{
    /**
     * @param rules The rules to connect.
     */
    public XorRule(Rule[] rules)
    {
        super(rules);
    }

    @Override
    public void validate(CLIParser intent, CLIParser.ValidatedInput params)
        throws RuleNotMetException
    {
        boolean isSet = false;
        for (Rule r : rules)
        {
            boolean curSet = false;
            try
            {
                r.validate(intent, params);
                curSet = true;
            }
            catch (RuleNotMetException ex) {}

            if (curSet)
            {
                if (isSet)
                {
                    throw new RuleNotMetException(getErrorMessage());
                }
                isSet = true;
            }
        }
        if (!isSet)
        {
            throw new RuleNotMetException(getErrorMessage());
        }
    }

    private String getErrorMessage()
    {
        return errorMessage == null? toString() : errorMessage;
    }

    public String toString()
    {
        String str = "(Exactly one of these conditions has to be met:\n";
        for (Rule r:rules)
        {
            str += r + "\n";
        }
        return str + ")";
    }
}
