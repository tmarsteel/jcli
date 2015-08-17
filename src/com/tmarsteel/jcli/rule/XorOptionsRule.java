package com.tmarsteel.jcli.rule;

import com.tmarsteel.jcli.CLIParser;
import com.tmarsteel.jcli.Option;
import com.tmarsteel.jcli.RuleNotMetException;

/**
 * Accepts input if exactly one of the given options/flags is set/specified. Use
 * this class instead of {@link XorRule} for a more user-friendly error message.
 * @author tmarsteel
 */
public class XorOptionsRule extends BaseRule
{
    protected Option[] options;
    
    /**
     * @param options The options/flags to connect.
     */
    public XorOptionsRule(Option... options)
    {
        this.options = options;
    }
    
    @Override
    public void validate(CLIParser intent, CLIParser.ValidatedInput params)
        throws RuleNotMetException
    {
        Option prevSet = null;
        
        for (Option o:options)
        {
            boolean curSet = false;
            if (o.isFlag())
            {
                curSet = params.isFlagSet(o);
            }
            else
            {
                curSet = params.getOption(o) != null;
            }
            if (curSet)
            {
                if (prevSet != null)
                {
                    throw new RuleNotMetException(
                        errorMessage != null ? errorMessage :
                        prevSet + " and " + o + " cannot be set at the same time."
                    );
                }
                prevSet = o;
            }
        }
    }
    
    @Override
    public String toString()
    {
        String str = "(Exactly one of these can be set at the same time:\n";
        for (Option o:options)
        {
            str += o + "\n";
        }
        return str + ')';
    }
}
