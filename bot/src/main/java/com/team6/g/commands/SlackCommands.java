package com.team6.g.commands;

import com.team6.g.config.SlackConfig;
import com.ullink.slack.simpleslackapi.SlackChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SlackCommands {
    @Autowired
    SlackConfig slackConfig;
    
    protected void sendMessage(SlackChannel channel, String text) {
        slackConfig.slackSession().sendTyping(channel);
        slackConfig.slackSession().sendMessage(channel, text);
    }
}
