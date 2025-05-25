package jt.olmos.alertmanager.command.impl;

import jt.olmos.alertmanager.command.api.BotCommand;
import jt.olmos.alertmanager.command.api.CommandContext;
import org.springframework.stereotype.Component;

@Component
public class StartCommand implements BotCommand {
    @Override
    public String getCommandIdentifier() {
        return "start";
    }

    @Override
    public void execute(CommandContext context) {
        String welcomeMessage = "Welcome to the bot! Your chat ID is: " + context.message().chat.id;
        if (context.arguments() != null &&!context.arguments().isEmpty()) {
            welcomeMessage += "\nPayload received: " + context.arguments();
        }
        context.botContext().sendMessage(context.message().chat.id, welcomeMessage).exec();
    }

    @Override
    public String getDescription() {
        return "Initiates interaction with the bot.";
    }
}
