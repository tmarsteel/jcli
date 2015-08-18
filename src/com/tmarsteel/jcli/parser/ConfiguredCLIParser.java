package com.tmarsteel.jcli.parser;

import com.tmarsteel.jcli.Argument;
import com.tmarsteel.jcli.CLIParser;
import com.tmarsteel.jcli.Environment;
import com.tmarsteel.jcli.Flag;
import com.tmarsteel.jcli.Option;
import com.tmarsteel.jcli.ParseException;
import com.tmarsteel.jcli.filter.BigIntegerFilter;
import com.tmarsteel.jcli.filter.DecimalFilter;
import com.tmarsteel.jcli.filter.FileFilter;
import com.tmarsteel.jcli.filter.IntegerFilter;
import com.tmarsteel.jcli.filter.RegexFilter;
import com.tmarsteel.jcli.filter.SetFilter;
import com.tmarsteel.jcli.filter.ValueFilter;
import com.tmarsteel.jcli.rule.AndRule;
import com.tmarsteel.jcli.rule.CombinedRule;
import com.tmarsteel.jcli.rule.NotRule;
import com.tmarsteel.jcli.rule.OptionSetRule;
import com.tmarsteel.jcli.rule.OrRule;
import com.tmarsteel.jcli.rule.BaseRule;
import com.tmarsteel.jcli.rule.XorOptionsRule;
import com.tmarsteel.jcli.rule.XorRule;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * An arg parser that is constructed from a config-file
 * @author tmarsteel
 */
public class ConfiguredCLIParser extends CLIParser
{
    public ConfiguredCLIParser(Document xmlDocument, Environment env)
        throws ParseException
    {
        super(env);

        Node rootNode = xmlDocument.getFirstChild();
        if (!rootNode.getNodeName().equals("cli"))
        {
            throw new ParseException("Root node must be named cli.");
        }

        NodeList topNodes = rootNode.getChildNodes();
        for (int i = 0;i < topNodes.getLength();i++)
        {
            Node node = topNodes.item(i);
            switch (node.getNodeName())
            {
                case "flag":
                    // parse the flag
                    add(parseFlag(node));
                    break;
                case "option":
                    // parse the option
                    add(parseOption(node));
                    break;
                case "argument":
                    add(parseArgument(node));
                    break;
                case "rule":
                    add(parseRule(node));
                    break;
            }
        }
    }

    private Flag parseFlag(Node flagNode)
        throws ParseException
    {
        // resolve the primary identifier
        NamedNodeMap attrs = flagNode.getAttributes();
        Node node = attrs.getNamedItem("identifier");
        if (node == null)
        {
            throw new ParseException("Missing identifier attribute for flag");
        }
        final String primaryIdentifier = node.getTextContent();
        if (primaryIdentifier.isEmpty())
        {
            throw new ParseException("Empty identifier attribute for flag");
        }

        // resolve aliases
        ArrayList<String> names = new ArrayList<>();
        names.add(primaryIdentifier);
        getAliases(flagNode, names);

        return new Flag(names.toArray(new String[names.size()]));
    }

    private Option parseOption(Node optNode)
        throws ParseException
    {
        // resolve the primary identifier
        NamedNodeMap attrs = optNode.getAttributes();
        Node node = attrs.getNamedItem("identifier");
        if (node == null)
        {
            throw new ParseException("Missing identifier attribute for option");
        }
        final String primaryIdentifier = node.getTextContent();
        if (primaryIdentifier.isEmpty())
        {
            throw new ParseException("Empty identifier attribute for option");
        }

        List<String> names = new ArrayList<>();
        names.add(primaryIdentifier);
        ValueFilter filter = null;
        String defValue = null;

        NodeList childNodes = optNode.getChildNodes();
        for (int i = 0;i < childNodes.getLength();i++)
        {
            node = childNodes.item(i);
            switch (node.getNodeName())
            {
                case "#text": break;
                case "alias":
                    final String alias = node.getTextContent();
                    if (alias == null || alias.isEmpty())
                    {
                        throw new ParseException("Empty alias for option " + primaryIdentifier);
                    }   names.add(alias);
                    break;
                case "filter":
                    filter = parseFilter(node);
                    break;
                case "default":
                    defValue = node.getTextContent();
                    break;
                default:
                    throw new ParseException("Unknown tag " + node.getNodeName());
            }
        }

        return new Option(filter, defValue, names.toArray(new String[names.size()]));
    }

