package com.tmarsteel.jcli;

import com.tmarsteel.jcli.CLIParser;
import com.tmarsteel.jcli.parser.MisconfigurationException;

public interface ParserBuilder
{    
    /**
     * Applies the preconfigurations in this builder to the given {@link CLIParser}
     * @param p The parser to configure
     * @throws MisconfigurationException If the preconfiguration of this builder
     * cannot be applied because it is invalid/flawed.
     */
    public void configure(CLIParser p)
        throws MisconfigurationException;
}
