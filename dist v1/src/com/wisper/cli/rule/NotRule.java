/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wisper.cli.rule;

import com.wisper.cli.CLIParser;
import com.wisper.cli.RuleNotMetException;

/**
 *
 * @author Tobse
 */
public class NotRule extends CombinedRule
{
    public NotRule(Rule[] negates)
    {
        super(negates);
    }

    @Override
    public void validate(CLIParser intent, CLIParser.ValidatedInput params)
        throws RuleNotMetException
    {
        for (Rule rule : rules)
        {
            try
            {
                rule.validate(intent, params);
                throw new RuleNotMetException("This rule may not be met: " + rule);
            }
            catch (RuleNotMetException ex) {}
        }
    }
}
