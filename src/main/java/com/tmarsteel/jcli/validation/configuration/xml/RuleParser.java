package com.tmarsteel.jcli.validation.configuration.xml;

import com.tmarsteel.jcli.ParseException;
import com.tmarsteel.jcli.rule.Rule;
import com.tmarsteel.jcli.validation.MisconfigurationException;
import org.w3c.dom.Node;

/**
 * Parses {@link Node} objects to {@link Rule} objects. Intended use: abstract the XML parsing done by
 * XMLValidatorConfigurator
 */
public interface RuleParser<R extends Rule> {

    /**
     * Parses the given {@link Node} in the {@code context} of the given {@link XMLValidatorConfigurator} to
     * a {@link Rule}
     * @param context The context in which the resulting filter is to be used.
     * @param node The XML node to be parsed.
     * @param subParser Can be utilized to parse nested rules
     * @return The parsed object.
     * @throws ParseException If the node is syntactically incorrect.
     * @throws MisconfigurationException If the node is semantically incorrect.
     */
    public R parse(XMLValidatorConfigurator context, Node node, RuleParser<Rule> subParser)
            throws MisconfigurationException, ParseException;
}
