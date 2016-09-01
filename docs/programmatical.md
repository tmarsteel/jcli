# Programmatical Configuration

## Sub-Commands

jCLI offers support for sub-commands of the kind like `git commit`, `apt-get install`, `composer update`, etc. These
commands can be nested to an arbitrary depth, allowing for constructs like AWSs `aws ec2 allocate-hosts`.

Every command can have a return value in case it does not just write to STDOUT.

This feature requires some extra code, which is why it is listed on this page.

### Simple usage

#### 1. Put your CLI code into `Commands`

Create a class implementing `com.tmarsteel.jcli.command.Command` for every command your CLI should offer. The validation
of input given to that command is now a responsibility of each command:

```java
class MyCommand implements Command<Void> {
    public Void execute(String[] args) throws ValidationException, Exception {
        // put args through a Validator
    }
}
```

#### 3. Create a `CommandDispatcher`

Create an instance of `com.tmarsteel.jcli.command.CommandDispatcher` (presumably main() is the correct place for this).
You can then register your commands with that dispatcher.

```java
CommandDispatcher<Void> dispatcher = new CommandDispatcher<>();

dispatcher.add("my", new MyCommand());
```

#### 3. Invoke `CommandDispatcher#dispatch` when you're ready

Once all of your commands have been registered, invoke `dispatch` on your dispatcher:

```java
public static void main(String[] args) {
    CommandDispatcher<Void> dispatcher = new CommandDispatcher<>();
    
    // ... register your commands
    
    dispatcher.dispatch(args);
}
```

### Nesting Commands

`CommandDispatcher`s are `Command`s themselves. Lets look, just as an example, at the `aws ec2 allocate-hosts` command:

```java
CommandDispatcher<Void> dispatcher = new CommandDispatcher<>();
CommandDispatcher<Void> ec2Command = new CommandDispatcher<>();
ec2Command.add("allocate-hosts", new EC2_AllocateHostsCommand());
dispatcher.add("ec2", "ec2Command");
```