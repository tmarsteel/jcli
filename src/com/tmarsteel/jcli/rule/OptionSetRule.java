package com.tmarsteel.jcli.rule;

import com.tmarsteel.jcli.CLIParser;
import com.tmarsteel.jcli.Option;
import com.tmarsteel.jcli.RuleNotMetException;

/**
 * Requires that the given option(s) is/are set.
 * @author Tobse
 */
public class OptionSetRule extends Rule
{
    protected Option[] options = null;
    
    /**
     * @param options The options to require.
     */
    public OptionSetRule(Option... options)
    {
        if (options.length == 0)
        {
            throw new IllegalArgumentException("Need to specify at least one option.");
        }
        this.options = options;
    }

    @Override
    public void validate(CLIParser intent, CLIParser.ValidatedInput params)
        throws RuleNotMetException
    {
        for (Option o:options)
        {
            if (o.isFlag())
            {
                if (!params.isFlagSet(o))
                {
                    throw new RuleNotMetException("Required flag "
                        + o.getPrimaryIdentifier() + " not set.");
                }
            }
            else
            {
                if (params.getOption(o) == null)
                {
                    throw new RuleNotMetException("Required option "
                        + o.getPrimaryIdentifier() + " not set.");
                }
            }
        }
    }
    
    @Override
    public String toString()
    {
        if (options.length == 1)
        {
            return options[0].toString() + " is set";
        }
        else
        {
            String str = "(These options are specified:\n";
            for (Option o:options)
            {
                str += (o.isFlag()? "Flag " : "Option ")
                    + o.getPrimaryIdentifier() + "\n";
            }
            return str + ")";
        }
    }
}
