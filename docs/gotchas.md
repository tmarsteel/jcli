# Gotchas

## Ambigous argument values

Passing an argument value that starts with flag and/or option prefixes causes parsing issues:

`program -flag --option optionValue -argumentValue`

The parser cannot distinguish between a flag with the name `argumentValue` and an actual argument at
index 0 with the value `-argumentValue`. Given input like the above, the parser will decide that the 
last parameter is a flag; `Input#getArgument(0)` will return `null` (instead of `-argumentValue`).

This issue does not arise if the ambiguous argument is at index 1 or higher:

`program -flag -option optionValue argument1value -argument2value`

Will parse as expected:

```java
assertTrue(input.flags().contains("flag"));
assertEquals("optionValue", input.options().get("option"));
assertEquals("argument1Value", input.getArgument(0));
assertEquals("-argument2Value", input.getArgument(1));
```

### Workaround

To work around this, put a colon parameter before the first argument:

`program -flag -option optionValue : -argument1Value`

`program : -argument1Value`

```java
assertEquals("-argument1Value", input.getArgument(0));
```

This syntax is always correct, regardless of the actual argument values: `program : argument1value`.  
The documentation uses that syntax in every case. It is easier for the final software users to always
include the colon instead of having to learn and decide when to use it.