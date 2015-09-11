package com.tmarsteel.jcli;

import com.tmarsteel.jcli.Validator;
import com.tmarsteel.jcli.validator.MisconfigurationException;

public interface ParserBuilder
{    
    /**
     * Applies the preconfigurations in this builder to the given {@link Validator}
     * @param p The parser to configure
     * @throws MisconfigurationException If the preconfiguration of this builder
     * cannot be applied because it is invalid/flawed.
     */
    public void configure(Validator p)
        throws MisconfigurationException;
}
