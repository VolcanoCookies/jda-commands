package net.volcano.jdacommands.model.command.annotations;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BotOwnerCanAlwaysExecute {

}
