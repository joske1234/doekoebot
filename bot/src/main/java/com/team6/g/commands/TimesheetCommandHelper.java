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

import java.text.ParseException;
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

        // !timesheet <command> <user>
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
        
        // !timesheet edit logintime <user> <date>
        if (args.size() == 5 && "edit".equals(args.get(1)) && user.getId() == 1) {
            String whatToEdit = args.get(2);
            User lookupUser = userRepository.findByName(args.get(3));
            
            if (lookupUser == null) {
                sendMessage(slackChannel, String.format("user : `%s` does not exist", args.get(3)));
                return;
            }

            UserActivity userActivity = userActivityRepository.findByDateTodayAndUser(lookupUser);
            
            if (userActivity == null) {
                sendMessage(slackChannel, String.format("no user activity found for user: `%s`", user.getName()));
                return;
            }
            
            if ("logintime".equals(whatToEdit)) {
                try {
                    Date dateToSet = DateUtil.getTodayDateWithTimePattern(args.get(4));
                    
                    userActivity.setDateIn(dateToSet);
                    userActivityRepository.save(userActivity);
                    
                    sendMessage(slackChannel, String.format("changed log in date to : `%s` for user: `%s`", args.get(4), lookupUser.getName()));
                } catch (ParseException e) {
                    sendMessage(slackChannel, String.format("cannot parse: `%s`, correct format: HH:mm:ss", args.get(4)));
                }
            }
        }
    }
}
