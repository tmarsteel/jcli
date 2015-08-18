package com.tmarsteel.jcli.parser;

import com.tmarsteel.jcli.Argument;
import com.tmarsteel.jcli.CLIParser;
import com.tmarsteel.jcli.Environment;
import com.tmarsteel.jcli.Flag;
import com.tmarsteel.jcli.Option;
import com.tmarsteel.jcli.ParseException;
import com.tmarsteel.jcli.filter.ValueFilter;
import com.tmarsteel.jcli.rule.AndRule;
import com.tmarsteel.jcli.rule.CombinedRule;
import com.tmarsteel.jcli.rule.NotRule;
import com.tmarsteel.jcli.rule.OptionSetRule;
import com.tmarsteel.jcli.rule.OrRule;
import com.tmarsteel.jcli.rule.BaseRule;
import com.tmarsteel.jcli.rule.Rule;
import com.tmarsteel.jcli.rule.XorOptionsRule;
import com.tmarsteel.jcli.rule.XorRule;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.naming.OperationNotSupportedException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Builder/Factory for Parsers, based on XML configuration.
 * @author tmarsteel
 */
public class XMLParserBuilder
{    
    private Document baseDocument;
    private Environment environment;
    
    private final Map<String,Class> filterTypeClass = new HashMap<>();
    private final Map<String,Class> ruleTypeClass   = new HashMap<>();
    
    /**
     * Parses the input from <code>configInputStream</code> as XML and creates a
     * new ParserBuilder based on the resulting {@link Document}. This method does
     * not perform any operations on the actual XML content.
     * @param configInputStream A stream to read xml from.
     * @return A ParserBuilder prepared with the {@link Document} resulting form
     * <code>configInputStream</code>
     * @throws SAXException If an XML Syntax-Error occurs.
     * @throws IOException If an I/O-Error occurs.
     */
    public static XMLParserBuilder newParserBuilder(InputStream configInputStream)
        throws SAXException, IOException
    {
        return newParserBuilder(configInputStream, null);
    }
    
    /**
     * Parses the input from <code>configInputStream</code> as XML and creates a
     * new ParserBuilder based on the resulting {@link Document}. This method does
     * not perform any operations on the actual XML content.
     * @param configInputStream A stream to read xml from.
     * @param env The environment configuration to pass on to the created parsers.
     * @return A ParserBuilder prepared with the {@link Document} resulting form
     * <code>configInputStream</code>
     * @throws SAXException If an XML Syntax-Error occurs.
     * @throws IOException If an I/O-Error occurs.
     */
    public static XMLParserBuilder newParserBuilder(InputStream configInputStream,
        Environment env)
        throws SAXException, IOException
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(false);
        dbf.setIgnoringComments(true);
        dbf.setIgnoringElementContentWhitespace(true);
        
