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
package com.tmarsteel.jcli.validation.configuration;

import com.tmarsteel.jcli.Argument;
import com.tmarsteel.jcli.Environment;
import com.tmarsteel.jcli.Flag;
import com.tmarsteel.jcli.Option;
import com.tmarsteel.jcli.filter.Filter;
import com.tmarsteel.jcli.rule.Rule;
import com.tmarsteel.jcli.validation.MisconfigurationException;
import com.tmarsteel.jcli.validation.ValidationException;
import com.tmarsteel.jcli.validation.Validator;
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
public class XMLValidatorConfigurator implements ValidatorConfigurator
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
    public static XMLValidatorConfigurator getInstance(InputStream configInputStream)
        throws SAXException, IOException
    {
        return XMLValidatorConfigurator.getInstance(configInputStream, null);
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
    public static XMLValidatorConfigurator getInstance(InputStream configInputStream,
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
            return new XMLValidatorConfigurator(builder.parse(configInputStream), env);
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
    public static XMLValidatorConfigurator getInstance(File configFile)
        throws IOException, SAXException
    {
        return XMLValidatorConfigurator.getInstance(configFile, null);
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
    public static XMLValidatorConfigurator getInstance(File configFile, Environment env)
        throws IOException, SAXException
    {
        try (FileInputStream fIn = new FileInputStream(configFile))
        {
            return XMLValidatorConfigurator.getInstance(fIn, env);
        }
    }
    
    /**
     * Creates a new ParserBuilder based on the given {@link Document}. This
     * method does not perform any operations on the actual XML content.
     * @param xmlDocument The XML document to treat as configuration directives.
     * @return A ParserBuilder prepared with the given {@link Document}
     */
    public static XMLValidatorConfigurator getInstance(Document xmlDocument)
    {
        return new XMLValidatorConfigurator(xmlDocument, File.separatorChar == '/'? Environment.UNIX : Environment.DOS);
    }
    
    /**
     * Creates a new ParserBuilder based on the given {@link Document}.
     * @param xmlDocument The XML document to treat as configuration directives.
     * @param env The environment configuration to pass on to the created parsers.
     */
    public XMLValidatorConfigurator(Document xmlDocument, Environment env)
    {
        if (env == null)
        {
            env = Environment.getEnvironment();
        }
        
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
        filterTypeClass.put("pattern",     com.tmarsteel.jcli.filter.MetaRegexFilter.class);
        
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
     * @param cls The class to register; must implement {@link Filter}.
     * @throws IllegalArgumentException If <code>cls</code> does not implement {@link Filter}
     * @throws NullPointerException If <code>type</code> is null.
     */
    public void setFilterType(String type, Class cls)
    {
        if (type == null)
        {
            throw new NullPointerException("type must not be null");
        }
        
        if (!Filter.class.isAssignableFrom(cls))
        {
            throw new IllegalArgumentException("The given class must implement com.tmarsteel.jcli.filter.Filter");
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
     * @throws IllegalArgumentException If <code>cls</code> does not implement {@link Filter}
     * @throws NullPointerException If <code>type</code> is null.
     */
    public void setRuleType(String type, Class cls)
    {
        if (type == null)
        {
            throw new NullPointerException("type must not be null");
        }
        
        if (!Rule.class.isAssignableFrom(cls))
        {
            throw new IllegalArgumentException("The given class must implement com.tmarsteel.jcli.filter.Rule");
        }
        
        this.ruleTypeClass.put(type, cls);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void configure(Validator p)
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
        
        p.setEnvironment(environment);
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
        Filter filter = null;
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

        Option o = new Option(filter, defValue, names.toArray(new String[names.size()]));
        o.setRequired(attrs.getNamedItem("required") != null);
        o.setAllowsMultipleValues(attrs.getNamedItem("collection") != null);
        
        return o;
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
        Filter filter = null;
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

    private Filter parseFilter(Node filterNode)
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
                filterClass = getClass().getClassLoader().loadClass(classname);
                
                if (!Filter.class.isAssignableFrom(filterClass))
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
                return (Filter) constr.newInstance(filterNode);
            }
            catch (NoSuchMethodException ex)
            {
                try
                {
                    Constructor constr = filterClass.getConstructor();
                    return (Filter) constr.newInstance();
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
            throws ValidationException
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
                throw new ValidationException("Invalid decimal number: " + numeric, ex);
            }
        }

        public static Long asLong(String numeric, int radix)
            throws ValidationException
        {
            if (numeric == null)
            {
                return null;
            }
            try
            {
                return Long.parseLong(numeric, radix);
            }
            catch (NumberFormatException ex)
            {
                throw new ValidationException("Invalid integer number: " + numeric, ex);
            }
        }
    }
}
