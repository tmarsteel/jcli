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
 * Combines multiple rules with a logical or connection: at least one has to be met.
 * @author tmarsteel
 */
public class OrRule extends CombinedRule
{   
    public OrRule(Rule... rules)
    {
        super(rules);
    }
    
    @Override
    public void validate(Validator intent, Validator.ValidatedInput params)
        throws RuleNotMetException
    {
        RuleNotMetException lastEx = null;
        for (Rule r:rules)
        {
            try
            {
                r.validate(intent, params);
                return;
            }
            catch (RuleNotMetException ex)
            {
                lastEx = ex;
            }
        }
        throw new RuleNotMetException(
            errorMessage != null? errorMessage : toString(),
        lastEx);
    }
    
    public String toString()
    {
        String str = "(At least one of these conditions have to be met:\n";
        for (Rule r:rules)
        {
            str += r + "\n";
        }
        return str + ")";
    }
}
