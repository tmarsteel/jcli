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
package com.tmarsteel.jcli.validation.configuration.xml;

import com.tmarsteel.jcli.*;
import com.tmarsteel.jcli.filter.Filter;
import com.tmarsteel.jcli.filter.MetaRegexFilter;
import com.tmarsteel.jcli.filter.PathFilter;
import com.tmarsteel.jcli.rule.*;
import com.tmarsteel.jcli.validation.MisconfigurationException;
import com.tmarsteel.jcli.validation.ValidationException;
import com.tmarsteel.jcli.validation.Validator;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.tmarsteel.jcli.validation.configuration.ValidatorConfigurator;
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
    
    private final Map<String,FilterParser<? extends Filter>> filterParsers = new HashMap<>();
    private final Map<String,RuleParser<? extends Rule>>     ruleParsers   = new HashMap<>();
    
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
        filterParsers.put("big-decimal", FilterParsingUtil::parseBigDecimalFilter);
        filterParsers.put("big-integer", FilterParsingUtil::parseBigInteger);
        filterParsers.put("decimal",     FilterParsingUtil::parseDecimalFilter);
        filterParsers.put("integer",     FilterParsingUtil::parseIntegerFilter);
        filterParsers.put("regex",       FilterParsingUtil::parseRegexFilter);
        filterParsers.put("set",         FilterParsingUtil::parseSetFilter);
        filterParsers.put("file",        FilterParsingUtil::parseFileFilter);
        filterParsers.put("path",        (context, node) -> new PathFilter(FilterParsingUtil.parseFileFilter(context, node)));
        filterParsers.put("pattern",     (context, node) -> new MetaRegexFilter());
        
        ruleParsers.put("and",        RuleParsingUtil.combinedRuleParser(AndRule.class));
        ruleParsers.put("or",         RuleParsingUtil.combinedRuleParser(OrRule.class));
        ruleParsers.put("xor",        RuleParsingUtil.combinedRuleParser(XorRule.class));
        ruleParsers.put("not",        RuleParsingUtil.combinedRuleParser(NotRule.class));
        ruleParsers.put("option-xor", RuleParsingUtil::parseXorOptionsRule);
        ruleParsers.put("option-set", RuleParsingUtil::parseOptionSetRule);
    }

    /**
     * Registers the given filter type with the given parser. If {@link #configure(Validator)} encounters a
     * &lt;filter&gt; with the {@code type} attribute set to {@code type} it will delegate the parsing to the given
     * {@link FilterParser}.
     * @param type The type string to register.
     * @param parser The parser to use for filters of type {@code type}.
     * @throws NullPointerException If {@code type} or {@code parser} is null.
     */
    public void setFilterType(String type, FilterParser<? extends Filter> parser)
    {
        Objects.requireNonNull(type);
        Objects.requireNonNull(parser);

        this.filterParsers.put(type, parser);
    }

    /**
     * Registers the given rule type with the given parser. If {@link #configure(Validator)} encounters a &lt;rule&gt;
     * with the {@code type} attribute set to {@code type} it will delegate the parsing to the given {@link RuleParser}.
     * @param type The type string to register.
     * @param parser The parser to use for rules of type {@code type}.
     * @throws NullPointerException If {@code type} or {@code parser} is null.
     */
    public void setRuleType(String type, RuleParser<? extends Rule> parser)
    {
        Objects.requireNonNull(type);
        Objects.requireNonNull(parser);

        this.ruleParsers.put(type, parser);
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
        String description = null;

        NodeList childNodes = flagNode.getChildNodes();
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
                        throw new MisconfigurationException("Empty alias for flag " + primaryIdentifier);
                    }
                    names.add(alias);
                    break;
                case "description":
                    description = node.getTextContent();
                    break;
                default:
                    throw new MisconfigurationException("Unknown tag " + node.getNodeName());
            }
        }

        Flag f = new Flag(names.toArray(new String[names.size()]));
        f.setDescription(description == null? "" : description);
        return f;
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
        String description = null;

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
                    }
                    names.add(alias);
                    break;
                case "filter":
                    filter = parseFilter(node);
                    break;
                case "default":
                    defValue = node.getTextContent();
                    break;
                case "description":
                    description = node.getTextContent();
                    break;
                default:
                    throw new MisconfigurationException("Unknown tag " + node.getNodeName());
            }
        }

        Option o = new Option(filter, defValue, names.toArray(new String[names.size()]));
        o.setRequired(attrs.getNamedItem("required") != null);
        o.setAllowsMultipleValues(attrs.getNamedItem("collection") != null);
        o.setDescription(description == null? "" : description);
        
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

        final boolean variadic;
        node = attrs.getNamedItem("variadic");
        if (node == null)
        {
            variadic = false;
        }
        else
        {
            switch (node.getTextContent()) {
                case "true":
                    variadic = true;
                    break;
                case "false":
                    variadic = false;
                    break;
                default:
                    throw new MisconfigurationException("Illegal value for attribute variadic");
            }
        }

        // filters, description
        Filter filter = null;
        String defValue = null;
        String description = "";
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
                    case "description":
                        description = node.getTextContent();
                        break;
                    default:
                        throw new MisconfigurationException("Unknown tag " + node.getNodeName()
                            + " in argument (index " + index + ")");
                }
            }
        }
        Argument arg = new Argument(primaryIdentifier, index, defValue, filter);
        arg.setRequired(required);
        arg.setDescription(description);
        arg.setVariadic(variadic);
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
        Node node = attrs.getNamedItem("type");
        if (node == null)
        {
            throw new MisconfigurationException("No type attribute specified for filter");
        }

        final String filterType = node.getTextContent();
        FilterParser<? extends Filter> filterParser = this.filterParsers.get(filterType);

        if (filterParser == null)
        {
            throw new MisconfigurationException("Unknown filter type " + filterType);
        }

        try
        {
            return Objects.requireNonNull(filterParser.parse(this, node), "Filter parser returned null");
        }
        catch(ParseException ex) {
            throw new MisconfigurationException(
                "Failed to parse filter of type " + filterType,
                ex
            );
        }
    }
    
    private Rule parseRule(Node ruleNode)
        throws MisconfigurationException
    {
        NamedNodeMap attrs = ruleNode.getAttributes();
        Node node = attrs.getNamedItem("type");
            
        if (node == null)
        {
            throw new MisconfigurationException("No type attribute specified for rule");
        }
            
        final String ruleType = node.getTextContent();
        RuleParser<? extends Rule> ruleParser = this.ruleParsers.get(ruleType);

        if (ruleParser == null)
        {
            throw new MisconfigurationException("Unknown rule type " + ruleType);
        }

        try
        {
            return Objects.requireNonNull(
                ruleParser.parse(
                    this,
                    node,
                    (context, _node, subParser) -> this.parseRule(node)
                ),
                "Rule parser returned null"
            );
        }
        catch (ParseException ex)
        {
            throw new MisconfigurationException(
                "Failed to parse rule of type " + ruleType,
                ex
            );
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

        public static Long asLong(String numeric, int radix)
            throws ParseException
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
                throw new ParseException("Invalid integer number: " + numeric, ex);
            }
        }
    }
}
