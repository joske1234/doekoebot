package com.team6.g.commands;

import com.team6.g.model.User;
import com.ullink.slack.simpleslackapi.SlackChannel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public abstract class AbstractCommand extends SlackCommands {
    public abstract void handle(SlackChannel slackChannel, User user, List<String> args);
}
