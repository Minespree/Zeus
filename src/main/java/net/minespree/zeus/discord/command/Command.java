package net.minespree.zeus.discord.command;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @since 15/10/2017
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
    String command();

    int parameters() default 0;

    String usage() default "";
}
