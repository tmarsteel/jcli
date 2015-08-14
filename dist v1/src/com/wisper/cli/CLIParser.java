package com.wisper.cli;

import com.wisper.cli.rule.Rule;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Parses an argument-array in the given environment
 * @author Tobias Marstaller
 */
public class CLIParser
{
    protected final Environment env;
    protected final ArrayList<Option> options = new ArrayList<>();
    protected final ArrayList<Option> flags = new ArrayList<>();
    protected final ArrayList<Rule> rules = new ArrayList<>();
    protected final ArrayList<Argument> arguments = new ArrayList<>();
    protected final boolean flagsOptionsDistinguishable;
    
    /**
     * Parses the configuration-xml from <code>configInputStream</code> and returns
     * an configured parser.
     * @param configInputStream A stream to read the configuration from.
     * @return A parser configured according to the configuration read from
     * <code>configInputStream</code>.
     * @throws SAXException If an XML Syntax-Error occurs.
     * @throws IOException If an I/O-Error occurs.
     * @throws ParseException If a semantic error occurs while parsing the xml.
     */
    public static CLIParser getInstance(InputStream configInputStream)
        throws SAXException, IOException, ParseException
    {
        return getInstance(configInputStream, null);
    }
    
    /**
     * Parses the configuration-xml from <code>configInputStream</code> and returns
     * an configured parser.
     * @param configInputStream A stream to read the configuration from.
     * @return A parser configured according to the configuration read from
     * <code>configInputStream</code>.
     * @throws SAXException If an XML Syntax-Error occurs.
     * @throws IOException If an I/O-Error occurs.
     * @throws ParseException If a semantic error occurs while parsing the xml.
     */
    public static CLIParser getInstance(InputStream configInputStream,
        Environment env)
        throws SAXException, IOException, ParseException
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(false);
        dbf.setIgnoringComments(true);
        dbf.setIgnoringElementContentWhitespace(true);
        
