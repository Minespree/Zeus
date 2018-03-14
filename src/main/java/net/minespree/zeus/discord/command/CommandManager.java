package net.minespree.zeus.discord.command;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minespree.zeus.Zeus;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @since 15/10/2017
 */
public class CommandManager {

    private Zeus zeus;
    private List<CommandData> commands = Lists.newArrayList();
    private Map<Class<?>, ParameterTransformer> transformerMap = Maps.newHashMap();

    public CommandManager() {
        zeus = Zeus.getInstance();

        transformerMap.put(int.class, (message, source) -> {
            try {
                return Integer.valueOf(source);
            } catch (Exception e) {
                message.reply(source + " is not a valid integer");
                return null;
            }
        });
    }

    public void registerCommands(Class<?> clazz) {
        for (Method method : clazz.getDeclaredMethods()) {
            method.setAccessible(true);
            Command annot = method.getAnnotation(Command.class);
            if (annot == null) continue;

            CommandData data = new CommandData(annot.command(), annot.parameters(), annot.usage(), method);
            commands.add(data);
        }
    }

    private Optional<CommandData> anyMatch(String cmd) {
        return commands.stream().filter(c -> c.getCommand().equalsIgnoreCase(cmd)).findFirst();
    }

    @EventSubscriber
    public void onGuildMessageReceived(MessageReceivedEvent event) {
        if (event.getMessage().getMentions().contains(zeus.getClient().getOurUser())
                && (!event.getMessage().mentionsEveryone() && !event.getMessage().mentionsHere())) {
            String message = event.getMessage().getContent();
            String[] split = message.split(" ");
            split = Arrays.copyOfRange(split, 1, split.length);
            if (split.length <= 0) return;

            String command = split[0];
            Optional<CommandData> data = anyMatch(command);
            String[] finalSplit = split;
            data.ifPresent(cd -> {
                Method method = cd.getMethod();

                if (cd.getExpectedArguments() == (finalSplit.length - 1)) {
                    try {
                        List<Object> objects = Lists.newArrayList();
                        objects.add(event.getMessage());
                        String[] paramSplit = Arrays.copyOfRange(finalSplit, 1, finalSplit.length);

                        int index = 0;
                        boolean execute = true;
                        for (Class<?> type : method.getParameterTypes()) {
                            if (type.isAssignableFrom(IMessage.class)) continue;
                            ParameterTransformer transformer = transformerMap.get(type);
                            if (transformer == null) {
                                event.getMessage().reply("Parameter transformer for `" + type.getClass().getName() + "` not found");
                                execute = false;
                                break;
                            } else {
                                Object o = transformer.transform(event.getMessage(), paramSplit[index++]);
                                if (o == null) {
                                    execute = false;
                                    break;
                                }
                                objects.add(o);
                            }
                        }

                        if (execute) cd.getMethod().invoke(null, objects.toArray(new Object[objects.size()]));
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                } else {
                    event.getMessage().reply("Wrong usage, use: `" + cd.getUsage() + "`.");
                }
            });
        }
    }

}

