package jt.olmos.alertmanager.command.registry;

import jakarta.annotation.PostConstruct;
import jt.olmos.alertmanager.command.api.BotCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommandRegistry {
    private final List<BotCommand> commands;
    private Map<String, BotCommand> commandMap;

    @PostConstruct
    public void init() {
        commandMap = commands.stream()
                .filter(cmd -> cmd.getCommandIdentifier() != null && !cmd.getCommandIdentifier().isEmpty())
                .collect(Collectors.toMap(BotCommand::getCommandIdentifier, Function.identity(), (existing, replacement) -> {
                    log.warn("Duplicate command identifier found: {}. Keeping existing: {}",
                            existing.getCommandIdentifier(), existing.getClass().getName());
                    return existing;
                }));
        log.info("Registered commands: {}", commandMap.keySet());
    }

    public Optional<BotCommand> getCommand(String identifier) {
        return Optional.ofNullable(commandMap.get(identifier.toLowerCase()));
    }

    public Map<String, BotCommand> getAllCommands() {
        return commandMap;
    }
}