    private Argument parseArgument(Node argNode)
        throws ParseException
    {
        // resolve the primary identifier
        NamedNodeMap attrs = argNode.getAttributes();
        Node node = attrs.getNamedItem("identifier");
        if (node == null)
        {
            throw new ParseException("Missing identifier attribute for argument");
        }
        final String primaryIdentifier = node.getTextContent();
        if (primaryIdentifier.isEmpty())
        {
            throw new ParseException("Empty identifier attribute for argument");
        }

        node = attrs.getNamedItem("index");
        if (node == null)
        {
            throw new ParseException("Missing index attribute for argument");
        }
        final int index;
        try
        {
            index = Integer.parseInt(node.getTextContent());
        }
        catch (NumberFormatException ex)
        {
            throw new ParseException("Invalid index for argument (" +
                node.getTextContent() + ")", ex);
        }
        if (primaryIdentifier.isEmpty())
        {
            throw new ParseException("Empty identifier attribute for argument");
        }

        final boolean required;
        node = attrs.getNamedItem("required");
        if (node == null)
        {
            required = false;
        }
        else
        {
            if (node.getTextContent().equals("true"))
            {
                required = true;
            }
            else if (node.getTextContent().equals("false"))
            {
                required = false;
            }
            else
            {
                throw new ParseException("Illegal value for attribute required");
            }
        }

        // look for a filter
        ValueFilter filter = null;
        String defValue = null;
        NodeList children = argNode.getChildNodes();
        for (int i = 0;i < children.getLength();i++)
        {
            node = children.item(i);
            if (!node.getNodeName().equals("#text"))
            {
                switch (node.getNodeName())
                {
                    case "filter":
                        filter = parseFilter(node);
                        break;
                    case "default":
                        defValue = node.getTextContent();
                        break;
                    default:
                        throw new ParseException("Unknown tag " + node.getNodeName()
                            + " in argument (index " + index + ")");
                }
            }
        }
        Argument arg = new Argument(primaryIdentifier, index, defValue, filter);
        arg.setRequired(required);
        return arg;
    }

    private static void getAliases(Node topNode, List<String> target)
        throws ParseException
    {
        NodeList children = topNode.getChildNodes();
        for (int i = 0;i < children.getLength();i++)
        {
            Node node = children.item(i);
            if (node.getNodeName().equals("alias"))
            {
                final String alias = node.getTextContent();
                if (alias == null || alias.isEmpty())
                {
                    throw new ParseException("Empty alias");
                }
                target.add(alias);
            }
        }
    }

