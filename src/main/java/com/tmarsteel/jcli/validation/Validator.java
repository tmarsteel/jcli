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

import com.tmarsteel.jcli.*;
import com.tmarsteel.jcli.rule.Rule;

import java.util.*;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

/**
 * Parses an argument-array in the given environment
 * @author tmarsteel
 */
public class Validator
{
    private Environment env;
    private final Set<Option> options = new HashSet<>();
    private final Set<Flag> flags = new HashSet<>();
    private final Set<Rule> rules = new HashSet<>();

    /**
     * List of all arguments. Is sorted index ascending
     */
    private final SortedSet<Argument> arguments = new TreeSet<>((arg1, arg2) -> arg1.getIndex() - arg2.getIndex());

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
     * Returns whether this validator is sensible for the given option.
     * @param name The name of the option to check for.
     * @return Whether this validator is sensible for the given option.
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
        return false;
    }

    /**
     * Returns whether this validator is sensible for the given argument.
     * @param name The name of the argument to check for.
     * @return Whether this validator is sensible for the given argument.
     */
    public boolean knowsArgument(String name) {
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
     * @throws MisconfigurationException If the given option has duplicate names with other options configured in this
     *                                   validator.
     */
    public synchronized void add(Option o)
        throws MisconfigurationException
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
                    throw new MisconfigurationException("Flag "
                        + cur + " is ambiguous with " + o);
                }
            }
            
            options.add(o);
        }
    }
    
    /**
     * Adds the given flag to the list of known flags.
     * @param f The flag to add.
     * @throws MisconfigurationException If the given flag has duplicate names with other flags configured in this
     *                                   validator.
     */
    public synchronized void add(Flag f)
        throws MisconfigurationException
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
                    throw new MisconfigurationException(cur + " is ambiguous with " + f);
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
     * @throws MisconfigurationException If an argument already configured targets the same index as the given argument<br>
     *                                   If the given argument is variadic and a variadic argument is already configured<br>
     *                                   If the given argument is variadic and an other configured argument targets a
     *                                   greater index as the given argument.<br>
     *                                   If a variadic argument targeting a lower index than the given argument is already
     *                                   configured.
     */
    public synchronized void add(Argument arg)
        throws MisconfigurationException
    {
        if (!this.arguments.contains(arg))
        {
            this.arguments.forEach(carg -> {
                if (carg.getIndex() == arg.getIndex())
                {
                    throw new MisconfigurationException("Cannot add argument "
                        + arg.getIdentifier() + ": argument " + carg.getIdentifier()
                        + " already occupies index " + arg.getIndex());
                }
                if (arg.isVariadic() && carg.isVariadic()) {
                    throw new MisconfigurationException("Cannot validate using multiple variadic arguments: argument " +
                        carg.getIdentifier() + " is variadic");
                }
                if (arg.isVariadic() && carg.getIndex() > arg.getIndex()) {
                    throw new MisconfigurationException("Variadic argument " + arg.getIdentifier() + " must occupy the" +
                            "greatest index; argument " + carg.getIdentifier() + " has a greater index of " + carg.getIndex());
                }
                if (carg.isVariadic() && arg.getIndex() > carg.getIndex()) {
                    throw new MisconfigurationException("Cannot add any more arguments after variadic argument " +
                        carg.getIdentifier() + " at index " + carg.getIndex());
                }
            });

            this.arguments.add(arg);
        }
    }

    /**
     * Returns an {@link Iterator} of all the {@link Option}s knonw to this validator.
     * @return An {@link Iterator} of all the {@link Option}s knonw to this validator.
     */
    public Iterator<? extends Option> options() {
        return options.iterator();
    }

    /**
     * Returns an {@link Iterator} of all the {@link Flag}s knonw to this validator.
     * @return An {@link Iterator} of all the {@link Flag}s knonw to this validator.
     */
    public Iterator<? extends Flag> flags() {
        return flags.iterator();
    }

    /**
     * Returns an {@link Iterator} of all the {@link Argument}s knonw to this validator.
     * @return An {@link Iterator} of all the {@link Argument}s knonw to this validator.
     */
    public Iterator<? extends Argument> arguments() {
        return arguments.iterator();
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
            final List<String> rawOptValues = input.getOption(option);
            
            if (rawOptValues.isEmpty() && option.isRequired())
            {
                throw new ValidationException("Required option " + option.getPrimaryIdentifier() + " not set.");
            }
            else if (rawOptValues.size() > 1 && !option.allowsMultipleValues())
            {
                throw new ValidationException("Option " + option.getPrimaryIdentifier() + " may only be set once, " + rawOptValues.size() + " values given.");
            }
            else
            {
                List<Object> values = new ArrayList<>(rawOptValues.size());
                for (String value : rawOptValues) {
                    values.add(option.parse(value));
                }
                vinput.optionValues.put(option.getPrimaryIdentifier(), Collections.unmodifiableList(values));
            }
        }
        
        // check for unknown options
        input.options().forEach((name, values) -> {
            if (!knowsOption(name))
            {
                vinput.optionValues.put(name, new ArrayList<>(values.size()));
                values.forEach(vinput.optionValues.get(name)::add);
                vinput.optionValues.put(name, Collections.unmodifiableList(vinput.optionValues.get(name)));
            }
        });
        
        // check the arguments; they are sorted by index ascending
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

                if (arg.getDefaultValue() == null) {
                    vinput.argumentValues.put(arg.getIdentifier(), Collections.unmodifiableList(Arrays.asList()));
                }
                else
                {
                    vinput.argumentValues.put(arg.getIdentifier(), Collections.unmodifiableList(Arrays.asList(arg.getDefaultValue())));
                }
            }
            else
            {
                List<Object> values = new ArrayList<>();
                // if variadic: consume all from the index to the end of the input
                if (arg.isVariadic()) {
                    // stream API not usable because of the ValidationException thrown by Argument#parse
                    for (int i = arg.getIndex();i < input.arguments().size();i++) {
                        values.add(arg.parse(input.getArgument(i)));
                    }
                } else {
                    values.add(arg.parse(value));
                }
                vinput.argumentValues.put(arg.getIdentifier(), Collections.unmodifiableList(values));
            }
        }
        
        // check the rules
        Iterator<Rule> rIt = rules.iterator();
        while (rIt.hasNext())
        {
            rIt.next().validate(this, vinput);
        }

        vinput.flagValues = Collections.unmodifiableMap(vinput.flagValues);
        vinput.optionValues = Collections.unmodifiableMap(vinput.optionValues);
        vinput.argumentValues = Collections.unmodifiableMap(vinput.argumentValues);

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
        /**
         * Flag values. Contains a key for each flag known at the time of parsing the input. Whether a flag was actually
         * present in the input is denoted by the map value.
         */
        protected Map<String,Boolean> flagValues = new HashMap<>();

        /**
         * Option values. Contains a key for each option known at the time parsing the input. Options which cannot hold
         * multiple values have only one entry in the list. If an option has a default value and was not specified in the
         * parsed input its default value is in the list.
         */
        protected Map<String,List<Object>> optionValues = new HashMap<>();

        /**
         * Argument values. Contains a key for each argument known at the time parsing the input. Variadic arguments have
         * multiple entries in the list. If an argument has a default value and was not specified in the parsed input its
         * default value is in the list.
         */
        protected Map<String,List<Object>> argumentValues = new HashMap<>();
        
        /**
         * Returns the value of the given option. If the option has multiple values, the first value is returned.
         * @param identifier The options primary identifier (see {@link Option#getPrimaryIdentifier()}) whose value
         *                   to retrieve
         * @return The value of the given option. Actual data type depends on the filter. Returns null if
         * this option was not specified in the input and has no default value.
         * @throws NoSuchElementException If no known option is primarily identified by {@code identifier}.
         */
        public Object getOption(String identifier)
                throws NoSuchElementException
        {
            List<Object> values = getOptionValues(identifier);

            if (values.size() == 0) {
                return null;
            }

            return values.get(0);
        }

        /**
         * Returns the value of the given option. If the option has multiple values, the first value is returned.
         * @param option The option whose value to retrieve.
         * @return The value of the given option. Actual data type depends on the filter. Returns null if
         * this option was not specified in the input and has no default value.
         * @throws NoSuchElementException If the given option was not known at the time the input was parsed.
         */
        public Object getOption(Option option)
                throws NoSuchElementException
        {
            return getOption(option.getPrimaryIdentifier());
        }

        /**
         * Returns all values of the given option.
         * @param identifier The options primary identifier (see {@link Option#getPrimaryIdentifier()}) whose values
         *                   to retrieve
         * @return The values of the given option. If the option was not specified in the input, an empty list is returned.
         * @throws NoSuchElementException If no known option is primarily identified by {@code identifier}.
         */
        public List<Object> getOptionValues(String identifier)
            throws NoSuchElementException
        {
            List<Object> values = optionValues.get(identifier);

            if (values == null) {
                throw new NoSuchElementException("Unknown option " + identifier);
            }

            return values;
        }

        /**
         * Returns all values of the given option.
         * @param option The options whose values to retrieve
         * @return The values of the given option. If the option was not specified in the input, an empty list is returned.
         * @throws NoSuchElementException If the given option was not known at the time the input was parsed.
         */
        public List<Object> getOptionValues(Option option)
                throws NoSuchElementException
        {
            return getOptionValues(option.getPrimaryIdentifier());
        }

        /**
         * Returns the value of the given argument. If the argument is variadic, the first value is returned.
         * @param identifier The arguments identifier (see {@link Argument#getIndex()}) whose value to retrieve.
         * @return The arguments value. If the argument was not given in the input, null is returned.
         * @throws NoSuchElementException If no known argument is identified by {@code identifier}.
         */
        public Object getArgument(String identifier)
                throws NoSuchElementException
        {
            List<Object> values = getArgumentValues(identifier);

            if (values.size() == 0) {
                return null;
            }

            return values.get(0);
        }

        /**
         * Returns the values of the given argument.
         * @param identifier The arguments identifier (see {@link Argument#getIdentifier()}) whose values to retrieve.
         * @return The argument values. If the argument was not given in the input, an empty list is returned.
         * @throws NoSuchElementException If no known argument is identified by {@code identifier}.
         */
        public List<Object> getArgumentValues(String identifier)
            throws NoSuchElementException
        {
            List<Object> values = argumentValues.get(identifier);

            if (values == null) {
                throw new NoSuchElementException("Unknown argument " + identifier);
            }

            return values;
        }

        /**
         * Returns the value of the given argument. If the argument is variadic, the first value is returned.
         * @param argument The argument whose value to retrieve.
         * @return The arguments value. If the argument was not given in the input, null is returned.
         * @throws NoSuchElementException If the given argument was not known at the time the input was parsed.
         */
        public Object getArgument(Argument argument)
                throws NoSuchElementException
        {
            return getArgumentValues(argument.getIdentifier());
        }

        /**
         * Returns the values of the given argument.
         * @param argument The arguments whose values to retrieve.
         * @return The argument values. If the argument was not given in the input, an empty list is returned.
         * @throws NoSuchElementException If the given argument was not known at the time the input was parsed.
         */
        public List<Object> getArgumentValues(Argument argument)
                throws NoSuchElementException
        {
            return getArgumentValues(argument.getIdentifier());
        }
        
        /**
         * Returns whether the given flag was set.
         * @param flag The flags primary identifier (see {@link Flag#getPrimaryIdentifier()}) whose value to retrieve.
         * @return Whether the given flag was set.
         * @throws NoSuchElementException If a flag with the given primary identifier was not known at the time the input
         * was parsed.
         */
        public boolean isFlagSet(String flag)
            throws NoSuchElementException
        {
            Boolean is = flagValues.get(flag);

            if (is == null) {
                throw new NoSuchElementException("Unknown flag " + flag);
            }

            return is;
        }

        /**
         * Returns whether the given flag was set.
         * @return Whether the given flag was set.
         * @throws NoSuchElementException If the given flag was not known at the time the input was parsed.
         */
        public boolean isFlagSet(Flag flag)
            throws NoSuchElementException
        {
            return isFlagSet(flag.getPrimaryIdentifier());
        }
        
        /**
         * Returns all flags and their values known to this input. The returned map is immutable.
         * @return An immutable map of the flags and their values known to this input.
         */
        public Map<String,Boolean> flagValues()
        {
            return flagValues;
        }

        /**
         * Returns all options and their values known to this input. The returned map is immutable.
         * @return An immutable map of the options and their values known to this input.
         */
        public Map<String,List<Object>> optionValues()
        {
            return optionValues;
        }

        /**
         * Returns all options and their values known to this input. The returned map is immutable.
         * @return An immutable map of the options and their values known to this input.
         */
        public Map<String,List<Object>> argumentValues()
        {
            return argumentValues;
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
