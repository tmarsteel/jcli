# Configuring the parser with XML

### Preface
This document uses the phrases MUST, MUST NOT, SHOULD, SHOULD NOT and MAY in the same way they are used in RFC documents.


The XML configuration is parsed by the class `com.tmarsteel.jcli.validator.configuration.XMLValidatorConfigurator`.
Before actually parsing and applying a configuration to an instance of `com.tmarsteel.jcli.validation.Validator` several
options can be set to extend the functionality.

---

The root-element of the configuration xml MUST be a `<cli>`-Tag. It includes all definitions of flags, options,
arguments and their value constraints. A quick example:

```xml
<xml version="1.0">
<cli>
    <flag identifier="verbose">
        <alias>v</alias>
    </flag>

    <argument identifier="input" index="0">
        <filter type="file">
            <permissions>READ</permissions>
            <existence>MUST_EXIST</existence>
        </filter>
    </argument>
</cli>
```

These CLI-Calls are equal:  
`program -v : foo.txt`
`program -verbose : foo.txt`
*Note: the colon between the flag and the argument is optional here. See [Ambiguous Arguments](../gotchas.md)*.

## Flags

Flags MUST be configured using the `<flag>`-tag. The `identifier` attribute MUST be set.  
**Note:** the content of the identifier-attribute is the string that is used to identify the flag within the
configuration and when accessing its value after parsing input.

Inside that tag you MAY specify as many aliases (for abbreviation or compatibility) as you wish. The identifier as well
as all the aliases will be recognized.
For example:

```xml
<flag identifier="verbose">
    <alias>v</alias>
</flag>
```

## Options

Options MUST be configured using the `<option>`-tag. The `identifier` attribute MUST be set.  
**Note:** the content of the identifier-attribute is the string that is used to identify the option within the
configuration and when accessing its value after parsing input.

Inside that tag you MAY specify as many aliases (for abbreviation or compatibility) as you wish. The identifier as well
as all the aliases will be recognized.

One `<default>` tag MAY be given. This will be the value used when the option is not set. If a `<default>` tag is not
given and the option is given in parsed input the value of the option will be `null`. To enforce an option set
`required="true"` on the `<option>` tag.

One `<filter>` tag MAY be given. It hints to the validator how to validate input given to that option. See below for
filters.

For example:

```xml
<option identifier="output">
    <alias>out</alias>
    <alias>o</alias>
	<default>out.txt</output>
</option>
```

### Multiple values

In case an option can be specified multiple times (e.g. multiple inputs), the option MAY be marked as a collection by
setting the `collection` attribute to `true`.

The validator will then pass each single value for that option through the filter and collect the filtered objects.

To access all of the values, use `Validator.ValidatedInput#getOptionValues`

The order of the parsed objects in the returned list reflects the order in which the single values were given in the
original input.

Example:

```xml
<option identifier="input" collection="true">
```

`program -input foo.txt -input bar.txt`

```java
assertEquals("foo.txt", validatedInput.getOptionValues("input").get(0));
assertEquals("bar.txt", validatedInput.getOptionValues("input").get(1));
```

## Arguments

Arguments MUST be configured using the `<argument>`-tag. Both the `identifier` and the `index` attribute MUST be set. 

You MAY set the `required`-attribute to `true`. This will cause the parser to throw an error if no input could be mapped
to this argument.

You MAY set the `variadic`-attribute to `true`. This will cause that all arguments from the index of this argument on are
treated as values to this argument.
There MUST NOT be more than one variadic argument in the same validator configuration. If there is a variadic
argument in the configuration, its index MUST be the greatest among all defined arguments.

The content of the `identifier`-attribute is the string that is used to identify the argument within the configuration
and when accessing its value after parsing input.

The content of the `index`-attriute MUST be an integer number greater or equal to 0. `Validator`s will use that index
to access the argument values from `Input`. 1st argument: index 0, 2nd argument: index 1, ...

You MAY specify a `<default>`-tag. This will be the value used when the argument is not set.

You MAY specify one `<filter>`-tag that will hint to the validator how to validate input given to that argument.
See below for filters.

Example:

```xml
<argument identifier="format" index="0" />
<argument identifier="input" index="1" />
```

`program -v : someFormat input.txt`


```xml
<argument identifier="inputs" index="0" variadic="true" />
```
`program -v : input1.txt input2.txt input3.txt`

*Note: the colon between the flag and the argument is optional here. See [Ambiguous Arguments](../gotchas.md)*.

## Filters

One `<filter>`-tag MAY be specified per option or argument. It tells the validator how to parse the values for these
options/arguments, e.g. integers or files. There are seven predefined filters (see below). You can create and use your
own filters; more on that later.

The strings parsed from the input are passed to the filters. They return objects already specific to what their
validation is, e.g. `java.lang.Long` or `java.io.File`.

