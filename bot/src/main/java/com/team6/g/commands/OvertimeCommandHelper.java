package com.team6.g.commands;

import com.team6.g.model.User;
import com.team6.g.repository.UserActivityRepository;
import com.team6.g.repository.UserRepository;
import com.team6.g.util.DateUtil;
import com.ullink.slack.simpleslackapi.SlackChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OvertimeCommandHelper extends AbstractCommand {
    @Autowired
    UserRepository userRepository;

    @Autowired
    UserActivityRepository userActivityRepository;

    @Override
    public void handle(SlackChannel slackChannel, User user, List<String> args) {
        // !overtime maikel
        if (args.size() > 2) {
            sendMessage(slackChannel, "fail, command: !overtime <username>");
            return;
        }
        
        if (args.size() == 2) {
            user = userRepository.findByName(args.get(1));
        }

        sendMessage(slackChannel, String.format("user : `%s`, overtime: *%s*", user.getName(), DateUtil.calculateOverTime(userActivityRepository.findAllByUserAndDateInIsNotNullAndDateOutIsNotNull(user))));
    }
}
