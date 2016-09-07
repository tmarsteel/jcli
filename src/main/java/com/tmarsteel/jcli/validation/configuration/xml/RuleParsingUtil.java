package com.tmarsteel.jcli.validation.configuration.xml;

import com.tmarsteel.jcli.ParseException;
import com.tmarsteel.jcli.filter.*;
import com.tmarsteel.jcli.rule.OptionSetRule;
import com.tmarsteel.jcli.rule.Rule;
import com.tmarsteel.jcli.validation.MisconfigurationException;
import com.tmarsteel.jcli.validation.ValidationException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Utility methods used to parse the XML configurations of rules provided by the library
 * @see com.tmarsteel.jcli.filter
 */
public class RuleParsingUtil
{
    public static OptionSetRule parseOptionSetRule(Node ruleNode)
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
                throw new MisconfigurationException("The error tag is not supported by the option-set rule.");
            }
            else if (!node.getNodeName().equals("#text"))
            {
                throw new MisconfigurationException("Unknown subtag " + node.getNodeName() + " of option-set rule");
            }
        }

        return new OptionSetRule(optionNames.toArray(new String[optionNames.size()]));
    }
}
