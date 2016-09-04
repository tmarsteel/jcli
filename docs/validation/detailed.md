# Configuring the parser with XML

### Preface
This document uses the phrases MUST, MUST NOT, SHOULD, SHOULD NOT and MAY in the same way they are used in RFC documents.


The XML configuration is parsed by the class `com.tmarsteel.jcli.validator.configuration.XMLValidatorConfigurator`. Before actually parsing and applying a configuration to an instance of `com.tmarsteel.jcli.validation.Validator` several options can be set to extend the functionality.

---

The root-element of the configuration xml MUST be a `<cli>`-Tag. It includes all definitions of flags, options and arguments and their possible values and validation information. A quick example:

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
`<executable> -v : foo.txt`  
`<executable> -verbose : foo.txt`  
*Note: the colon between the flag and the argument is optional here. See [Ambiguous Arguments](../gotchas.md)*.

## Flags

Flags MUST be configured using the `<flag>`-tag. The `identifier` attribute MUST be set.  
**Note:** the content of the identifier-attribute is the string that is used to identify the flag within the configuration and when accessing its value after parsing input.

Inside that tag you MAY specify as many aliases (for abbreviation or compatibility) as you wish. The identifier as well as all the aliases will be recognized.
For example:

```xml
<flag identifier="verbose">
    <alias>v</alias>
</flag>
```

## Options

Options MUST be configured using the `<option>`-tag. The `identifier` attribute MUST be set.  
**Note:** the content of the identifier-attribute is the string that is used to identify the option within the configuration and when accessing its value after parsing input.

Inside that tag you MAY specify as many aliases (for abbreviation or compatibility) as you wish. The identifier as well as all the aliases will be recognized.

One `<default>` tag MAY be given. This will be the value used when the option is not set. If a `<default>` tag is not given and the option is given in parsed input the value of the
option will be `null`. To enforce an option set `required="true"` on the `<option>` tag.

One `<filter>` tag MAY be given. It hints to the validator how to validate input given to that option. See below for filters.

for example:

```xml
<option identifier="output">
    <alias>out</alias>
    <alias>o</alias>
	<default>out.txt</output>
</option>
```

### Multiple values

In case an option can be specified multiple times (e.g. multiple inputs), the option MAY be marked as a collection by
setting the `collection` attribute to true.

The validator will then pass each single value for that option through the filter and collect the filtered objects.

To access all of the values, use `Validator.ValidatedInput#getOptionValues`

The order of the parsed objects in the returned array reflects the order in which the single values were given in the original input.

example:

```xml
<option identifier="input" collection="true">
```

## Arguments

Arguments MUST be configured using the `<argument>`-tag. Both the `identifier` and the `index` attribute MUST be set. 

You MAY set the `required`-attribute to true. This will cause the parser to throw an error if no input could be mapped to this argument.

You MAY set the `variadic`-attribute to true. This will cause that all arguments from the index of this argument on are
treated as values to this argument. `Input#getOption` will then behave like a multivalue option.
There MUST NOT be more than one variadic argument in the same validator configuration. If there is a variadic
argument in the configuration, its index MUST be the greatest among all defined arguments.

The content of the `identifier`-attribute is the string that is used to identify the argument within the configuration and when accessing its value after parsing input.

The content of the `index`-attriute MUST be an integer number greater or equal to 0. It defines what argument should be parsed as defined in the `<argument>` tag.

You MAY specify a `<default>`-tag. This will be the value used when the argument is not set.

You MAY specify one `<filter>`-tag that will hint to the validator how to validate input given to that argument. See below for filters.

Example:

```xml
<argument identifier="format" index="0" />
```

`<executable> -v : someFormat`  

```xml
<argument identifier="inputs" index="0" variadic="true" />
```
`<executable> -v : input1.txt input2.txt input3.txt`

*Note: the colon between the flag and the argument is optional here. See [Ambiguous Arguments](../gotchas.md)*.

## Filters

One `<filter>`-tag MAY be specified per option or argument. It tells the validator how to parse the values for these options/arguments, e.g. integers or files. There are seven predefined filters (see below). You can create and use your own filters; more on that later.

The strings parsed from the input are passed to the filters. They return objects already specific to what their
validation is, e.g. `java.lang.Long` or `java.io.File`.

Either the type attribute or the class attribute MUST be set on a `<filter>` tag.

##### The `class` attribute
If the `class` attribute is given, `XMLValidatorConfigurator` will try to load the class with the name given in the `class` attribute. If the loaded class implements `com.tmarsteel.jcli.filter.Filter` these constructors wil tried to be called in the order listed here:

