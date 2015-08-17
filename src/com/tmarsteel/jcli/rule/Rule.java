package com.tmarsteel.jcli.rule;

import com.tmarsteel.jcli.CLIParser;
import com.tmarsteel.jcli.RuleNotMetException;
import java.util.Iterator;
import java.util.Map;

/**
 * @author tmarsteel
 */
public interface Rule
{
    /**
     * Checks whether <code>params</code> fulfills this rule with respect to the
     * requirements set by <code>intent</code>
     * @param params Input parameters.
     * @throws RuleNotMetException If this rule is not fulfilled by <code>params</code>.
     */
    public void validate(CLIParser intent, CLIParser.ValidatedInput params)
        throws RuleNotMetException;
    
    public static final Rule ONLY_KNOWN_FLAGS = (CLIParser intent, CLIParser.ValidatedInput params) -> {
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
    };
    
    public static final Rule ONLY_KNOWN_OPTIONS = (CLIParser intent, CLIParser.ValidatedInput params) -> {
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
    };
}