The `type` attribute MUST be set on all `<filter>` tags to identify what filter is used.


### Predefined filters

Values of the `type` attribute map to filter classes. The predefined ones are listed below.
How the types can be modified or extended is explained in the *Custom filters* section. 

#### Number filters: integer, decimal, big-integer and big-decimal

These filters try to parse the value as integer or decimal. You MAY specify `<min>`, `<max>` and a `<radix>` tag for the integer filters; their functionality should be self explanatory.

|Filter            |Value of type-attribute |radix supported? |Return-Type          |
|------------------|------------------------|-----------------|---------------------|
|Integer-Filter    |integer                 |yes              |java.lang.Long       |
|Decimal-Filter    |decimal                 |no, radix = 10   |java.lang.Double     |
|BigInteger-Filter |big-integer             |yes              |java.math.BigInteger |
|BigDecimal-Filter |big-decimal             |no, radix = 10   |java.math.BigDecimal |

Example:

```xml
<filter type="integer">
    <min>0</min>
    <radix>16</radix>
</filter>
```

#### File Filter and Path Filter

The file filter returns an instance of `java.io.File`. Whether or not a file will be accepted can be configured. These criteria are applicable:

* existence: the file has to exist, the file may not exist or existence is irrelevant
* permissions: the has to be
  - readable
  - writable
  - executable
* it has to be a file/directory
* a specific extension is required

To define the required existence-state use an `<existence>` tag. Its content has to be one of `com.wisper.cli.filter.FileFilter.EXISTENCE`s values:

* MUST_EXIST
* MUST_NOT_EXIST
* IRRELEVANT

To define the required permissions use a `<permissions>` tag. Its content has to be one of `com.wisper.cli.filter.FileFilter.PERMISSION`s values:

* READ
* WRITE
* EXECUTE
* READ_WRITE
* READ_EXECUTE
* READ_WRITE_EXECUTE
* IRRELEVANT

To define whether the file has to be a file or a directory use a `<type>` tag. Its content has to be one of `com.wisper.cli.filter.FileFilter.TYPE`s values:

* FILE
* DIRECTORY
* IRRELEVANT

To define the required extension use an `<extension>` tag.

As an example a filter will serve that requires a readable xml file.

```xml
<filter type="file">
    <extension>xml</extension>
    <type>FILE</type>
    <existence>MUST_EXIST</existence>
    <permissions>READ</permissions>
</filter>
```

#### Set Filter

The set filter allows only values from a given set; case-sensitivity is off by default but can be turned on by the `caseSensitive`-attribute.

Specify the different possibilities with <value> tags. The set filter will always return values from the configuration and never the actual values. For Example:

```xml
<filter type="set" caseSensitive="false">
    <value>AES128</value>
    <value>AES192</value>
    <value>AES256</value>
    <value>SERPENT</value>
    <value>THREEFISH</value>
</filter>
```

This filter is case insensitive. Given the input `ThReEFiSH` it will return `THREEFISH`.

##### Path Filter
The path filter (`type="path"`) wraps a `FileFilter` but returns a `java.nio.Path` instead of a `java.io.File`.

#### Regex Filter

The regex filter allows only values matching the given regex. It will return the entire input if matched or a group, if specified by the `returnGroup`-attribute. Specify the regex using a `<regex>` tag. For example:

```xml
<filter type="regex" returnGroup="1">
    <regex>(\d+)KG</regex>
</filter>
```

Given the input "15KG" this filter will return the string "15".

### Pattern Filter (Meta Regex Filter)

This filter parses the input string as a regular expression (wraps `Pattern#compile`). This comes in handy e.g. when
offering filter functionality.
This filter accepts no parameters. It returns the parsed regex as a `java.util.regex.Pattern`.

Example:

```xml
<filter type="pattern" />
```

## Rules

Rules are optional. They MUST be configured using a `<rule>` tag.  
Rules are validated after all input has been parsed. They can reject that input in case they are not met. For example,
rules can enforce an option to be given or that two options cannot be set at the same time.

The `type`-attribute MUST be set on all `<rule>` tags to specified the rule to use. You can implement custom rules,
more on that later.

### Predefined rules

To refuse all flags and options that are were not defined in the configuration, predefined rules have to be added
programmatically:

```java
Validator inputValidator = new Validator();
inputValidator.add(Rule.ONLY_KNOWN_OPTIONS);
inputValidator.add(Rule.ONLY_KNOWN_FLAGS);
```

Almost any other combination can be configured in the XML configuration. There are six pre-defined types of rules.

The error messages produced by the rules included in the library are not user-friendly in
most cases. For every rule included in the library you can specify an `<error>` tag that will be passed as the error
message in case of failure.

**Note:** rules SHOULD be placed at the end of the XML file. Referring to a not yet parsed option/flag in a rule
may throw an error.

