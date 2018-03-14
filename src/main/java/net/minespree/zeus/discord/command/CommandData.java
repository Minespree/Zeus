package net.minespree.zeus.discord.command;

import java.lang.reflect.Method;

/**
 * @since 15/10/2017
 */
class CommandData {

    private String command;
    private int expectedArguments;
    private String usage;
    private Method method;

    CommandData(String command, int expectedArguments, String usage, Method method) {
        this.command = command;
        this.expectedArguments = expectedArguments;
        this.usage = usage;
        this.method = method;
    }

    String getCommand() {
        return command;
    }

    int getExpectedArguments() {
        return expectedArguments;
    }

    String getUsage() {
        return usage;
    }

    Method getMethod() {
        return method;
    }
}
