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
package com.tmarsteel.jcli;

import com.tmarsteel.jcli.validation.Validator;

import java.util.*;
import java.util.Map.Entry;

/**
 * Parses cli commands (entire command line strings or String[]s) and provides
 * facilities to access these.
 * @author Tobias Marstaller
 */
public class Input
{
    private Set<String> flags = new HashSet<>();
    private Map<String,List<String>> options = new HashMap<>();
    private List<String> arguments = new ArrayList<>();
    protected Validator intent;
    
    public Input()
    {
        this(null);
    }
    
    /**
     * @param intent A validator which known options and flags to use in the case
     * the flags and options cannot be distinguished by prefix (<code>flagMarker</code>
     * and <code>optionMarker</code> are equal).
     */
    public Input(Validator intent)
    {
        this.intent = intent;
    }
    
    /**
     * Creates a new instance and {@link #add(com.tmarsteel.jcli.Environment, java.lang.String[])}s
     * the <code>args</code> to it. <br>
     * <b>Note:</b> If <code>env.flagMarker</code> equals <code>env.optionMarker</code>
     * a {@link ParseException} will be thrown. To avoid this, use
     * {@link #Input(com.tmarsteel.jcli.validation.Validator, com.tmarsteel.jcli.Environment, java.lang.String[])}.
     * @param env The environment to parse <code>args</code> in
     * @param args Input to parse.
     * @throws ParseException 
     */
    public Input(Environment env, String[] args)
        throws ParseException
    {
        this();
        add(env, args);
    }
    
    /**
     * Creates a new instance and {@link #add(com.tmarsteel.jcli.Environment, java.lang.String[])}s
     * the <code>args</code> to it.
     * @param intent A validator which known options and flags to use in the case
     * the flags and options cannot be distinguished by prefix (<code>flagMarker</code>
     * and <code>optionMarker</code> are equal).
     * @param env The environment to parse <code>args</code> in
     * @param args Input to parse.
     * @throws ParseException 
     */
    public Input(Validator intent, Environment env, String[] args)
        throws ParseException
    {
        this(intent);
        add(env, args);
    }
    
    /**
     * Creates a new instance and {@link #add(com.tmarsteel.jcli.Environment, java.lang.String)}s
     * the <code>args</code> to it.
     * <b>Note:</b> If <code>env.flagMarker</code> equals <code>env.optionMarker</code>
     * a {@link ParseException} will be thrown. To avoid this, use
     * {@link #Input(com.tmarsteel.jcli.validation.Validator, com.tmarsteel.jcli.Environment, java.lang.String)}.
     * @param env The environment to parse <code>args</code> in
     * @param argline Input to parse.
     * @throws ParseException 
     */
    public Input(Environment env, String argline)
        throws ParseException
    {
        this();
        add(env, argline);
    }
    
    /**
     * Creates a new instance and {@link #add(com.tmarsteel.jcli.Environment, java.lang.String)}s
     * the <code>args</code> to it.
     * @param intent A validator which known options and flags to use in the case
     * the flags and options cannot be distinguished by prefix (<code>flagMarker</code>
     * and <code>optionMarker</code> are equal).
     * @param env The environment to parse <code>args</code> in
     * @param argline Input to parse.
     * @throws ParseException 
     */
    public Input(Validator intent, Environment env, String argline)
        throws ParseException
    {
        this(intent);
        add(env, argline);
    }
    
