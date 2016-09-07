package com.tmarsteel.jcli.validation.configuration.xml;

import com.tmarsteel.jcli.ParseException;
import com.tmarsteel.jcli.rule.AndRule;
import com.tmarsteel.jcli.rule.CombinedRule;
import com.tmarsteel.jcli.rule.OptionSetRule;
import com.tmarsteel.jcli.rule.Rule;
import com.tmarsteel.jcli.validation.RuleNotMetException;
import com.tmarsteel.jcli.validation.Validator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class RuleParsingUtilTest
{
    NodeList testNodes;
    XMLValidatorConfigurator dummyContext = mock(XMLValidatorConfigurator.class);
    RuleParser<Rule> dummySubParser = mock(RuleParser.class);

    @Before
    public void loadTestXML()
            throws ParserConfigurationException, SAXException, IOException
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(false);
        dbf.setIgnoringComments(true);
        dbf.setIgnoringElementContentWhitespace(true);

        DocumentBuilder builder = dbf.newDocumentBuilder();

        Document testDocument = builder.parse(this.getClass().getResourceAsStream("rules.xml"));

        testNodes = testDocument.getElementsByTagName("rule");
    }

    @Test
    public void parseCombinedShouldFindAllSubRulesAndDelegateToSubParser()
        throws ParseException
    {
        RuleParser<MockCombinedRule> parser = RuleParsingUtil.combinedRuleParser(MockCombinedRule.class);

        MockCombinedRule parsedRule = parser.parse(dummyContext, testNodes.item(0), dummySubParser);

        verify(dummySubParser).parse(same(dummyContext), same(testNodes.item(1)), any());
        verify(dummySubParser).parse(same(dummyContext), same(testNodes.item(2)), any());
        Assert.assertEquals("Not all / too many rules given to combined rule", 2, parsedRule.rules.length);
    }

    static class MockCombinedRule extends CombinedRule {

        public Rule[] rules;

        public MockCombinedRule(Rule... rules) {
            this.rules = rules;
        }

        @Override
        public void validate(Validator intent, Validator.ValidatedInput params) throws RuleNotMetException
        {

        }
    }
}
