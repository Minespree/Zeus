package net.minespree.zeus.discord.command;

import sx.blah.discord.handle.obj.IMessage;

/**
 * @since 15/10/2017
 */
public interface ParameterTransformer {
    Object transform(IMessage message, String source);
}