    /**
     * Returns whether the given flag was within the input.
     * @return whether the given flag was within the input.
     */
    public boolean containsFlag(Flag flag)
    {
        final Iterator<String> it = flags.iterator();
        while (it.hasNext())
        {
            if (flag.isIdentifiedBy(it.next()))
            {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Returns the values of the given option. The returned list is empty if
     * the given option was not contained in the input.
     * @return the values of the given option.
     */
    public List<String> getOption(Option option)
    {
        final Iterator<Entry<String,List<String>>> it = options.entrySet().iterator();
        while (it.hasNext())
        {
            final Entry<String,List<String>> curE = it.next();
            if (option.isIdentifiedBy(curE.getKey()))
            {
                return curE.getValue();
            }
        }
        
        return new ArrayList<>();
    }
    
    /**
     * Returns the string value of the argument at index <code>index</code>
     * or null if no value is given at that index.
     * @param index The index of the argument to return
     * @return The argument value or null if the argument was not specified.
     */
    public String getArgument(int index)
    {
        return arguments.size() > index? arguments.get(index) : null;
    }
    
    /**
     * Adds the flags and options contained in <code>paramStr</code> to this input; tells flags and options apart
     * according to <code>env</code>.
     * @param env The environment settings to follow when parsing <code>args</code>
     * @param paramStr A string containing the input parameters, e.g.
     *  <code>"-v --input foo.txt"</code>
     * @throws IllegalArgumentException If there is an odd parameter.
     * @throws ParseException If a quoted parameter has no ending <code>"</code>.
     */
    public void add(Environment env, String paramStr)
        throws ParseException
    {  
        List<String> params = new ArrayList<>();
        
        boolean inString = false;
        boolean escape = false;
        String curStr = "";
        final char[] argline = paramStr.toCharArray();
        for (char c : argline)
        {
            if (escape)
            {
                escape = false;
                curStr += c;
            }
            else
            {
                if ((c == ' ' || c == '\t' || c == '\n') && !inString)
                {
                    if (!curStr.isEmpty())
                    {
                        params.add(curStr);
                        curStr = "";
                    }
                }
                else if (c == '"')
                {
                    if (!curStr.isEmpty())
                    {
                        params.add(curStr);
                        curStr = "";
                    }
                    inString = !inString;
                }
                else if (c == env.getEscapeChar())
                {
                    escape = true;
                }
                else
                {
                    curStr += c;
                }
            }
        }
        
        if (inString)
        {
            throw new ParseException("Unclosed quoted argument");
        }
        
        if (!curStr.isEmpty())
        {
            params.add(curStr);
        }
        
        add(env, params.toArray(new String[params.size()]));
    }
    
    /**
     * Adds the flags and options contained in <code>args</code> to this input; tells flags and options apart
     * according to <code>env</code>.
     * @param env The environment settings to follow when parsing <code>args</code>
     * @param args The input parameters to parse.
     * @throws IllegalArgumentException If there is an odd parameter.
     */
    public void add(Environment env, String[] args)
        throws ParseException
    {
        // if the recognition chars for flags are longer than for options, check
        // for options first
        final boolean flagsOverOptions =
            env.getFlagMarker().length() > env.getOptionMarker().length();
        final boolean flagsOptionsDistinguishable =
            !env.getFlagMarker().equals(env.getOptionMarker());
        boolean argumentsStarted = false;
        
        for (int i = 0;i < args.length;i++)
        {
            final byte type = getType(flagsOptionsDistinguishable,
                intent, args[i], env, flagsOverOptions);

            // the first parameter that is only a colon serves as a workaround for #1
            if (args[i].equals(":") && !argumentsStarted) {
                argumentsStarted = true;
            }
            else if (argumentsStarted) {
                // once an argument has been seen, everything that follows has to be an argument, too
                arguments.add(args[i]);
            }
            else if (type == 1)
            {
                addFlag(args[i], env);
            }
            else if (type == 2)
            {
                // search for the value
                if (args.length > i + 1)
                { // there is another parameter
                    
                    String optionName = args[i].substring(env.getOptionMarker().length());
                    
                    // add the value
                    if (!options.containsKey(optionName))
                    {
                        options.put(optionName, new ArrayList<>());
                    }
                    options.get(optionName).add(args[++i]);
                }
                else
                { // we have reached the end...
                    // might this be a flag?
                    if (isFlag(args[i], env))
                    {
                        addFlag(args[i], env);
                    }
                    else
                    {
                        throw new IllegalArgumentException("Missing value for option " + args[i]);
                    }
                }
            }
            else
            {
                // Argument
                arguments.add(args[i]);
                argumentsStarted = true;
            }
        }
    }
    
    public Set<String> flags()
    {
        return this.flags;
    }
    
    public Map<String,List<String>> options()
    {
        return this.options;
    }
    
    public List<String> arguments()
    {
        return this.arguments;
    }
    
    private void addFlag(String param, Environment env)
    {
        final String str = param.substring(env.getFlagMarker().length());
        if (!flags.contains(str))
        {
            flags.add(str);
        }
    }
    
    private static boolean isFlag(String param, Environment env)
    {
        return param.startsWith(env.getFlagMarker());
    }
    
    private static boolean isOption(String param, Environment env)
    {
        return param.startsWith(env.getOptionMarker());
    }
    
    /**
     * @return <code>1</code> for flags, <code>2</code> for options and
     * <code>0</code> for neither.
     */
    private static byte getType(boolean flagsOptionsDistinguishable,
        Validator intent, String param, Environment env,
        boolean flagsOverOptions)
        throws ParseException
    {
        if (flagsOptionsDistinguishable)
        {
            if (flagsOverOptions)
            {
                if (isFlag(param, env))
                {
                    return 1;
                }
                else if (isOption(param, env))
                {
                    return 2;
                }
            }
            else
            {
                if (isOption(param, env))
                {
                    return 2;
                }
                else if (isFlag(param, env))
                {
                    return 1;
                }
            }
            return 0;
        }
        else
        {
            if (intent == null)
            {
                throw new ParseException(
                    "Cannot distinguish flags and options. Specify an intent to solve this.");
            }
            // flags and options cannot be distinguished. The type of the
            // input has to be found out by checking its contents against
            // the known list of flags and options
            if (param.startsWith(env.getFlagMarker()))
            {
                String _param = param.substring(env.getFlagMarker().length());
                if (intent.knowsOption(_param))
                {
                    return 2;
                }
                else
                {
                    // the marker is there and correct but the parameter is not
                    // an option. So, whether or not the intent knows the parameter
                    // it is considered a flag.
                    return 1;
                }
            }
            else
            {
                return 0;
            }
        }
    }
}
