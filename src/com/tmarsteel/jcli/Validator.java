package com.tmarsteel.jcli;

import com.tmarsteel.jcli.rule.BaseRule;
import com.tmarsteel.jcli.rule.Rule;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * Parses an argument-array in the given environment
 * @author tmarsteel
 */
public class Validator
{
    private Environment env;
    private final List<Option> options = new ArrayList<>();
    private final List<Flag> flags = new ArrayList<>();
    private final List<Rule> rules = new ArrayList<>();
    private final List<Argument> arguments = new ArrayList<>();
    private final boolean flagsOptionsDistinguishable;
    
    /**
     * Constructs a new parser for the systems default environment.
     */
    public Validator()
    {
        this(Environment.getEnvironment());
    }
    
    /**
     * Constructs a new parser for the specified environment.
     * @param env The environment this parser should expect.
     */
    public Validator(Environment env)
    {
        this.env = env;
        this.flagsOptionsDistinguishable = !(env.getFlagMarker().equals(env.getOptionMarker()));
    }
    
    /**
     * Returns the environment this parser expects.
     * @return The environment this parser expects.
     */
    public Environment getEnvironment()
    {
        return env;
    }
    
    /**
     * Sets the environment this parser should expect.
     * @param env The environment this parser should expect.
     */
    public void setEnvironment(Environment env)
    {
        this.env = env;
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
                    flags.add((Flag) o);
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
            Iterator<Flag> fIt = flags.iterator();
            while (fIt.hasNext())
            {
                Option cur = fIt.next();
                if (cur.isAmbigousWith(o))
                {
                    throw new IllegalArgumentException("Flag "
                        + cur + " is ambigous with " + o);
                }
            }
            
            Iterator<Option> oIt = options.iterator();
            while (oIt.hasNext())
            {
                Option cur = oIt.next();
                if (cur.isAmbigousWith(o))
                {
                    throw new IllegalArgumentException("Option "
                        + cur + " is ambigous with " + o);
                }
            }
            
            if (o instanceof Flag)
            {
                flags.add((Flag) o);
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
        Iterator<Flag> flagIterator = flags.iterator();
        while (flagIterator.hasNext())
        {
            final Flag flag = flagIterator.next();
            vinput.flagValues.put(flag.getPrimaryIdentifier(),
                input.containsFlag(flag));
        }
        
        // check for unknown flags
        Iterator<String> stringIterator = input.flags.iterator();
        while (stringIterator.hasNext())
        {
            final String flag = stringIterator.next();
            if (!vinput.flagValues.containsKey(flag))
            {
                vinput.flagValues.put(flag, Boolean.TRUE);
            }
        }
        
        // check for known options
        Iterator<Option> optionIterator = options.iterator();
        while (optionIterator.hasNext())
        {
            final Option option = optionIterator.next();
            final String optValue = input.getOption(option);
            if (optValue == null)
            {
                if (option.isRequired())
                {
                    throw new ParseException("Required option " + option.getPrimaryIdentifier() + " not set.");
                }
                else
                {
                    vinput.optionValues.put(option.getPrimaryIdentifier(), option.getDefaultValue());
                }
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
        Iterator<Entry<String,String>> unknownOptionsIterator = input.options.entrySet().iterator();
        while (unknownOptionsIterator.hasNext())
        {
            final Entry<String,String> option = unknownOptionsIterator.next();
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
     * @author tmarsteel
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
