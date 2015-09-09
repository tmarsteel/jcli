package com.tmarsteel.jcli.rule;

import com.tmarsteel.jcli.Validator;
import com.tmarsteel.jcli.RuleNotMetException;

/**
 * Only accepts input when exactly one of the specified rules are met.
 * @author tmarsteel
 */
public class XorRule extends CombinedRule
{
    /**
     * @param rules The rules to connect.
     */
    public XorRule(BaseRule... rules)
    {
        super(rules);
    }

    @Override
    public void validate(Validator intent, Validator.ValidatedInput params)
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
