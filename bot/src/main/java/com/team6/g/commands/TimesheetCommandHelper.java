package com.team6.g.commands;

import com.team6.g.model.User;
import com.team6.g.model.UserActivity;
import com.team6.g.repository.UserActivityRepository;
import com.team6.g.repository.UserRepository;
import com.team6.g.util.DateUtil;
import com.ullink.slack.simpleslackapi.SlackChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class TimesheetCommandHelper extends AbstractCommand {
    private static final Logger logger = LoggerFactory.getLogger(TimesheetCommandHelper.class);
    
    @Autowired
    UserRepository userRepository;

    @Autowired
    UserActivityRepository userActivityRepository;

    @Override
    public void handle(SlackChannel slackChannel, User user, List<String> args) {
        if (args.size() > 3) {
            sendMessage(slackChannel, "fail.");
            return;
        }

        if (args.size() >= 2) {
            // if the command has no name included, lookup our own
            if (args.size() == 3) {
                user = userRepository.findByName(args.get(2));
            }
            
            logger.info("looking up timesheet statistics for user: '{}'", user);

            if ("overtime".equals(args.get(1))) {
                // !timesheet overtime maikel
                sendMessage(slackChannel, String.format("user : `%s`, overtime: *%s*", user.getName(), DateUtil.calculateOverTime(userActivityRepository.findAllByUserAndDateInIsNotNullAndDateOutIsNotNull(user))));
            } else if ("logintime".equals(args.get(1))) {
                // !timesheet logintime maikel
                UserActivity userActivity = userActivityRepository.findByDateTodayAndUser(user);

                if (userActivity.getDateIn() == null) {
                    sendMessage(slackChannel, "user : `%s` did not login yet");
                } else {
                    sendMessage(slackChannel, String.format("user : `%s` logged in at : `%s`", user.getName(), userActivityRepository.findByDateTodayAndUser(user).getDateIn()));
                }
            } else if ("workedtime".equals(args.get(1))) {
                // !timesheet workedtime maikel
                UserActivity userActivity = userActivityRepository.findByDateTodayAndUser(user);

                if (userActivity.getDateIn() == null) {
                    sendMessage(slackChannel, "user : `%s` did not login yet");
                } else {
                    sendMessage(slackChannel, String.format("user : `%s` has already worked `%s` today", user.getName(), DateUtil.getWorkedTime(userActivityRepository.findByDateTodayAndUser(user).getDateIn(), new Date())));
                }
            }
        }
    }
}
