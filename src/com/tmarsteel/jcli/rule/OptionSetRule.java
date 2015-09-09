package com.tmarsteel.jcli.rule;

import com.tmarsteel.jcli.Validator;
import com.tmarsteel.jcli.Option;
import com.tmarsteel.jcli.RuleNotMetException;
import com.tmarsteel.jcli.parser.MisconfigurationException;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Requires that the given option(s) is/are set.
 * @author tmarsteel
 */
public class OptionSetRule extends BaseRule
{
    protected String[] optionNames = null;
    
    public OptionSetRule(Node ruleNode)
    {
        NodeList children = ruleNode.getChildNodes();
        List<String> optionNames = new ArrayList<>();
        for (int i = 0;i < children.getLength();i++)
        {
            Node node = children.item(i);
            if (node.getNodeName().equals("option"))
            {
                optionNames.add(node.getTextContent());
            }
            else if (node.getNodeName().equals("error"))
            {
                this.errorMessage = node.getTextContent();
            }
            else if (!node.getNodeName().equals("#text"))
            {
                throw new MisconfigurationException("The option-set rule allows only <option> subtags");
            }
        }
        
        this.optionNames = new String[optionNames.size()];
        optionNames.toArray(this.optionNames);
    }
    
    /**
     * @param optionNames The options to require.
     */
    public OptionSetRule(String... optionNames)
    {
        if (optionNames.length == 0)
        {
            throw new IllegalArgumentException("Need to specify at least one option.");
        }
        this.optionNames = optionNames;
    }

    @Override
    public void validate(Validator forParser, Validator.ValidatedInput params)
        throws RuleNotMetException
    {
        for (String name : optionNames)
        {
            // determine whether the requested thing is a flag or an option
            if (forParser.knowsFlag(name))
            {
                if (!params.isFlagSet(name))
                {
                    throw new RuleNotMetException("Required falg " + name + " not set!");
                }
            }
            else if (forParser.knowsOption(name))
            {
                if (params.getOption(name) == null)
                {
                    throw new RuleNotMetException("Required option " + name + " not set!");
                }
            }
            else if (!params.isFlagSet(name) && params.getOption(name) == null)
            {
                throw new RuleNotMetException("Required flag or option " + name + " not set!");
            }
        }
    }
    
    @Override
    public String toString()
    {
        if (optionNames.length == 1)
        {
            return optionNames[0] + " is set";
        }
        else
        {
            String str = "(These options are specified:\n";
            for (String name : optionNames)
            {
                str += name + "\n";
            }
            
            return str + ")";
        }
    }
}
