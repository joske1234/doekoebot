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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.team6.g.util.DateUtil.atEndOfDay;
import static com.team6.g.util.DateUtil.atStartOfDay;
import static com.team6.g.util.DateUtil.getWorkedTime;

@Component
public class TimesheetCommandHelper extends AbstractCommand {
    private static final Logger logger = LoggerFactory.getLogger(TimesheetCommandHelper.class);
    
    @Autowired
    UserRepository userRepository;

    @Autowired
    UserActivityRepository userActivityRepository;

    @Override
    public void handle(SlackChannel slackChannel, User user, List<String> args) {
        // !timesheet <command> <user>
        if (args.size() >= 2) {
            // if the command has name included, lookup
            if (args.size() == 3) {
                user = userRepository.findByName(args.get(2));
            }
            
            logger.info("looking up timesheet statistics for user: '{}'", user);

            if ("overtime".equals(args.get(1))) {
                // !timesheet overtime maikel
                sendMessage(slackChannel, String.format("user : `%s`, overtime: *%s*", user.getName(), DateUtil.calculateOverTime(userActivityRepository.findAllByUserAndDateInIsNotNullAndDateOutIsNotNull(user))));
            } else if ("logintime".equals(args.get(1))) {
                // !timesheet logintime maikel
                UserActivity userActivity = userActivityRepository.findByDateTodayAndUser(user.getId());

                if (userActivity.getDateIn() == null) {
                    sendMessage(slackChannel, "user : `%s` did not login yet");
                } else {
                    sendMessage(slackChannel, String.format("user : `%s` logged in at : `%s`", user.getName(), userActivityRepository.findByDateTodayAndUser(user.getId()).getDateIn()));
                }
            } else if ("workedtime".equals(args.get(1))) {
                // !timesheet workedtime date <date>
                if (args.size() == 4 && "date".equals(args.get(2))) {
                    SimpleDateFormat parser = new SimpleDateFormat("dd/MM/yyyy");
                    try {
                        Date dateObj = parser.parse(args.get(3));
                        UserActivity userActivity = userActivityRepository.findAllByDateAddedBetweenAndUser(atStartOfDay(dateObj), atEndOfDay(dateObj), user);

                        if (userActivity == null) {
                            sendMessage(slackChannel, "*niet* gewerkt op die dag, dus geen factuur'je. mondje.");
                        } else {
                            if (userActivity.getDateIn() != null && userActivity.getDateOut() != null) {
                                sendMessage(slackChannel, String.format("user : `%s` worked : %s", user.getName(), getWorkedTime(userActivity.getDateIn(), userActivity.getDateOut())));
                            } else {
                                sendMessage(slackChannel, "eerst een hele dag werken plz");
                            }
                        }
                    } catch (ParseException e) {
                        sendMessage(slackChannel, "uh mooi date format, n00b. dd/MM/yyyy pls");
                    }
                } else {
                    // !timesheet workedtime maikel
                    UserActivity userActivity = userActivityRepository.findByDateTodayAndUser(user.getId());

                    if (userActivity.getDateIn() == null) {
                        sendMessage(slackChannel, "user : `%s` did not login yet");
                    } else {
                        sendMessage(slackChannel, String.format("user : `%s` has already worked `%s` today", user.getName(), DateUtil.getWorkedTime(userActivityRepository.findByDateTodayAndUser(user.getId()).getDateIn(), new Date())));
                    }
                }
            } else if ("stats".equals(args.get(1))) {
                StringBuilder sb = new StringBuilder();

                sb.append(String.format("user : `%s` logged in at : `%s`\n", user.getName(), userActivityRepository.findByDateTodayAndUser(user.getId()).getDateIn()));
                sb.append(String.format("user : `%s` has already worked `%s` today\n", user.getName(), DateUtil.getWorkedTime(userActivityRepository.findByDateTodayAndUser(user.getId()).getDateIn(), new Date())));
                sb.append(String.format("user : `%s`, overtime: *%s*\n", user.getName(), DateUtil.calculateOverTime(userActivityRepository.findAllByUserAndDateInIsNotNullAndDateOutIsNotNull(user))));

                sendMessage(slackChannel, sb.toString());
            }
        }
        
        // !timesheet edit <logintime|logouttime> <date>
        if (args.size() == 4 && "edit".equals(args.get(1))) {
            String whatToEdit = args.get(2);
            String dateStr = args.get(3);

            UserActivity userActivity = userActivityRepository.findByDateTodayAndUser(user.getId());
            
            if (userActivity == null) {
                sendMessage(slackChannel, String.format("no user activity found for user: `%s`", user.getName()));
                return;
            }

            if (changeTime(userActivity, whatToEdit, dateStr)) {
                sendMessage(slackChannel, String.format("changed %s time to : `%s` for user: `%s`", whatToEdit, dateStr, user.getName()));
            } else {
                sendMessage(slackChannel, "unknown error occurred");
            }
        }
    }
    
    private Boolean changeTime(UserActivity userActivity, String whatToEdit, String dateStr) {
        try {
            Boolean ret = true;
            Date dateToSet = DateUtil.getTodayDateWithTimePattern(dateStr);
            
            switch (whatToEdit) {
                case "logintime":
                    userActivity.setDateIn(dateToSet);
                    break;
                
                case "logouttime":
                    userActivity.setDateOut(dateToSet);
                    break;
                    
                default: 
                    ret = false;
            }

            if (ret) {
                userActivityRepository.save(userActivity);
            }
            
            return ret;
        } catch (ParseException e) {
            return false;
        }
    }
}
