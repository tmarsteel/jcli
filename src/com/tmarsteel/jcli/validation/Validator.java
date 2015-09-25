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
package com.tmarsteel.jcli.validation;

import com.tmarsteel.jcli.Argument;
import com.tmarsteel.jcli.Environment;
import com.tmarsteel.jcli.Flag;
import com.tmarsteel.jcli.Input;
import com.tmarsteel.jcli.Option;
import com.tmarsteel.jcli.ParseException;
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
     * Constructs a new validator for the systems default environment.
     */
    public Validator()
    {
        this(Environment.getEnvironment());
    }
    
    /**
     * Constructs a new validator for the specified environment.
     * @param env The environment this validator should expect.
     */
    public Validator(Environment env)
    {
        this.env = env;
        this.flagsOptionsDistinguishable = !(env.getFlagMarker().equals(env.getOptionMarker()));
    }
    
    /**
     * Returns the environment this validator expects.
     * @return The environment this validator expects.
     */
    public Environment getEnvironment()
    {
        return env;
    }
    
    /**
     * Sets the environment this validator should expect.
     * @param env The environment this validator should expect.
     */
    public void setEnvironment(Environment env)
    {
        this.env = env;
    }
    
    /**
     * Returns whether this validator is sensible for the given flag.
     * @param name The name of the flag to check for.
     * @return Whether this validator is sensible for the given flag.
     */
    public boolean knowsFlag(String name)
    {
        for (Flag f : flags)
        {
            if (f.isIdentifiedBy(name))
            {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Returns whether this validator is sensible for the given option or argument.
     * @param name The name of the option or argument to check for.
     * @return Whether this validator is sensible for the given option or argument.
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
     * Adds the given option to the list of known options.
     * @param o The option to add.
     */
    public synchronized void add(Option o)
    {
        if (flagsOptionsDistinguishable)
        {
            if (!options.contains(o))
            {
                options.add(o);
            }
        }
        else
        {
            // if flags and options are not distinguishable name-interference
            // has to be checked.
            Iterator<Flag> it = flags.iterator();
            while (it.hasNext())
            {
                Flag cur = it.next();
                if (cur.isAmbigousWith(o))
                {
                    throw new IllegalArgumentException("Flag "
                        + cur + " is ambigous with " + o);
                }
            }
            
            options.add(o);
        }
    }
    
    /**
     * Adds the given flag to the list of known flags.
     * @param f The flag to add.
     */
    public synchronized void add(Flag f)
    {
        if (flagsOptionsDistinguishable)
        {
            if (!flags.contains(f))
            {
                flags.add(f);
            }
        }
        else
        {
            // if flags and options are not distinguishable name-interference
            // has to be checked.
            Iterator<Option> it = options.iterator();
            while (it.hasNext())
            {
                Option cur = it.next();
                if (cur.isAmbigousWith(f))
                {
                    throw new IllegalArgumentException(cur + " is ambigous with " + f);
                }
            }
            
            flags.add(f);
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
     * Parses and validates the given input.
     * @param argline A string containing the input parameters, e.g.
     *  <code>"-v --input foo.txt"</code>
     * @return The parsed and validated input.
     */
    public ValidatedInput parse(String argline)
        throws ParseException, ValidationException
    {
        return parse(new Input(this, this.env, argline));
    }
    
    /**
     * Parses and validates the given input.
     * @param args The input parameters to parse and validate.
     * @return The parsed and validated input.
     */
    public ValidatedInput parse(String[] args)
        throws ParseException, ValidationException
    {
        return parse(new Input(this, this.env, args));
    }
    
    /**
     * Parses the given input.
     * @param input The input parameters to parse.
     * @return The parsed input.
     */
    public ValidatedInput parse(Input input)
        throws ValidationException
    {
        ValidatedInput vinput = new ValidatedInput();
        
        // check for known flags
        Iterator<Flag> flagIt = flags.iterator();
        while (flagIt.hasNext())
        {
            final Flag flag = flagIt.next();
            vinput.flagValues.put(flag.getPrimaryIdentifier(),
                input.containsFlag(flag));
        }
        
        // check for unknown flags
        Iterator<String> fIt = input.flags().iterator();
        while (fIt.hasNext())
        {
            final String flag = fIt.next();
            if (!vinput.flagValues.containsKey(flag))
            {
                vinput.flagValues.put(flag, Boolean.TRUE);
            }
        }
        
        // check for known options
        Iterator<Option> optionIt = options.iterator();
        while (optionIt.hasNext())
        {
            final Option option = optionIt.next();
            final String optValue = input.getOption(option);
            
            if (optValue == null)
            {
                if (option.isRequired())
                {
                    throw new ValidationException("Required option " + option.getPrimaryIdentifier() + " not set.");
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
                catch (ValidationException ex)
                {
                    throw new ValidationException("Invalid value for option " +
                        option.getPrimaryIdentifier() + ": " + ex.getMessage(), ex);
                }
                vinput.optionValues.put(option.getPrimaryIdentifier(), value);
            }
        }
        
        // check for unknown options
        Iterator<Entry<String,String>> oIt = input.options().entrySet().iterator();
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
                    throw new ValidationException("You must specify at least "
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
     * Resets this validator to the state it was in after being constructed.
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
    public static class ValidatedInput
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
        public boolean isFlagSet(Flag flag)
        {
            return isFlagSet(flag.getPrimaryIdentifier());
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
         * Writes all values of the flags, options and arguments known by this
         * input to an instance of {@link java.util.Properties}. The boolean state of flags
         * will be represented by the strings <code>"true"</code> and
         * <code>"false"</code>
         */
        public void writeTo(final Properties to)
        {
            flagValues.entrySet().forEach((entry) -> {
                to.setProperty(entry.getKey(), entry.getValue()? "true" : "false");
            });
           
            optionValues.entrySet().forEach((entry) -> {
                to.setProperty(entry.getKey(), entry.getValue().toString());
            });
        }
    }
}
