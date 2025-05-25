package jt.olmos.alertmanager.handler;

import io.github.natanimn.BotClient;
import jakarta.annotation.PostConstruct;
import jt.olmos.alertmanager.command.dispatcher.CommandDispatcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramMessageHandler {

    private final BotClient botClient;
    private final CommandDispatcher commandDispatcher;

    @PostConstruct
    public void registerHandlers() {
        log.info("Registering Telebof message handlers...");

        // Handler for private messages
        botClient.onMessage(
                filter -> filter.Private() && filter.text(),
                (botCtx, message) -> {
                    log.trace("Received private message from {}: {}", message.from.id, message.text);
                    commandDispatcher.dispatch(botCtx, message);
                }
        );

        // Handler for group and supergroup messages
        botClient.onMessage(
                filter -> (filter.group() || filter.supergroup()) && filter.text(),
                (botCtx, message) -> {
                    log.trace("Received group/supergroup message in chat {}: {}", message.chat.id, message.text);
                    commandDispatcher.dispatch(botCtx, message);
                }
        );

        // Handler other specific message types (e.g., photos, documents)
    }
}