1. (org.w3c.com.Node) The `<filter>` node will be passed to the constructor
2. ()

If an instance of the loaded class could be created it will be used as the filter. If not, a `MisconfigurationException` will be thrown. 

##### The `type` attribute

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

#### File Filter

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


#### Regex Filter

The regex filter allows only values matching the given regex. It will return the entire input if matched or a group, if specified by the `returnGroup`-attribute. Specify the regex using a `<regex>` tag. For example:

```xml
<filter type="regex" returnGroup="1">
    <regex>(\d+)KG</regex>
</filter>
```

Given the input "15KG" this filter will return the string "15".

### Pattern Filter (Meta Regex Filter)

This filter parses the input string as a regular expression. This comes in handy e.g. when offering filter functionality.
This filter accepts no parameters.  
It returns the parsed regex as a `java.util.regex.Pattern`.

Example:

```xml
<filter type="pattern" />
```

## Rules

Rules are optional. They MUST be configured using a `<rule>` tag.  
Rules are validated after all input has been parsed. They can reject that input in case they are not met. For example, rules can enforce an option to be given or that two options cannot be set at the same time.

Either the `type`-attribute or the `class` attribute MUST be set on a `<rule>`-tag.

##### The `class` attribute
If the `class` attribute is given, `XMLValidatorConfigurator` will try to load the class with the name given in the `class` attribute. If the loaded class implements `com.tmarsteel.jcli.rule.Rule` these constructors will tried to be called in the order listed here:

1. (org.w3c.com.Node) The `<filter>` node will be passed to the constructor
2. (com.tmarsteel.jcli.rule.Rule[]) Nested `<rule>` tags will be parsed and given to the rule.
2. ()

If an instance of the loaded class could be created it will be used as the rule. If not, a `MisconfigurationException` will be thrown. 

##### The `type` attribute

Values of the `type` attribute map to rule classes. The predefined ones are listed below.
How the types can be modified or extended is explained in the *Custom rules* section.

### Predefined rules

To refuse all flags and options that are not known by the validator, you will have to add the rules programmatically:

```java
Validator inputValidator = new Validator();
inputValidator.add(Rule.ONLY_KNOWN_OPTIONS);
inputValidator.add(Rule.ONLY_KNOWN_FLAGS);
```

Almost any other combination can be configured via XML. There are six pre-defined types of rules. 

The error messages produced by the rules included in the library are not user-friendly in
most cases. For every rule included in the library you can specify an `<error>` tag that will be passed as the error message in case of failure.

**Note:** rules should be at the end of the XML-file since referring to a not yet parsed option/flag will result in an error.

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

Custom filter can be implemented; First, you will have to create a class that extends `com.tmarsteel.jcli.filter.Filter`. It MUST have at least one of these constructor signatures:

* `(org.w3c.dom.Node)`
* `()`

The `(org.w3c.dom.Node)` constructor will be preferred. The argument is the `<filter>` tag found by the parser.

To use that class, either specify the **full classname** in the class attribute:

```xml
<filter class="com.mypackage.cli.filter.CustomFilterXY" />
```

or add your own `type` to the `XMLValidatorConfigurator`:

```java
XMLValidatorConfigurator configurator = ... ;
configurator.setFilterType("custom", com.mypackage.cli.filter.CustomFilterXY.class);
```

```xml
<filter type="custom" />
```

Every filter can implement its own XML settings via the `(org.w3c.dom.Node)` constructor:

```xml
<filter class="com.mypackage.cli.filter.CustomFilterXY" ignoreSth="true">
	<option>value</option>
</filter>
```

**Note when using custom filters:** make sure the specified class is in the classpath at
runtime!

## Custom Rules

You can implement your own rules by creating a class that implements `com.tmarsteel.jcli.rule.Rule`. It MUST have at least one of these constructor signatures:

* (com.tmarsteel.jcli.rule.Rule[]) (Nested `<rule>` tags will be passed to the constructor)
* (org.w3c.com.Node) (The `<rule>` tag will be passed to the constructor)
* ()

To use the custom rule you can either specify the `class` attribute on the `<rule>` tag:

```xml
<rule class="com.mypackage.cli.rule.CustomRule" />
```

or add your own `type` to the `XMLValidatorConfigurator`:

```java
XMLValidatorConfigurator configurator = ... ;
configurator.setRuleType("custom", com.mypackage.cli.rule.CustomRule.class);
```

```xml
<rule type="custom" />
```

Every rule can implement its own XML settings via the `(org.w3c.dom.Node)` constructor:

```xml
<rule class="com.mypackage.cli.rule.CustomRule" ignoreSth="true">
	<option>value</option>
</rule>
```