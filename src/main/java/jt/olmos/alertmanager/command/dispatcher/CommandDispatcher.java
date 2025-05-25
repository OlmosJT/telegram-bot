package jt.olmos.alertmanager.command.dispatcher;

import io.github.natanimn.BotContext;
import io.github.natanimn.types.Message;
import jt.olmos.alertmanager.command.api.CommandContext;
import jt.olmos.alertmanager.command.parser.CommandParser;
import jt.olmos.alertmanager.command.registry.CommandRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommandDispatcher {
    private final CommandRegistry commandRegistry;
    private final CommandParser commandParser;

    public void dispatch(BotContext botCtx, Message message) {
        if (message.text == null || message.text.isEmpty()) {
            log.warn("Message text is empty. Nothing to do.");
            return;
        }
        CommandParser.ParsedCommand parsedCommand =
                commandParser.parse(message.text, botCtx, message);

        if (parsedCommand == null ||!parsedCommand.isCommand()) {
            // Not a command or not intended for this bot
            log.trace("Not a command or not for this bot: {}", message.text);
            return;
        }

        String commandIdentifier = parsedCommand.command();
        log.debug("Dispatching command: '{}' with args: '{}' from chat: {}",
                commandIdentifier, parsedCommand.arguments(), message.chat.id);

        commandRegistry.getCommand(commandIdentifier).ifPresentOrElse(
                botCommand -> {
                    try {
                        CommandContext commandContext = new CommandContext(
                                botCtx, message, commandIdentifier, parsedCommand.arguments()
                        );
                        MDC.put("chatId", String.valueOf(message.chat.id));
                        MDC.put("userId", String.valueOf(message.from.id));
                        botCommand.execute(commandContext);
                    } catch (Exception e) {
                        log.error("Error executing command: {}", commandIdentifier, e);
                        botCtx.sendMessage(message.chat.id, "An error occurred while processing your command.").exec();
                    } finally {
                        MDC.clear();
                    }
                },
                () -> {
                    log.warn("No handler found for command: {}", commandIdentifier);
                    // Optionally, send "unknown command" message
                    botCtx.sendMessage(message.chat.id, "Unknown command: " + commandIdentifier).exec();
                }
        );

    }
}
