package com.wisper.cli;

import com.wisper.cli.filter.BigDecimalFilter;
import com.wisper.cli.filter.BigIntegerFilter;
import com.wisper.cli.filter.DecimalFilter;
import com.wisper.cli.filter.FileFilter;
import com.wisper.cli.filter.IntegerFilter;
import com.wisper.cli.filter.RegexFilter;
import com.wisper.cli.filter.SetFilter;
import com.wisper.cli.filter.ValueFilter;
import com.wisper.cli.rule.AndRule;
import com.wisper.cli.rule.CombinedRule;
import com.wisper.cli.rule.NotRule;
import com.wisper.cli.rule.OptionSetRule;
import com.wisper.cli.rule.OrRule;
import com.wisper.cli.rule.Rule;
import com.wisper.cli.rule.XorOptionsRule;
import com.wisper.cli.rule.XorRule;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * An arg parser that is constructed from a config-file
 * @author Tobias Marstaller
 */
class ConfiguredCLIParser extends CLIParser
{
    public ConfiguredCLIParser(Document xmlDocument)
        throws ParseException
    {
        this(xmlDocument, File.separatorChar == '/'? Environment.UNIX : Environment.WINDOWS);
    }
    public ConfiguredCLIParser(Document xmlDocument, Environment env)
        throws ParseException
    {
        super(env);
        
        Node rootNode = xmlDocument.getFirstChild();
        if (!rootNode.getNodeName().equals("cli"))
        {
            throw new ParseException("Root node must be namend cli.");
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
        
        ArrayList<String> names = new ArrayList<>();
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
    
    private static void getAliases(Node topNode, ArrayList<String> target)
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
    
    private static String[] getMinMax(Node topNode)
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
    
    private static String[] getMinMaxRadix(Node topNode)
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
    
    private static Double asDouble(String numeric)
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
    
    private static Long asLong(String numeric)
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
    
    private static ValueFilter parseFilter(Node filterNode)
        throws ParseException
    {
        // look for type and class attributes
        NamedNodeMap attrs = filterNode.getAttributes();
        Node node = attrs.getNamedItem("type");
        if (node == null)
        {
            node = attrs.getNamedItem("class");
            if (node == null)
            {
                throw new ParseException("No type and no class attribute specified for filter");
            }
            // load the class
            final String classname = node.getTextContent();
            Class filterClass;
            try
            {
                filterClass = ConfiguredCLIParser.class.getClassLoader().loadClass(classname);
                if (!ValueFilter.class.isAssignableFrom(filterClass))
                {
                    throw new ParseException("Class " + classname + " does not implement com.wisper.cli.filter.ValueFilter");
                }
                Constructor constr = filterClass.getConstructor();
                return (ValueFilter) constr.newInstance();
            }
            catch (ClassNotFoundException | NoSuchMethodException ex)
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
        
        // varaibles used in multibel cases
        String[] minMax;
        String[] minMaxRadix;
        int radix;
        NodeList children;
        Node cNode;
        
        switch (node.getTextContent())
        {
            // for numeric filters, look for the min and max tags
            case "big-decimal":
                minMax = getMinMax(filterNode);
                return new BigDecimalFilter(
                    minMax[0] == null? null : new BigDecimal(minMax[0]),
                    minMax[1] == null? null : new BigDecimal(minMax[1])
                );
            case "big-integer":
                minMaxRadix = getMinMaxRadix(filterNode);
                
                try
                {
                    if (minMaxRadix[2] == null)
                    {
                        radix = 10;
                    }
                    else
                    {
                        radix = Integer.parseInt(minMaxRadix[2]);
                    }
                }
                catch (NumberFormatException ex)
                {
                    throw new ParseException("Invalid radix: " + minMaxRadix[2]);
                }
                
                return new BigIntegerFilter(
                    minMaxRadix[0] == null? null : new BigInteger(minMaxRadix[0]),
                    minMaxRadix[1] == null? null : new BigInteger(minMaxRadix[1]),
                    radix
                );
            case "decimal":
                minMax = getMinMax(filterNode);
                return new DecimalFilter(asDouble(minMax[0]), asDouble(minMax[1]));
            case "integer":
                minMaxRadix = getMinMaxRadix(filterNode);
                try
                {
                    if (minMaxRadix[2] == null)
                    {
                        radix = 10;
                    }
                    else
                    {
                        radix = Integer.parseInt(minMaxRadix[2]);
                    }
                }
                catch (NumberFormatException ex)
                {
                    throw new ParseException("Invalid radix: " + minMaxRadix[2]);
                }
                return new IntegerFilter(
                    minMaxRadix[0] == null? Long.MIN_VALUE : asLong(minMaxRadix[0]),
                    minMaxRadix[1] == null? Long.MAX_VALUE : asLong(minMaxRadix[1]),
                    radix
                );
            case "regex":
                // look for the return-group attribute
                node = attrs.getNamedItem("returnGroup");
                final int returnGroup;
                if (node == null)
                {
                    returnGroup = 0;
                }
                else
                {
                    try
                    {
                        returnGroup = Integer.parseInt(node.getTextContent());
                    }
                    catch (NumberFormatException ex)
                    {
                        throw new ParseException("Value of returnGroup attribute needs to be an integer");
                    }
                }
                // look for the regex tag, has to be the only one
                Node regexNode = filterNode.getFirstChild();
                while (regexNode.getNodeName().equals("#text"))
                    regexNode = regexNode.getNextSibling();
                if (regexNode.getNodeName().equals("regex"))
                {
                    RegexFilter filter = new RegexFilter(regexNode.getTextContent());
                    filter.setReturnGroup(returnGroup);
                    return filter;
                }
                else
                {
                    throw new ParseException("regex-filters only allow regex tags");
                }
            case "set":
                // look for caseSensitive attribute
                cNode = attrs.getNamedItem("caseSensitive");
                final boolean caseSensitive;
                if (cNode == null)
                {
                    caseSensitive = false;
                }
                else
                {
                    if (cNode.getTextContent().equals("true"))
                    {
                        caseSensitive = true;
                    }
                    else if (cNode.getTextContent().equals("false"))
                    {
                        caseSensitive = false;
                    }
                    else
                    {
                        throw new ParseException("Invalid value for caseSensitive attribute on set-filter");
                    }
                }
                
                // look for all possible values
                ArrayList<String> values = new ArrayList<>();
                children = filterNode.getChildNodes();
                for (int i = 0;i < children.getLength();i++)
                {
                    cNode = children.item(i);
                    if (cNode.getNodeName().equals("value"))
                    {
                        values.add(cNode.getTextContent());
                    }
                    else if (!cNode.getNodeName().equals("#text"))
                    {
                        throw new ParseException("set-filters only allow value tags");
                    }
                }
                return new SetFilter(caseSensitive, values);
            case "file":
                String extension = null;
                FileFilter.TYPE type = FileFilter.TYPE.IRRELEVANT;
                FileFilter.PERMISSION perms = FileFilter.PERMISSION.IRRELEVANT;
                FileFilter.EXISTANCE exist = FileFilter.EXISTANCE.IRRELEVANT;
                
                children = filterNode.getChildNodes();
                for (int i = 0;i < children.getLength();i++)
                {
                    cNode = children.item(i);
                    
                    if (cNode.getNodeName().equals("#text"))
                    {
                        continue;
                    }
                    
                    switch (cNode.getNodeName())
                    {
                        case "extension":
                            extension = cNode.getTextContent();
                            break;
                        case "type":
                            try
                            {
                                type = FileFilter.TYPE.valueOf(cNode.getTextContent());
                            }
                            catch (IllegalArgumentException ex)
                            {
                                throw new ParseException("Illegal value for type of file-filter");
                            }
                            break;
                        case "permissions":
                            try
                            {
                                perms = FileFilter.PERMISSION.valueOf(cNode.getTextContent());
                            }
                            catch (IllegalArgumentException ex)
                            {
                                throw new ParseException("Illegal value for permissions of file-filter");
                            }
                            break;
                        case "existance":
                            try
                            {
                                exist = FileFilter.EXISTANCE.valueOf(cNode.getTextContent());
                            }
                            catch (IllegalArgumentException ex)
                            {
                                throw new ParseException("Illegal value for existance of file-filter");
                            }
                            break;
                        default:
                            throw new ParseException("Unknown tag " + cNode.getNodeName()
                                    + " in file-filter");
                    }
                }
                FileFilter f = new FileFilter();
                f.setExtension(extension);
                f.setFileType(type);
                f.setExistanceStatus(exist);
                f.setPermissions(perms);
                return f;
            default:
                throw new ParseException("Unknown filter-type " + node.getTextContent());
        }
    }
    private Rule parseRule(Node ruleNode)
        throws ParseException
    {
        NamedNodeMap attrs = ruleNode.getAttributes();
        Node node = attrs.getNamedItem("type");
        final String ruleType = node == null? "class" : node.getTextContent();
        Class ruleClass = null;
        String customClassName = null;
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
            ArrayList<Option> options = new ArrayList<>();
            for (int i = 0;i < children.getLength();i++)
            {
                node = children.item(i);
                if (node.getNodeName().equals("option"))
                {
                    Option o = getOption(node.getTextContent());
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
                else if (!node.getNodeName().equals("#text"))
                {
                    throw new ParseException(
                        "Rules only allow rule, option and flag tags");
                }
            }
            if (ruleType.equals("option-xor"))
            {
                return new XorOptionsRule(options.toArray(new Option[options.size()]));
            }
            else
            {
                return new OptionSetRule(options.toArray(new Option[options.size()]));
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

        // look for rule subtags
        NodeList children = ruleNode.getChildNodes();
        ArrayList<Rule> subRules = new ArrayList<>();
        for (int i = 0;i < children.getLength();i++)
        {
            node = children.item(i);
            if (node.getNodeName().equals("rule"))
            {
                subRules.add(parseRule(node));
            }
            else if (!node.getNodeName().equals("#text"))
            {
                throw new ParseException("Rule-tags only allow rule subtags");
            }
        }

        try
        {
            if (CombinedRule.class.isAssignableFrom(ruleClass))
            { // we need at least one rule
                if (subRules.size() < 1)
                {
                    throw new ParseException("Combined rules need to have at least one child-rule");
                }
                Constructor constr = ruleClass.getConstructor(Rule[].class);
                return (Rule) constr.newInstance(
                    (Object) subRules.toArray(new Rule[subRules.size()])
                );
            }
            else
            { // no sub-rules allowed
                if (!subRules.isEmpty())
                {
                    throw new ParseException("No rules allowed within a non-combined rule");
                }
                Constructor defConstr = ruleClass.getConstructor();
                return (Rule) defConstr.newInstance();
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
}