package com.tmarsteel.jcli.rule;

import com.tmarsteel.jcli.CLIParser;
import com.tmarsteel.jcli.RuleNotMetException;
import java.util.Iterator;
import java.util.Map;

/**
 * Represents a rule regarding the precence and values of options and flags.
 * @author Tobias Marstaller
 */
public abstract class Rule
{
    protected String errorMessage;
    
    public static final Rule ONLY_KNOWN_FLAGS = new Rule()
    {
        @Override
        public void validate(CLIParser intent, CLIParser.ValidatedInput params)
            throws RuleNotMetException
        {
            final Iterator<Map.Entry<String,Boolean>> flagIt = params.getFlagIterator();
            String flagName;

            while (flagIt.hasNext())
            {
                flagName = flagIt.next().getKey();
                if (!intent.knowsFlag(flagName))
                {
                    throw new RuleNotMetException("Unknown flag: " + flagName);
                }
            }
        }
    };
    public static final Rule ONLY_KNOWN_OPTIONS = new Rule()
    {
        @Override
        public void validate(CLIParser intent, CLIParser.ValidatedInput params)
            throws RuleNotMetException
        {
            Iterator<Map.Entry<String,Object>> optIt = params.getOptionIterator();
            String optName;
            
            while (optIt.hasNext())
            {
                optName = optIt.next().getKey();
                if (!intent.knowsOption(optName))
                {
                    throw new RuleNotMetException("Unknown option: " + optName);
                }
            }
        }
    };
    /**
     * Checks whether <code>params</code> fulfills this rule with respect to the
     * requirements set by <code>intent</code>
     * @param params Input parameters.
     * @throws RuleNotMetException If this rule is not fulfilled by <code>params</code>.
     */
    public abstract void validate(CLIParser intent, CLIParser.ValidatedInput params)
        throws RuleNotMetException;
    
    public void setErrorMessage(String msg)
    {
        this.errorMessage = msg;
    }
}
