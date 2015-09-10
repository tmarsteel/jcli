package com.tmarsteel.jcli.rule;

import com.tmarsteel.jcli.Flag;
import com.tmarsteel.jcli.Identifiable;
import com.tmarsteel.jcli.Validator;
import com.tmarsteel.jcli.RuleNotMetException;

/**
 * Accepts input if exactly one of the given options/flags is set/specified. Use
 * this class instead of {@link XorRule} for a more user-friendly error message.
 * @author tmarsteel
 */
public class XorOptionsRule extends BaseRule
{
    protected Identifiable[] options;
    
    /**
     * @param options The options/flags to connect.
     */
    public XorOptionsRule(Identifiable... options)
    {
        this.options = options;
    }
    
    @Override
    public void validate(Validator intent, Validator.ValidatedInput params)
        throws RuleNotMetException
    {
        Identifiable prevSet = null;
        
        for (Identifiable o:options)
        {
            boolean curSet = false;
            if (o instanceof Flag)
            {
                curSet = params.isFlagSet(o.getPrimaryIdentifier());
            }
            else
            {
                curSet = params.getOption(o.getPrimaryIdentifier()) != null;
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
        
        if (prevSet == null)
        {
            throw new RuleNotMetException(
                errorMessage != null ? errorMessage :
                "Either one of these options/flags must be set."
            );
        }
    }
    
    @Override
    public String toString()
    {
        String str = "(Exactly one of these can be set at the same time:\n";
        for (Identifiable o:options)
        {
            str += o + "\n";
        }
        return str + ')';
    }
}
