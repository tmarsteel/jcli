package com.tmarsteel.jcli.validation.configuration;

import com.tmarsteel.jcli.validation.MisconfigurationException;
import com.tmarsteel.jcli.validation.MisconfigurationException;
import com.tmarsteel.jcli.validation.Validator;

public interface ValidatorConfigurator
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
