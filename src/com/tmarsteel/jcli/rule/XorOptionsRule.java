package com.tmarsteel.jcli.rule;

import com.tmarsteel.jcli.Flag;
import com.tmarsteel.jcli.Identifiable;
import com.tmarsteel.jcli.validator.Validator;
import com.tmarsteel.jcli.validator.RuleNotMetException;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Accepts input if exactly one of the given options/flags is set/specified. Use
 * this class instead of {@link XorRule} for a more user-friendly error message.
 * @author tmarsteel
 */
public class XorOptionsRule extends BaseRule
{
    protected String[] options;
    
    /**
     * @param options The options/flags to connect.
     */
    public XorOptionsRule(Identifiable... options)
    {
        this.options = new String[options.length];
        
        for (int i = 0;i < options.length;i++)
        {
            this.options[i] = options[i].getPrimaryIdentifier();
        }
    }
    
    /**
     * Node structure: <br>
     * List all the identifiable flags, options and arguments in &lt;option&gt;
     * subtags.
     * @param node 
     */
    public XorOptionsRule(Node node)
    {
        NodeList children = node.getChildNodes();
        
        List<String> parsedOptions = new ArrayList<>();
        
        for (int i = 0;i < children.getLength();i++)
        {
            Node cNode = children.item(i);
            
            switch (cNode.getNodeName())
            {
                case "#text":
                    continue;
                case "option":
                    parsedOptions.add(cNode.getTextContent().trim());
                    break;
            }
        }
        
        parsedOptions.toArray(this.options = new String[parsedOptions.size()]);
    }
    
    @Override
    public void validate(Validator intent, Validator.ValidatedInput params)
        throws RuleNotMetException
    {
        boolean prevSet = false;
        boolean anySet = false;
        
        for (String o:options)
        {
            if (params.isFlagSet(o) || params.getOption(o) != null)
            {
                anySet = true;
                
                if (prevSet)
                {
                    throw new RuleNotMetException(
                        errorMessage != null ? errorMessage :
                        prevSet + " and " + o + " cannot be set at the same time."
                    );
                }
                prevSet = true;
            }
            else
            {
                prevSet = false;
            }
        }
        
        if (!anySet)
        {
            throw new RuleNotMetException(
                errorMessage != null ? errorMessage :
                "Either one of these options/flags must be set."
            );
        }
    }
    
    @Override
    public String toString()
    {
        String str = "(Exactly one of these can be set at the same time:\n";
        for (String o:options)
        {
            str += o + "\n";
        }
        return str + ')';
    }
}