        try
        {
            DocumentBuilder builder = dbf.newDocumentBuilder();
            return getInstance(builder.parse(configInputStream), env);
        }
        catch (ParserConfigurationException ex)
        {
            throw new RuntimeException(ex);
        }
    }
    
    /**
     * Parses the configuration-xml from <code>configFile</code> and returns
     * an configured parser.
     * @param configFile A file to read the configuration from.
     * @return A parser configured according to the configuration read from
     * <code>configFile</code>.
     * @throws SAXException If an XML Syntax-Error occurs.
     * @throws IOException If an I/O-Error occurs.
     * @throws ParseException If a semantic error occurs while parsing the xml.
     */
    public static CLIParser getInstance(File configFile)
        throws IOException, SAXException, ParseException
    {
        return getInstance(configFile, null);
    }
    
    /**
     * Parses the configuration-xml from <code>configFile</code> and returns
     * an configured parser.
     * @param configFile A file to read the configuration from.
     * @return A parser configured according to the configuration read from
     * <code>configFile</code>.
     * @throws SAXException If an XML Syntax-Error occurs.
     * @throws IOException If an I/O-Error occurs.
     * @throws ParseException If a semantic error occurs while parsing the xml.
     */
    public static CLIParser getInstance(File configFile, Environment env)
        throws IOException, SAXException, ParseException
    {
        try (FileInputStream fIn = new FileInputStream(configFile))
        {
            return getInstance(fIn, env);
        }
    }
    
    /**
     * Parses the configuration-xml in <code>xmlDocument</code> and returns
     * an configured parser.
     * @return A parser configured according to the configuration read from
     * <code>xmlDocument</code>.
     * @throws ParseException If a semantic error occurs while parsing the xml.
     */
    public static CLIParser getInstance(Document xmlDocument)
        throws ParseException
    {
        return new ConfiguredCLIParser(xmlDocument);
    }
    
    /**
     * Parses the configuration-xml in <code>xmlDocument</code> and returns
     * an configured parser.
     * @return A parser configured according to the configuration read from
     * <code>xmlDocument</code>.
     * @throws ParseException If a semantic error occurs while parsing the xml.
     */
    public static CLIParser getInstance(Document xmlDocument, Environment env)
        throws ParseException
    {
        if (env != null)
        {
            return new ConfiguredCLIParser(xmlDocument, env);
        }
        else
        {
            return getInstance(xmlDocument);
        }
    }
    
    /**
     * Constructs a new parser for the systems default environment.
     */
    public CLIParser()
    {
        this(File.separatorChar == '/'? Environment.UNIX : Environment.WINDOWS);
    }
    
    /**
     * Constructs a new parser for the specified environment.
     */
    public CLIParser(Environment env)
    {
        this.env = env;
        this.flagsOptionsDistinguishable = !(env.getFlagMarker().equals(env.getOptionMarker()));
    }
    
    /**
     * Returns whether this parser is sensible for the given flag.
     * @param name The name of the flag to check for.
     * @return Whether this parser is sensible for the given flag.
     */
    public boolean knowsFlag(String name)
    {
        for (Option f:flags)
        {
            if (f.isIdentifiedBy(name))
            {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Returns whether this parser is sensible for the given option.
     * @param name The name of the option to check for.
     * @return Whether this parser is sensible for the given option.
     */
    public boolean knowsOption(String name)
    {
        for (Option o:options)
        {
            if (o.isIdentifiedBy(name))
            {
                return true;
            }
        }
        for (Argument a:arguments)
        {
            if (a.getIdentifier().equals(name))
            {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Adds the given flag or option to the list of known flags / options.
     * @param o The flag or option to add.
     */
    public synchronized void add(Option o)
    {
        if (flagsOptionsDistinguishable)
        {
            if (o.isFlag())
            {
                if (!flags.contains(o))
                {
                    flags.add(o);
                }
                else
                {
                    throw new IllegalArgumentException("Flag " + o.getPrimaryIdentifier()
                        + " already registered.");
                }
            }
            else
            {
                if (!options.contains(o))
                {
                    options.add(o);
                }
                else
                {
                    throw new IllegalArgumentException("Option " + o.getPrimaryIdentifier()
                        + " already registered.");
                }
            }
        }
        else
        {
            // if flags and options are not distinguishable name-interference
            // has to be checked.
            Iterator<Option> it = flags.iterator();
            while (it.hasNext())
            {
                Option cur = it.next();
                if (cur.isAmbigousWith(o))
                {
                    throw new IllegalArgumentException("Flag "
                        + cur + " is ambigous with " + o);
                }
            }
            
            it = options.iterator();
            while (it.hasNext())
            {
                Option cur = it.next();
                if (cur.isAmbigousWith(o))
                {
                    throw new IllegalArgumentException("Option "
                        + cur + " is ambigous with " + o);
                }
            }
            
            if (o.isFlag())
            {
                flags.add(o);
            }
            else
            {
                options.add(o);
            }
        }
    }
    
    /**
     * Adds the given rule to the set of rules to check after input has been parsed.
     * @param r The rule to add.
     */
    public synchronized void add(Rule r)
    {
        if (!this.rules.contains(r))
        {
            this.rules.add(r);
        }
    }
    
    /**
     * Adds the given argument to the set of arguments to parse on input.
     */
    public synchronized void add(Argument arg)
    {
        if (!this.arguments.contains(arg))
        {
            final Iterator<Argument> it = this.arguments.iterator();
            while (it.hasNext())
            {
                final Argument carg = it.next();
                if (carg.getIndex() == arg.getIndex())
                {
                    throw new IllegalArgumentException("Cannot add argument "
                        + arg.getIdentifier() + ": argument " + carg.getIdentifier()
                        + " already occupies index " + arg.getIndex());
                }
            }
            this.arguments.add(arg);
        }
    }
    
    /**
     * Parses the given input.
     * @param argline A string containing the input parameters, e.g.
     *  <code>"-v --input foo.txt"</code>
     * @return The parsed input.
     */
    public ValidatedInput parse(String argline)
        throws ParseException
    {
        return parse(new Input(this, this.env, argline));
    }
    
    /**
     * Parses the given input.
     * @param args The input parameters to parse.
     * @return The parsed input.
     */
    public ValidatedInput parse(String[] args)
        throws ParseException
    {
        return parse(new Input(this, this.env, args));
    }
    
    /**
     * Parses the given input.
     * @param input The input parameters to parse.
     * @return The parsed input.
     */
    public ValidatedInput parse(Input input)
        throws ParseException
    {
        ValidatedInput vinput = new ValidatedInput();
        
        // check for known flags
        Iterator<Option> it = flags.iterator();
        while (it.hasNext())
        {
            final Option flag = it.next();
            vinput.flagValues.put(flag.getPrimaryIdentifier(),
                input.containsFlag(flag));
        }
        
        // check for unknown flags
        Iterator<String> fIt = input.flags.iterator();
        while (fIt.hasNext())
        {
            final String flag = fIt.next();
            if (!vinput.flagValues.containsKey(flag))
            {
                vinput.flagValues.put(flag, Boolean.TRUE);
            }
        }
        
        // check for known options
        it = options.iterator();
        while (it.hasNext())
        {
            final Option option = it.next();
            final String optValue = input.getOption(option);
            if (optValue == null)
            {
                vinput.optionValues.put(option.getPrimaryIdentifier(),
                    option.getDefaultValue());
            }
            else
            {
                final Object value;
                
                try
                {
                    value = option.parse(optValue);
                }
                catch (ParseException ex)
                {
                    throw new ParseException("Invalid value for option " +
                        option.getPrimaryIdentifier() + ": " + ex.getMessage(), ex);
                }
                vinput.optionValues.put(option.getPrimaryIdentifier(), value);
            }
        }
        
        // check for unknown options
        Iterator<Entry<String,String>> oIt = input.options.entrySet().iterator();
        while (oIt.hasNext())
        {
            final Entry<String,String> option = oIt.next();
            if (!vinput.optionValues.containsKey(option.getKey()))
            {
                vinput.optionValues.put(option.getKey(), option.getValue());
            }
        }
        
        // check the arguments
        for (Argument arg : arguments)
        {
            final String value = input.getArgument(arg.getIndex());
            if (value == null)
            {
                if (arg.isRequired())
                {
                    throw new ParseException("You must specify at least "
                        + (arg.getIndex() + 1) + " argument(s).");
                }
                vinput.optionValues.put(arg.getIdentifier(), arg.getDefaultValue());
            }
            else
            {
                vinput.optionValues.put(arg.getIdentifier(), arg.parse(value));
            }
        }
        
        // check the rules
        Iterator<Rule> rIt = rules.iterator();
        while (rIt.hasNext())
        {
            rIt.next().validate(this, vinput);
        }
        
        return vinput;
    }
    
    /**
     * Returns the default value for the given option or null if such an option
     * was not specified.
     * @param optionName The name of the option to obtain the default value from.
     * @return The default value for the given option or null if such an option
     */
    public Object getDefaultValue(final String optionName)
    {
        final Iterator<Option> it = options.iterator();
        while (it.hasNext())
        {
            final Option opt = it.next();
            if (opt.isIdentifiedBy(optionName))
            {
                return opt.getDefaultValue();
            }
        }
        return null;
    }
    
    /**
     * Resets this parser to the state it was in after being constructed.
     */
    public void reset()
    {
        this.flags.clear();
        this.options.clear();
        this.rules.clear();
        this.arguments.clear();
    }
    
    /**
     * Represents a set of validated and organized inputs.
     * @author Tobias Marstaller
     */
    public class ValidatedInput
    {
        protected HashMap<String,Boolean> flagValues = new HashMap<>();
        protected HashMap<String,Object> optionValues = new HashMap<>();
        
        /**
         * Returns the value of the given option or argument.
         * @param option The primary identifier of the option or argument to
         *  obtain the value from.
         * @return The value of the given option or argument.
         */
        public Object getOption(String option)
        {
            return optionValues.get(option);
        }
        
        /**
         * Returns the value of the given option.
         * @return The value of the given option.
         */
        public Object getOption(Option option)
        {
            return getOption(option.getPrimaryIdentifier());
        }
        
        /**
         * Returns whether the given flag was set.
         * @param flag The primary identifier to obtain the status from.
         * @return Whether the given flag was set.
         */
        public boolean isFlagSet(String flag)
        {
            Boolean is = flagValues.get(flag);
            return is == null? false : is;
        }

        /**
         * Returns whether the given flag was set.
         * @return Whether the given flag was set.
         */
        public boolean isFlagSet(Option flag)
        {
            return flag.isFlag() && isFlagSet(flag.getPrimaryIdentifier());
        }
        
        /**
         * Returns an iterator that will rotate over all flags known by this input.
         * @return An iterator that will rotate over all flags known by this input.
         */
        public Iterator<Entry<String,Boolean>> getFlagIterator()
        {
            return flagValues.entrySet().iterator();
        }
        
        /**
         * Returns an iterator that will rotate over all options known by this input.
         * @return An iterator that will rotate over all options known by this input.
         */
        public Iterator<Entry<String,Object>> getOptionIterator()
        {
            return optionValues.entrySet().iterator();
        }
        
        /**
         * Returns an instance of {@link java.util.Properties} that holds all values
         * of the flags and options known by this input. The boolean state of
         * flags will be represented by the strings <code>"true"</code> and
         * <code>"false"</code>
         * @return An instance of {@link java.util.Properties} that holds all values
         * of the flags and options known by this input.
         */
        public Properties asProperties()
        {
            Properties p = new Properties();
            writeTo(p);
            return p;
        }
        
        /**
         * Writes all values of the flags and options known by this input to an
         * instance of {@link java.util.Properties}. The boolean state of flags
         * will be represented by the strings <code>"true"</code> and
         * <code>"false"</code>
         */
        public void writeTo(final Properties to)
        {
            final Iterator<Entry<String,Boolean>> flagIt = flagValues.entrySet().iterator();
            while (flagIt.hasNext())
            {
                final Entry<String,Boolean> cur = flagIt.next();
                to.setProperty(cur.getKey(), cur.getValue()? "true" : "false");
            }
            
            final Iterator<Entry<String,Object>> optionsIt = optionValues.entrySet().iterator();
            while (optionsIt.hasNext())
            {
                final Entry<String,Object> cur = optionsIt.next();
                to.setProperty(cur.getKey(), cur.getValue().toString());
            }
        }
    }
}