        try
        {
            DocumentBuilder builder = dbf.newDocumentBuilder();
            return new XMLParserBuilder(builder.parse(configInputStream), env);
        }
        catch (ParserConfigurationException ex)
        {
            throw new RuntimeException(ex);
        }
    }
    
    /**
     * Parses the file <code>configFile</code> as XML and creates a  new 
     * ParserBuilder based on the resulting {@link Document}. This method does
     * not perform any operations on the actual XML content.
     * @param configFile The file to read xml from.
     * @return A ParserBuilder prepared with the {@link Document} resulting form
     * <code>configFile</code>
     * @throws SAXException If an XML Syntax-Error occurs.
     * @throws IOException If an I/O-Error occurs.
     */
    public static XMLParserBuilder newParserBuilder(File configFile)
        throws IOException, SAXException
    {
        return newParserBuilder(configFile, null);
    }
    
    /**
     * Parses the file <code>configFile</code> as XML and creates a  new 
     * ParserBuilder based on the resulting {@link Document}. This method does
     * not perform any operations on the actual XML content.
     * @param configFile The file to read xml from.
     * @param env The environment configuration to pass on to the created parsers.
     * @return A ParserBuilder prepared with the {@link Document} resulting form
     * <code>configFile</code>
     * @throws SAXException If an XML Syntax-Error occurs.
     * @throws IOException If an I/O-Error occurs.
     */
    public static XMLParserBuilder newParserBuilder(File configFile, Environment env)
        throws IOException, SAXException
    {
        try (FileInputStream fIn = new FileInputStream(configFile))
        {
            return newParserBuilder(fIn, env);
        }
    }
    
    /**
     * Creates a new ParserBuilder based on the given {@link Document}. This
     * method does not perform any operations on the actual XML content.
     * @param xmlDocument The XML document to treat as configuration directives.
     * @return A ParserBuilder prepared with the given {@link Document}
     */
    public static XMLParserBuilder newParserBuilder(Document xmlDocument)
    {
        return new XMLParserBuilder(xmlDocument, File.separatorChar == '/'? Environment.UNIX : Environment.DOS);
    }
    
    /**
     * Creates a new ParserBuilder based on the given {@link Document}.
     * @param xmlDocument The XML document to treat as configuration directives.
     * @param env The environment configuration to pass on to the created parsers.
     */
    public XMLParserBuilder(Document xmlDocument, Environment env)
    {
        this.baseDocument = xmlDocument;
        this.environment = env;
        
        // default filter and rule types
        filterTypeClass.put("big-decimal", com.tmarsteel.jcli.filter.BigDecimalFilter.class);
        filterTypeClass.put("big-integer", com.tmarsteel.jcli.filter.BigIntegerFilter.class);
        filterTypeClass.put("decimal",     com.tmarsteel.jcli.filter.DecimalFilter.class);
        filterTypeClass.put("integer",     com.tmarsteel.jcli.filter.IntegerFilter.class);
        filterTypeClass.put("regex",       com.tmarsteel.jcli.filter.RegexFilter.class);
        filterTypeClass.put("set",         com.tmarsteel.jcli.filter.SetFilter.class);
        filterTypeClass.put("file",        com.tmarsteel.jcli.filter.FileFilter.class);
        
        ruleTypeClass.put("and",        com.tmarsteel.jcli.rule.AndRule.class);
        ruleTypeClass.put("or",         com.tmarsteel.jcli.rule.OrRule.class);
        ruleTypeClass.put("xor",        com.tmarsteel.jcli.rule.XorRule.class);
        ruleTypeClass.put("not",        com.tmarsteel.jcli.rule.NotRule.class);
        ruleTypeClass.put("option-xor", com.tmarsteel.jcli.rule.XorOptionsRule.class);
        ruleTypeClass.put("option-set", com.tmarsteel.jcli.rule.OptionSetRule.class);
    }
    
    /**
     * Registers the given filter type with the given class. If this builder
     * finds a &lt;filter&gt; of type <code>type</code> it will attempt to
     * instantiate a new instance of <code>cls</code>.
     * @param type The type string to register.
     * @param cls The class to register; must implement {@link ValueFilter}.
     * @throws IllegalArgumentException If <code>cls</code> does not implement {@link ValueFilter}
     * @throws NullPointerException If <code>type</code> is null.
     */
    public void setFilterType(String type, Class cls)
    {
        if (type == null)
        {
            throw new NullPointerException("type must not be null");
        }
        
        if (!cls.isAssignableFrom(ValueFilter.class))
        {
            throw new IllegalArgumentException("The given class must implement com.tmarsteel.jcli.filter.ValueFilter");
        }
        
        this.filterTypeClass.put(type, cls);
    }
    
    /**
     * Registers the given rule type with the given class. If this builder
     * finds a &lt;rule&gt; of type <code>type</code> it will attempt to
     * instantiate a new instance of <code>cls</code>.
     * <code>cls</code> must implement {@link Rule} and have one of these
     * constructor signatures: <code>(org.w3c.dom.Node)</code>,
     * <code>(com.tmarsteel.jcli.Rule[])</code>, <code>()</code>.
     * The signatures will be attempted in this order.
     * @param type The type string to register.
     * @param cls The class to register; must implement {@link Rule} and have
     * one of these constructor signatures: <code>()</code>, <code>(org.w3c.dom.Node)</code>
     * @throws IllegalArgumentException If <code>cls</code> does not implement {@link ValueFilter}
     * @throws NullPointerException If <code>type</code> is null.
     */
    public void setRuleType(String type, Class cls)
    {
        if (type == null)
        {
            throw new NullPointerException("type must not be null");
        }
        
        if (!cls.isAssignableFrom(Rule.class))
        {
            throw new IllegalArgumentException("The given class must implement com.tmarsteel.jcli.filter.Rule");
        }
        
        this.ruleTypeClass.put(type, cls);
    }
    
    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    public CLIParser newInstance()
    {
        CLIParser p = new CLIParser();
        this.configure(p);
        return p;
    }
    
    /**
     * {@inheritDoc}
     */
    public void configure(CLIParser p)
        throws MisconfigurationException
    {
        Node rootNode = baseDocument.getFirstChild();
        
        if (!rootNode.getNodeName().equals("cli"))
        {
            throw new MisconfigurationException("Root node must be named cli.");
        }

        NodeList topNodes = rootNode.getChildNodes();
        for (int i = 0;i < topNodes.getLength();i++)
        {
            Node node = topNodes.item(i);
            switch (node.getNodeName())
            {
                case "flag":
                    p.add(parseFlag(node));
                    break;
                case "option":
                    p.add(parseOption(node));
                    break;
                case "argument":
                    p.add(parseArgument(node));
                    break;
                case "rule":
                    p.add(parseRule(node));
                    break;
            }
        }
    }
    
    private Flag parseFlag(Node flagNode)
        throws MisconfigurationException
    {
        // resolve the primary identifier
        NamedNodeMap attrs = flagNode.getAttributes();
        Node node = attrs.getNamedItem("identifier");
        
        if (node == null)
        {
            throw new MisconfigurationException("Missing identifier attribute for flag");
        }
        final String primaryIdentifier = node.getTextContent();
        if (primaryIdentifier.isEmpty())
        {
            throw new MisconfigurationException("Empty identifier attribute for flag");
        }

        // resolve aliases
        ArrayList<String> names = new ArrayList<>();
        names.add(primaryIdentifier);
        getAliases(flagNode, names);

        return new Flag(names.toArray(new String[names.size()]));
    }

    private Option parseOption(Node optNode)
        throws MisconfigurationException
    {
        // resolve the primary identifier
        NamedNodeMap attrs = optNode.getAttributes();
        Node node = attrs.getNamedItem("identifier");
        if (node == null)
        {
            throw new MisconfigurationException("Missing identifier attribute for option");
        }
        final String primaryIdentifier = node.getTextContent();
        if (primaryIdentifier.isEmpty())
        {
            throw new MisconfigurationException("Empty identifier attribute for option");
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
                        throw new MisconfigurationException("Empty alias for option " + primaryIdentifier);
                    }   names.add(alias);
                    break;
                case "filter":
                    filter = parseFilter(node);
                    break;
                case "default":
                    defValue = node.getTextContent();
                    break;
                default:
                    throw new MisconfigurationException("Unknown tag " + node.getNodeName());
            }
        }

        return new Option(filter, defValue, names.toArray(new String[names.size()]));
    }

    private Argument parseArgument(Node argNode)
        throws MisconfigurationException
    {
        // resolve the primary identifier
        NamedNodeMap attrs = argNode.getAttributes();
        Node node = attrs.getNamedItem("identifier");
        if (node == null)
        {
            throw new MisconfigurationException("Missing identifier attribute for argument");
        }
        final String primaryIdentifier = node.getTextContent();
        if (primaryIdentifier.isEmpty())
        {
            throw new MisconfigurationException("Empty identifier attribute for argument");
        }

        node = attrs.getNamedItem("index");
        if (node == null)
        {
            throw new MisconfigurationException("Missing index attribute for argument");
        }
        final int index;
        try
        {
            index = Integer.parseInt(node.getTextContent());
        }
        catch (NumberFormatException ex)
        {
            throw new MisconfigurationException("Invalid index for argument (" +
                node.getTextContent() + ")", ex);
        }
        if (primaryIdentifier.isEmpty())
        {
            throw new MisconfigurationException("Empty identifier attribute for argument");
        }

        final boolean required;
        node = attrs.getNamedItem("required");
        if (node == null)
        {
            required = false;
        }
        else
        {
            switch (node.getTextContent())
            {
                case "true":
                    required = true;
                    break;
                case "false":
                    required = false;
                    break;
                default:
                    throw new MisconfigurationException("Illegal value for attribute required");
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
                        throw new MisconfigurationException("Unknown tag " + node.getNodeName()
                            + " in argument (index " + index + ")");
                }
            }
        }
        Argument arg = new Argument(primaryIdentifier, index, defValue, filter);
        arg.setRequired(required);
        return arg;
    }

    private static void getAliases(Node topNode, List<String> target)
        throws MisconfigurationException
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
                    throw new MisconfigurationException("Empty alias");
                }
                target.add(alias);
            }
        }
    }

    private ValueFilter parseFilter(Node filterNode)
        throws MisconfigurationException
    {
        // look for type and class attributes
        NamedNodeMap attrs = filterNode.getAttributes();
        Node node = attrs.getNamedItem("class");

        Class filterClass;

        if (node == null)
        {
            // class attribute not set
            node = attrs.getNamedItem("type");
            if (node == null)
            {
                throw new MisconfigurationException("No type and no class attribute specified for filter");
            }

            filterClass = this.filterTypeClass.get(node.getTextContent());
            
            if (filterClass == null)
            {
                throw new MisconfigurationException("Unknown filter type " + node.getTextContent());
            }
            
            // classes in this.filterTypeClass are checked to implement Filter
            // by setFilterType
        }
        else
        {
            String classname = node.getTextContent();
            
            try
            {
                filterClass = ConfiguredCLIParser.class.getClassLoader().loadClass(classname);
                
                if (!ValueFilter.class.isAssignableFrom(filterClass))
                {
                    throw new MisconfigurationException("Class " + classname + " does not implement com.wisper.cli.filter.ValueFilter");
                }
            }
            catch (ClassNotFoundException ex)
            {
                throw new MisconfigurationException("Filter-Class " + classname
                    + " could not be loaded", ex);
            }
        }

        try
        {
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
                    throw new MisconfigurationException("Filter-Class " + filterClass.getCanonicalName() +
                        " could not be instantiated: Needs to declare at least "+
                        "one of these constructors: () or (org.w3c.dom.Node)");
                }
            }
        }
        catch (InstantiationException | IllegalAccessException
            | IllegalArgumentException | InvocationTargetException ex)
        {
            throw new RuntimeException("Falied to instantiate filter of class "
                + filterClass.getCanonicalName(), ex);
        }
    }
    
    private Rule parseRule(Node ruleNode)
        throws MisconfigurationException
    {
        NodeList ruleNodeChildren = ruleNode.getChildNodes();
        NamedNodeMap attrs = ruleNode.getAttributes();
        Node node = attrs.getNamedItem("class");
        
        Class ruleClass;
        
        if (node == null)
        {
            node = attrs.getNamedItem("type");
            
            if (node == null)
            {
                throw new MisconfigurationException("Invalid rule-tag: missing class or type attribute");
            }
            
            ruleClass = ruleTypeClass.get(node.getTextContent());
            
            if (ruleClass == null)
            {
                throw new MisconfigurationException("Rule-Type " + node.getTextContent() +
                    " unknown/not defined");
            }
            
            // classes in ruleTypeClass are checked to implement Rule by
            // setRuleType
        }
        else
        {
            try
            {
                ruleClass = getClass().getClassLoader().loadClass(node.getTextContent());
                
                if (!ruleClass.isAssignableFrom(Rule.class))
                {
                    throw new MisconfigurationException("Class " + ruleClass.getCanonicalName() +
                        " does not implement com.tmarsteel.jcli.rule.Rule");
                }
            }
            catch (ClassNotFoundException ex)
            {
                throw new MisconfigurationException("Rule-Class " +
                    node.getTextContent() + " could not be loaded", ex);
            }
        }
        
        Rule ruleInstance = null;
        String errorMessage = null;
        
        // choose the appropriate constructor and create an instance
        try
        {
            try
            {
                ruleInstance = (Rule) ruleClass.getConstructor(Node.class).newInstance(ruleNode);
            }
            catch (NoSuchMethodException ex)
            {
                try
                {
                    Constructor constr = ruleClass.getConstructor(Rule[].class);

                    // look for rule subtags
                    
                    List<Rule> subRules = new ArrayList<>();
                    for (int i = 0;i < ruleNodeChildren.getLength();i++)
                    {
                        node = ruleNodeChildren.item(i);
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
                            throw new MisconfigurationException("Error while parsing rule of class "
                                + ruleClass.getCanonicalName() + ": constructor (Rule[]) allows only for <rule> and <error> subtags.");
                        }
                    }

                    ruleInstance = (Rule) constr.newInstance((Object) subRules.toArray(new Rule[subRules.size()]));
                }
                catch (NoSuchMethodException ex2)
                {
                    try
                    {
                        ruleInstance = (Rule) ruleClass.getConstructor().newInstance();
                    }
                    catch (NoSuchMethodException ex3)
                    {
                        throw new RuntimeException("Rule-Class " + ruleClass.getCanonicalName()
                            + " does not implement one of these constructors: (org.w3c.dom.Node), (com.tmarsteel.jcli.rule.Rule[]), ()");
                    }
                }
            }
        }
        catch (InstantiationException | IllegalAccessException | InvocationTargetException ex)
        {
            throw new RuntimeException("Failed to instantiate rule of class " 
                + ruleClass.getCanonicalName(), ex);
        }
        
        // look for a error tag
        if (errorMessage != null)
        {
            for (int i = 0;i < ruleNodeChildren.getLength();i++)
            {
                node = ruleNodeChildren.item(i);
                if (node.getNodeName().equals("error"))
                {
                    errorMessage = node.getTextContent();
                }
            }
        }
        
        // try to set the error message
        if (errorMessage != null)
        {
            try
            {
                ruleInstance.setErrorMessage(errorMessage);
            }
            catch (OperationNotSupportedException ex)
            {
                throw new MisconfigurationException("Rule of class "
                    + ruleClass.getCanonicalName() + " does not support custom error messages", ex);
            }
        }
        
        return ruleInstance;
    }
}
