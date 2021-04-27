package net.volcano.jdacommands.model.command.annotations.argument;

import java.lang.annotation.*;

@Inherited
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Optional {

}
