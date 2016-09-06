package com.tmarsteel.jcli.validation.configuration.xml;

import com.tmarsteel.jcli.ParseException;
import com.tmarsteel.jcli.validation.MisconfigurationException;
import org.w3c.dom.Node;

/**
 * Parses {@link Node} objects to R objects. Intended use: abstract the XML parsing done by XMLValidatorConfigurator;
 * R will be a {@link com.tmarsteel.jcli.filter.Filter} or a {@link com.tmarsteel.jcli.rule.Rule}.
 */
public interface NodeParser<R> {

    /**
     * Parses the given {@link Node} in the {@code context} of the given {@link XMLValidatorConfigurator} to an
     * object of type R.
     * @param context The context in which the resulting object is to be used.
     * @param node The XML node to be parsed.
     * @return The parsed object.
     * @throws ParseException If the node is syntactically incorrect.
     * @throws MisconfigurationException If the node is semantically incorrect.
     */
    public R parse(XMLValidatorConfigurator context, Node node)
            throws MisconfigurationException, ParseException;
}
