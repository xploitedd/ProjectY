package me.xploited.projecty.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Command {

    String value();
    String usage() default "";
    boolean isPlayerRequired() default false;
    int requiredArguments() default 0;

}