#### Option rule: option-set

This rule requires that one or more options/flags are set. Specify these options/flags with the `<option>`-tag. The content of these `<option>` tags has to be equal to the `identifier` attribute of the referenced option/flag; **aliases do not work!**

Example:

```xml
<rule type="option-set">
    <option>input</option>
</rule>
```

#### Option rule: option-xor

Basically, this rule could be realised with a xor-rule and nested option-set rules. However, the error message produced by an option-xor rule is much more user-friendly. This rule is intended if options and flags cannot be set at the same time. For example a verbose- and a quiet-flag at the same time do not make much sense:

```xml
<flag identifier="verbose" />
<flag identifier="quiet" />

<rule type="option-xor">
    <option>verbose</option>
    <option>quiet</option>
</rule>
```

**Note:** you can also refer to arguments here.

#### Logical rule: and, or, xor and not

These 4 rules connect other rules with the respective logical operator. For example: If a logfile should be written (log flag), then a logfile has to be specified (logfile option).

```xml
<flag identifier="log">
    <alias>l</alias>
</flag>

<option identifier="logfile">
    <alias>lf</alias>
    <filter type="file">
        <permissions>WRITE</permissions>
    </filter>
</option>

<rule type="xor">
    <rule type="not">
        <rule type="option-set">
            <option>log</option>
        </rule>
        <rule type="option-set">
            <option>logfile</option>
        </rule>
    </rule>
    <rule type="option-set">
        <option>log</option>
        <option>logfile</option>
    </rule>
	<error>When specifying the log flag, the logfile option becomes mandatory.</error>
</rule>
```

## Custom Filters

To get a custom filter to work with `XMLValidatorConfigurator` you have to define a filter type; this can be the name
of your filter class. The instance of `XMLValidatorConfigurator` then needs to be notified about the filter type and
how to parse it; this is achieved by invoking `XMLValidatorConfigurator#setFilterType`; you MAY overwrite the predefined
filters types.

### Creating the XML parser

`XMLValidatorConfigurator#setFilterType` requires an instance of `FilterParser`. Unless you wish to support multiple
configuration formats the parsing method SHOULD be defined in the filter class. IT can then e referenced using a lambda
expression (see example below).

### Creating the filter
A separate class SHOULD be used for every filter. It has to implement the interface `com.tmarsteel.jcli.filter.Filter`.

### Example

```java
/**
 * Filters input by resolving the given string value to a {@link Customer} entity by its ID.
 */
class CustomerFilter implements Filter {
    public static CustomerFilter parseToInstance(XMLValidatorConfigurator, org.w3c.dom.Node configurationNode) {
        // any custom parsing logic on configurationNode
        // extract information about the filter here
        return new CustomerFilter();
    }

    @Override
    public Object parse(String value) throws ValidationException {
        long id;
        try {
            id = Long.parseLong(value);
        } catch (NumberFormatException ex) {
            throw new ValidationException("Customer-ID ist not numeric");
        }

        if (id <= 0) {
            throw new ValidationException("Customer-ID must be greater than 0");
        }

        return findCustomerById(id);
    }
}

// in CLI
XMLValidatorConfigurator configurator;

// if you do not have custom parsing logic, you SHOULD use this:
configurator.setFilterType("customer", (context, node) -> new CustomerFilter());

// if you have little parsing logic, you SHOULD use a static getInstance method:
configurator.setFilterType("customer", CustomerFilter::parseToInstance);

// if you have a lot of parsing logic, you SHOULD create a separate parsing class
configurator.setFilterType("customer", new CustomerFilterParser());

// quick and dirty approach, it SHOULD NOT be used
configurator.setFilterType("customer", (context, node) -> str -> findCustomerById(Long.parseLong(str)));
```

## Custom Rules

Custom rules work the same as custom filters do, for the greatest part:

* Create a class that implements `com.tmarsteel.jcli.rule.Rule`
* If necessary, define your parsing logic, preferable as a static method of the rule class
* Link the rule parser to the `XMLValidatorConfigurator` by invoking `#setRuleType`

The difference is: Rules can have nested rules (the same way the predefined `and`, `or` and `xor` rules do). Those
MUST NOT be parsed by the specific rule parser; the parsing of nested rules MUST be delegated back to the
`XMLValidatorConfigurator`:

```java
class CustomRuleParser implements RuleParser<CustomRule>  {
    @Override
    public CustomRule parse(XMLValidatorConfigurator context, Node node, RuleParser<Rule> subParser)
                throws MisconfigurationException, ParseException
    {
        // if, during the parsing of node, a <rule> tag is encountered, invoke subParser#parse:
        Node nestedRuleNode;
        Rule nestedRule = subParser.parse(context, nestedRuleNode, subParser);

        return new CustomRule(nestedRule);
    }
}
```