package com.team6.g.commands;

import com.team6.g.config.SlackConfig;
import com.team6.g.model.User;
import com.ullink.slack.simpleslackapi.SlackChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public abstract class AbstractCommand {
    @Autowired
    SlackConfig slackConfig;

    public abstract void handle(SlackChannel slackChannel, User user, List<String> args);

    void sendMessage(SlackChannel channel, String text) {
        slackConfig.slackSession().sendTyping(channel);
        slackConfig.slackSession().sendMessage(channel, text);
    }
}
