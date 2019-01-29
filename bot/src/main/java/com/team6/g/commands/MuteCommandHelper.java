package com.team6.g.commands;

import com.team6.g.config.MuteConfig;
import com.team6.g.model.User;
import com.team6.g.repository.UserRepository;
import com.ullink.slack.simpleslackapi.SlackChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MuteCommandHelper extends AbstractCommand {
    private static final Logger logger = LoggerFactory.getLogger(MuteCommandHelper.class);

    @Autowired
    UserRepository userRepository;

    @Override
    public void handle(SlackChannel slackChannel, User user, List<String> args) {
        if (user.getId() != 1) {
            sendMessage(slackChannel, "lol nope.");
            return;
        }
        
        if ("start".equals(args.get(1))) {
            MuteConfig.active = true;
            MuteConfig.userToMute = slackConfig.slackSession().findUserByUserName(args.get(2));

            logger.info("ignoring user: '{}'", args.get(2));
        } else if ("stop".equals(args.get(1))) {
            logger.info("stop ignoring user: '{}'", args.get(2));
            MuteConfig.active = false;
        } else {
            logger.error("faulty command for muting");
        }
    }
}
