package com.tmarsteel.jcli;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

/**
 * Represents a set of input parameters.
 * @author tmarsteel
 */
public class Input
{
    protected List<String> flags = new ArrayList<>();
    protected HashMap<String,String> options = new HashMap<>();
    protected List<String> arguments = new ArrayList<>();
    protected CLIParser intent;
    
    public Input()
    {
        this(null);
    }
    
    public Input(CLIParser intent)
    {
        this.intent = intent;
    }
    
    public Input(Environment env, String[] args)
        throws ParseException
    {
        this();
        add(env, args);
    }
    
    public Input(CLIParser intent, Environment env, String[] args)
        throws ParseException
    {
        this(intent);
        add(env, args);
    }
    
    public Input(Environment env, String argline)
        throws ParseException
    {
        this();
        add(env, argline);
    }
    
    public Input(CLIParser intent, Environment env, String argline)
        throws ParseException
    {
        this(intent);
        add(env, argline);
    }
    
    /**
     * Returns whether the given flag was within the input.
     * @return whether the given flag was within the input.
     */
    public boolean containsFlag(Option flag)
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
     * Returns the value of the given option or <code>null</code> if not contained
     * in the input.
     * @return the value of the given option or <code>null</code> if not contained
     * in the input.
     */
    public String getOption(Option option)
    {
        final Iterator<Entry<String,String>> it = options.entrySet().iterator();
        while (it.hasNext())
        {
            final Entry<String,String> curE = it.next();
            if (option.isIdentifiedBy(curE.getKey()))
            {
                return curE.getValue();
            }
        }
        return null;
    }
    
    public String getArgument(int index)
    {
        return arguments.size() > index? arguments.get(index) : null;
    }
    
    /**
     * Adds the flags and options contained in <code>args</code> to this input
     * accodring to <code>env</code>.
     * @param env The environment settings to follow when parsing <code>args</code>
     * @param paramStr A string containing the input parameters, e.g.
     *  <code>"-v --input foo.txt"</code>
     * @throws IllegalArgumentException If there is an odd parameter.
     * @throws ParseException If a quoted parameter has no ending <code>"</code>.
     */
    public void add(Environment env, String paramStr)
        throws ParseException
    {  
        List<String> params = new ArrayList<String>();
        
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
     * Adds the flags and options contained in <code>args</code> to this input
     * accodring to <code>env</code>.
     * @param env The environment settings to follow when parsing <code>args</code>
     * @param args The input parameters to parse.
     * @throws IllegalArgumentException If there is an odd parameter.
     */
    public void add(Environment env, String[] args)
        throws ParseException
    {
        // if the recognicion chars for flags are longer than for options, check
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

            if (type == 1)
            {
                if (argumentsStarted)
                {
                    throw new IllegalArgumentException(
                        "Flags and options may only be used before the first argument.");
                }
                addFlag(args[i], env);
            }
            else if (type == 2)
            {
                if (argumentsStarted)
                {
                    throw new IllegalArgumentException(
                        "Flags and options may only be used before the first argument.");
                }
                // search for the value
                if (args.length > i + 1)
                { // there is another parameter
                    setOption(args[i], args[++i], env);
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
    
    private void addFlag(String param, Environment env)
    {
        final String str = param.substring(env.getFlagMarker().length());
        if (!flags.contains(str))
        {
            flags.add(str);
        }
    }
    
    private void setOption(String optionName, String content, Environment env)
    {
        options.put(optionName.substring(env.getOptionMarker().length()), content);
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
        CLIParser intent, String param, Environment env,
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
