package com.tmarsteel.jcli.validation.configuration.xml;

import com.tmarsteel.jcli.ParseException;
import com.tmarsteel.jcli.filter.*;
import com.tmarsteel.jcli.rule.CombinedRule;
import com.tmarsteel.jcli.rule.OptionSetRule;
import com.tmarsteel.jcli.rule.Rule;
import com.tmarsteel.jcli.validation.MisconfigurationException;
import com.tmarsteel.jcli.validation.ValidationException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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
    public static OptionSetRule parseOptionSetRule(XMLValidatorConfigurator context, Node ruleNode, RuleParser<Rule> subParser)
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

    /**
     * Returns a {@link RuleParser} parsing the given nodes to combined rules of the given type.
     * @param cls The type of the resulting rule. Must have a {@code (Rule[]) }constructor
     * @return A parser parsing the given nodes to combined rules.
     */
    public static <T extends CombinedRule> RuleParser<CombinedRule> combinedRuleParser(Class<T> cls)
        throws MisconfigurationException
    {
        try {
            Constructor<T> ctor =  cls.getConstructor(Rule[].class);
            return (context, ruleNode, subParser) -> {
                NodeList children = ruleNode.getChildNodes();
                List<Rule> rules = new ArrayList<>();
                String errorMessage = null;

                for (int i = 0;i < children.getLength();i++) {
                    Node node = children.item(i);
                    if (node.getNodeName().equals("rule"))
                    {
                        rules.add(subParser.parse(context, node, subParser));
                    }
                    else if (node.getNodeName().equals("error")) {
                        errorMessage = node.getTextContent();
                    }
                    else if (!node.getNodeName().equals("#text")) {
                        throw new MisconfigurationException("Unknown tag " + node.getNodeName() + " inside combined rule");
                    }
                }

                CombinedRule rule;
                try {
                    rule = ctor.newInstance(rules.toArray());
                }
                catch (InvocationTargetException ex) {
                    Throwable actualEx = ex.getTargetException();
                    if (actualEx instanceof MisconfigurationException) {
                        throw (MisconfigurationException) actualEx;
                    } else if (actualEx instanceof ParseException) {
                        throw (ParseException) actualEx;
                    } else {
                        throw new RuntimeException("Failed to instantiate " + cls.getName(), actualEx);
                    }
                }
                catch (Exception ex) {
                    throw new RuntimeException("Failed to instantiate " + cls.getName(), ex);
                }

                rule.setErrorMessage(errorMessage);
                return rule;
            };
        }
        catch (NoSuchMethodException ex) {
            throw new IllegalArgumentException("The given class does not have a (com.tmarsteel.jcli.rule.Rule[]) constructor", ex);
        }
    }
}
