/* 
 * Copyright (C) 2015 Tobias Marstaller
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.tmarsteel.jcli.rule;

import com.tmarsteel.jcli.validation.Validator;
import com.tmarsteel.jcli.validation.RuleNotMetException;
import java.util.Iterator;
import java.util.Map;
import javax.naming.OperationNotSupportedException;

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
    public abstract void validate(Validator intent, Validator.ValidatedInput params)
        throws RuleNotMetException;
    
    /**
     * Sets the error message that is to be included in the {@link RuleNotMetException}
     * thrown by {@link #validate(Validator, Validator.ValidatedInput)}
     * @param errorMessage The error message to throw on failure.
     * @throws OperationNotSupportedException If this rule does not support error messages.
     */
    default public void setErrorMessage(String errorMessage)
        throws OperationNotSupportedException
    {
        throw new OperationNotSupportedException("This rule does not support custom error-messages");
    }
    
    public static final Rule ONLY_KNOWN_FLAGS = (Validator intent, Validator.ValidatedInput params) -> {
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
    
    public static final Rule ONLY_KNOWN_OPTIONS = (Validator intent, Validator.ValidatedInput params) -> {
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