    private static ValueFilter parseFilter(Node filterNode)
        throws ParseException
    {
        // look for type and class attributes
        NamedNodeMap attrs = filterNode.getAttributes();
        Node node = attrs.getNamedItem("class");

        String classname;

        if (node == null)
        {
            node = attrs.getNamedItem("type");
            if (node == null)
            {
                throw new ParseException("No type and no class attribute specified for filter");
            }

            switch(node.getTextContent())
            {
                case "big-decimal":
                    classname = "com.wisper.cli.filter.BigDecimalFilter";
                    break;
                case "big-integer":
                    classname = "com.wisper.cli.filter.BigIntegerFilter";
                    break;
                case "decimal":
                    classname = "com.wisper.cli.filter.DecimalFilter";
                    break;
                case "integer":
                    classname = "com.wisper.cli.filter.IntegerFilter";
                    break;
                case "regex":
                    classname = "com.wisper.cli.filter.RegexFilter";
                    break;
                case "set":
                    classname = "com.wisper.cli.filter.SetFilter";
                    break;
                case "file":
                    classname = "com.wisper.cli.filter.FileFilter";
                    break;
                default:
                    classname = node.getTextContent();
            }
        }
        else
        {
            classname = node.getTextContent();
        }

        Class filterClass;
        try
        {
            filterClass = ConfiguredCLIParser.class.getClassLoader().loadClass(classname);
            if (!ValueFilter.class.isAssignableFrom(filterClass))
            {
                throw new ParseException("Class " + classname + " does not implement com.wisper.cli.filter.ValueFilter");
            }

            try
            {
                Constructor constr = filterClass.getConstructor(Node.class);
                return (ValueFilter) constr.newInstance(filterNode);
            }
            catch (NoSuchMethodException ex)
            {
                try
                {
                    Constructor constr = filterClass.getConstructor();
                    return (ValueFilter) constr.newInstance();
                }
                catch (NoSuchMethodException ex2)
                {
                    throw new ParseException("Filter-Class " + classname +
                        " could not be loaded: Needs to declare at least "+
                        "one of these constructors: () or (org.w3c.dom.Node)");
                }
            }
        }
        catch (ClassNotFoundException ex)
        {
            throw new ParseException("Filter-Class " + classname
                + " could not be loaded", ex);
        }
        catch (InstantiationException | IllegalAccessException
            | IllegalArgumentException | InvocationTargetException ex)
        {
            throw new RuntimeException("Falied to instantiate custom filter", ex);
        }
    }
    private BaseRule parseRule(Node ruleNode)
        throws ParseException
    {
        NamedNodeMap attrs = ruleNode.getAttributes();
        Node node = attrs.getNamedItem("type");
        final String ruleType = node == null? "class" : node.getTextContent();
        Class ruleClass = null;
        String customClassName = null;
        BaseRule finalRule = null;
        String errorMessage = null;
        // no type is specified
        if (node == null)
        {
            // look for class attribute
            node = attrs.getNamedItem("class");
            if (node == null)
            {
                throw new ParseException("No type and no class atrribute specified for rule");
            }
            else
            {
                customClassName = node.getTextContent();

                try
                {
                    ruleClass = ConfiguredCLIParser.class.getClassLoader()
                        .loadClass(customClassName);
                }
                catch (ClassNotFoundException ex)
                {
                    throw new ParseException("Rule-class " + customClassName
                        + " could not be loaded", ex);
                }
            }
        }
        // in case type is option-xor or option-set, we have to instantiate the
        // corresponding class and look for option sub-tags; for the rest we need
        // to look for rule subtags.
        else if (ruleType.equals("option-xor") || ruleType.equals("option-set"))
        {
            NodeList children = ruleNode.getChildNodes();
            List<Option> options = new ArrayList<>();
            for (int i = 0;i < children.getLength();i++)
            {
                node = children.item(i);
                if (node.getNodeName().equals("option"))
                {
                    Option o = getOptionOrFlag(node.getTextContent());
                    if (o == null)
                    {
                        throw new ParseException("Unknown option "
                            + node.getTextContent() + "; place rules at the end"
                            + " of the document.");
                    }
                    options.add(o);
                }
                else if (node.getNodeName().equals("flag"))
                {
                    Option f = getFlag(node.getTextContent());
                    if (f == null)
                    {
                        throw new ParseException("Unknown flag "
                            + node.getTextContent() + "; place rules at the end"
                            + " of the document.");
                    }
                    options.add(f);
                }
                else if (node.getNodeName().equals("error"))
                {
                    errorMessage = node.getTextContent();
                }
                else if (!node.getNodeName().equals("#text"))
                {
                    throw new ParseException(
                        "Rules only allow rule, option and flag tags");
                }
            }
            if (ruleType.equals("option-xor"))
            {
                finalRule = new XorOptionsRule(options.toArray(new Option[options.size()]));
            }
            else
            {
                finalRule = new OptionSetRule(options.toArray(new Option[options.size()]));
            }
            finalRule.setErrorMessage(errorMessage);
            return finalRule;
        }

        // look for rule subtags
        NodeList children = ruleNode.getChildNodes();
        List<BaseRule> subRules = new ArrayList<>();
        for (int i = 0;i < children.getLength();i++)
        {
            node = children.item(i);
            if (node.getNodeName().equals("rule"))
            {
                subRules.add(parseRule(node));
            }
            else if (node.getNodeName().equals("error"))
            {
                errorMessage = node.getTextContent();
            }
            else if (!node.getNodeName().equals("#text"))
            {
                throw new ParseException("Rule-tags only allow rule subtags");
            }
        }

        if (ruleType.equals("and"))
        {
            ruleClass = AndRule.class;
        }
        else if (ruleType.equals("or"))
        {
            ruleClass = OrRule.class;
        }
        else if (ruleType.equals("xor"))
        {
            ruleClass = XorRule.class;
        }
        else if (ruleType.equals("not"))
        {
            ruleClass = NotRule.class;
        }
        else if (!ruleType.equals("class"))
        {
            throw new ParseException("Unknown rule type " + ruleType);
        }

        try
        {
            if (CombinedRule.class.isAssignableFrom(ruleClass))
            { // we need at least one rule
                if (subRules.size() < 1)
                {
                    throw new ParseException("Combined rules need to have at least one child-rule");
                }
                Constructor constr = ruleClass.getConstructor(BaseRule[].class);
                finalRule = (BaseRule) constr.newInstance((Object) subRules.toArray(new BaseRule[subRules.size()])
                );
            }
            else
            { // no sub-rules allowed
                if (!subRules.isEmpty())
                {
                    throw new ParseException("No rules allowed within a non-combined rule");
                }
                Constructor defConstr;
                try
                {
                    defConstr = ruleClass.getConstructor(Node.class);
                    finalRule = (BaseRule) defConstr.newInstance(ruleNode);
                }
                catch (NoSuchMethodException ex)
                {
                    defConstr = ruleClass.getConstructor();
                    return (BaseRule) defConstr.newInstance();
                }
            }
        }
        catch (NoSuchMethodException | InstantiationException
            | IllegalAccessException | IllegalArgumentException
            | InvocationTargetException ex)
        {
            if (customClassName == null)
            {
                throw new RuntimeException(ex);
            }
            else
            {
                throw new ParseException("Rule-class " + customClassName
                    + " could not be instantiated", ex);
            }
        }
        if (finalRule == null)
        {
            throw new RuntimeException("Internal error #01");
        }
        else
        {
            finalRule.setErrorMessage(errorMessage);
            return finalRule;
        }
    }

