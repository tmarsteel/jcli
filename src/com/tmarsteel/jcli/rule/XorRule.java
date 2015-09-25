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

/**
 * Only accepts input when exactly one of the specified rules are met.
 * @author tmarsteel
 */
public class XorRule extends CombinedRule
{
    /**
     * @param rules The rules to connect.
     */
    public XorRule(Rule... rules)
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
