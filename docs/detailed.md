# Configuring the parser with XML

The root-element of the config xml MUST be an `<cli>`-Tag. It includes all definitions of flags, options and arguments and their possible values and validation information. A quick example:

```xml
<xml version="1.0">
<cli>
    <flag identifier="verbose">
        <alias>v</alias>
    </flag>

    <argument identifier="input" index="0">
        <filter type="file">
            <permissions>READ</permissions>
            <existance>MUST_EXIST</existance>
        </filter>
    </argument>
</cli>
```

These CLI-Calls are equal:  
`<executable> -v foo.txt`  
`<executable> -verbose foo.txt`


## Flags

Flags are configured using the `<flag>`-tag. The `identifier` attribute MUST be set.  
**Note:** the content of the identifier-attribute is the string that you will have to use to access the flag with from your code.

Inside that tag you can specify as many aliases (for abbreviation or compatibility) as you wish. The identifier as well as all the aliases will be recognized.
For example:

```xml
<flag identifier="verbose">
    <alias>v</alias>
</flag>
```

## Options

Options are configured using the `<option>`-tag. The `identifier` attribute MUST be set.  
**Note:** the content of the identifier-attribute is the string that you will have to use to access the option with from your code.  

Inside that tag you can specify as many aliases (for abbreviation or compatibility) as you wish. The identifier as well as all the aliases will be recognized.

You MAY specify a `<default>`-tag. This will be the value used when the option is not set. Omitting the `<default>` tag will cause an error if the option is not given in input parsed.

You MAY specify one <filter>-tag that will hint to the parser how to validate input given to that option. See below for filters.

for example:

```xml
<option identifier="output">
    <alias>out</alias>
    <alias>o</alias>
</option>
```

## Arguments

Arguments are configured using the `<argument>`-tag. Both the `identifier` and the `index` attribute to be set. 

You MAY set the `required`-attribute to true. This will cause the parser to throw an error if no input could be mapped to this argument.

The content of the `identifier`-attribute is the string that you will have to use to access the option with from your code.

The content of the `index`-attriute MUSTbe an integer number greater or equal to 0. It defines what argument should be parsed as defined in the `<argument>` tag.

You MAY specify a `<default>`-tag. This will be the value used when the argument is not set.

You MAY specify one `<filter>`-tag that will hint to the parser how to validate input given to that argument. See below for filters.

Example:

```xml
<argument identifier="format" index="1" />
```

`<executable> -v foo.txt someFormat`


## Filters

A `<filter>`-tag can be specified for options and arguments. It tells the parser how to parse the values for these options/arguments, e.g. integers or files. There are seven predefined filters (see below). You can also specify another class that is to filter values; it has to implement `com.tmarsteel.jcli.filter.ValueFilter`.

The strings pared from the input are passed to the filters. They return objects already specific to what their
validation is, e.g. `java.lang.Long` or `java.io.File`.

Either the type-attribute or the class attribute can be set on a `<filter>`-tag. What values are valid for the type-attribute is descibred below:

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

* existance: the file has to exist, the file may not exist or existance is irrelevant
* permissions: the has to be
  - readable
  - writable
  - executable
* it has to be a file/directory
* a specific extension is required

To define the required existance-state use an `<exinstance>` tag. Its content has to be one of `com.wisper.cli.filter.FileFilter.EXISTANCE`s values:

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
    <existance>MUST_EXIST</existance>
    <permissions>READ</permissions>
</filter>
```

#### Set Filter

The set filter allows only values from a given set; case-sensitivity is off by default but can be turned on by the `caseSensitive`-attribute.

Specify the different possibilities with <value> tags. The set filter will always return values from the configuration and never the actual values. For Example:

```xml
<filter type="set">
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


## Rules

You can specify rules that will be checked after all input has been parsed and that can reject that input in case they are not met. For example to require that an option is specified or that two options cannot be set at the same time.

To refuse all flags and options that are not known by the parser, you will have to add the rules programmatically:

```java
CLIParser cliParser = /* load the parser however you like */;
cliParser.add(Rule.ONLY_KNWON_OPTIONS);
cliParser.add(Rule.ONLY_KNWON_FLAGS);
```

Almost any other combination can be configured via XML. There are six types of rules. Either the `type`-attribute or the `class` attribute can be set on a `<rule>`-tag. What values are valid for the `type` attribute is descibred below.

The error messages produced by the rules included in the library do not always produce nice error messages. For
every rule included in the library you can specify an `<error>` tag that will be passed as the error message
in case of failure.

**Note:** rules should be at the end of the XML-file since referring to a not yet parsed option/flag will result in an error.

#### Option rule: option-set

This rule requires that one or more options/flags are set. Specify these options/flags with the `<option>`-tag. The content of these `<option>` tags has to be equal to the `identifier` attribute of the referenced option/flag; 
**aliases do not work!**

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

Custom filter can be implemented; First, you will have to create a class that extends `com.tmarsteel.jcli.filter.ValueFilter`. It MUST have one of these constructor signatures:

* `(org.w3c.dom.Node)`
* `()`

The `(org.w3c.dom.Node)` constructor will be preferred. The argument is the `<filter>` tag found by the parser.

In the `<filter>` tag, put the **full** classname in the `type` classname:

```xml
<filter type="com.mypackage.cli.filter.CustomFilterXY" />
```

Of course, your filter can implement its own XML settings via the `(org.w3c.dom.Node)` constructor:

```xml
<filter type="com.mypackage.cli.filter.CustomFilterXY" ignoreSth="true">
	<option>value</option>
</filter>
```

**Note when using custom filters:** make sure the specified class is in the classpath!

## Custom Rules

You can implement your own rules by specifying a `class`-attribute instead of a `type`-attribute and implement the correct interface. There are two ways of doing so:

#### Simple Rule

Implement `com.tmarsteel.jcli.rule.Rule`. If the specified class implements this interface the `<rule>`-tag MUST NOT have any contents and the class MUST have an empty `()` constructor.

```xml
<rule class="com.myproject.SimpleRule" />
```

### Combined Rule: combine multilple rules

Implement `com.tmarsteel.jcli.rule.CombinedRule`. If the specified class implements this interface the `<rule>`-tag MAY have nested `<rule>` tags and the class has to have a `(com.tmarsteel.jcli.rule.Rule[])` constructor. Needless to say, the defined rules from the XML document will be passed to your class when constructing an instance.

```xml
<rule class="com.myproject.CombinedRule">
    <rule type="option-set">
        <option>input</option>
    </rule>
    <rule type="option-set">
        <option>output</option>
    </rule>
</rule>
```

**Note:** in a later release, rules can implement further XML directives by implenting a `(org.w3c.dom.Node)` constructor.