    private Option getOption(String name)
    {
        for (Option o : options)
        {
            if (o.isIdentifiedBy(name))
            {
                return o;
            }
        }
        return null;
    }

    private Option getFlag(String name)
    {
        for (Option o : flags)
        {
            if (o.isIdentifiedBy(name))
            {
                return o;
            }
        }
        return null;
    }

    private Option getOptionOrFlag(String name)
    {
        Option o = getOption(name);
        Option f = getFlag(name);
        if (o != null && f != null)
        {
            return null;
        }
        else
        {
            return o == null? f : o;
        }
    }

    public static class XMLUtils
    {
        /**
         * Returns a string array with 2 entris:<br>
         * 0: min value<br>
         * 1: max value<br>.
         * As none of both is required both may be null.
         * @param topNode The node out of which to filter min and max tags.
         */
        public static String[] getMinMax(Node topNode)
        {
            NodeList children = topNode.getChildNodes();
            String min = null;
            String max = null;
            for (int i = 0;i < children.getLength();i++)
            {
                Node node = children.item(i);
                switch (node.getNodeName())
                {
                    case "min":
                        min = node.getTextContent();
                        break;
                    case "max":
                        max = node.getTextContent();
                        break;
                }
            }
            return new String[]{min, max};
        }

        /**
         *
         * Returns a string array with 3 entris:<br>
         * 0: min value<br>
         * 1: max value<br>
         * 2: radix<br>.
         * As none of these are requred every entry may be null..
         * @param topNode The node out of which to filter min, max and radix tags
         * @return
         */
        public static String[] getMinMaxRadix(Node topNode)
        {
            NodeList children = topNode.getChildNodes();
            String min = null;
            String max = null;
            String radix = null;
            for (int i = 0;i < children.getLength();i++)
            {
                Node node = children.item(i);
                switch (node.getNodeName())
                {
                    case "min":
                        min = node.getTextContent();
                        break;
                    case "max":
                        max = node.getTextContent();
                        break;
                    case "radix":
                        radix = node.getTextContent();
                        break;
                }
            }
            return new String[]{min, max, radix};
        }

        public static Double asDouble(String numeric)
            throws ParseException
        {
            if (numeric == null)
            {
                return null;
            }
            try
            {
                return Double.parseDouble(numeric);
            }
            catch (NumberFormatException ex)
            {
                throw new ParseException("Invalid decimal number: " + numeric, ex);
            }
        }

        public static Long asLong(String numeric)
            throws ParseException
        {
            if (numeric == null)
            {
                return null;
            }
            try
            {
                return Long.parseLong(numeric);
            }
            catch (NumberFormatException ex)
            {
                throw new ParseException("Invalid integer number: " + numeric, ex);
            }
        }
    }
}