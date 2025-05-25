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
public class TelegramChannelPostHandler {
    private final BotClient botClient;
    private final CommandDispatcher commandDispatcher;

    @PostConstruct
    public void registerHandlers() {
        log.info("Registering Telebof channel post handlers...");

        botClient.onChannelPost(
                filter -> filter.text()!= null && filter.text(),
                (botCtx, channelPost) -> {
                    log.trace("Received channel post in chat {}: {}", channelPost.chat.id, channelPost.text);
                    commandDispatcher.dispatch(botCtx, channelPost);
                }
        );

        // Add handlers for edited channel posts if needed
        // botClient.onEditedChannelPost(...)
    }
}
