package jt.olmos.alertmanager.core;

import io.github.natanimn.BotClient;
import io.github.natanimn.types.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
@RequiredArgsConstructor
public class BotUsernameProvider {
    private final BotClient botClient;
    private final AtomicReference<String> botUsername = new AtomicReference<>();

    public void initializeBotUsername() {
        if (botUsername.get() == null) {
            try {
                log.info("Fetching bot username...");
                User me = botClient.context.getMe().exec();
                if (me!= null && me.username != null) {
                    this.botUsername.set(me.username);
                    log.info("Bot username initialized: @{}", me.username);
                } else {
                    log.error("Failed to fetch bot username. 'getMe' returned null or username is null.");
                }
            } catch (Exception e) {
                log.error("Error fetching bot username", e);
            }
        }
    }

    public String getBotUsername() {
        if (botUsername.get() == null) {
            // Fallback or error if not initialized. Consider re-attempting initialization.
            log.warn("Bot username accessed before initialization or initialization failed.");
            // For robustness, could throw an exception or attempt re-initialization here.
            // However, for initial setup, ensure initializeBotUsername() is called during startup.
            initializeBotUsername(); // Attempt lazy initialization
            if (botUsername.get() == null) {
                throw new IllegalStateException("Bot username is not available.");
            }
        }
        return botUsername.get();
    }
}
