package com.team6.g.scheduler;

import com.team6.g.commands.SlackCommands;
import com.team6.g.config.SlackConfig;
import com.team6.g.model.User;
import com.team6.g.model.UserActivity;
import com.team6.g.repository.UserActivityRepository;
import com.team6.g.repository.UserRepository;
import com.team6.g.util.DateUtil;
import com.ullink.slack.simpleslackapi.SlackSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class TimesheetScheduler extends SlackCommands {
    private static final Logger logger = LoggerFactory.getLogger(TimesheetScheduler.class);
    
    @Autowired
    UserActivityRepository userActivityRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SlackSession slackSession;

    @Autowired
    SlackConfig slackConfig;

    /***
     * Cron expression to make sure all users that are logged in will have a logout date
     * if there is no logout date set, we will assume the user has worked 8 hours.
     * timer will go off at 7 PM. 
     *
     */
    @Scheduled(cron="0 0 19 * * MON-FRI", zone="Europe/Brussels")
    public void checkUserTimesheets() {
        List<User> userList = userRepository.findByWorkPeriodMinutesIsNotNull();

        logger.info("starting checkUserTimesheets scheduler..");

        for (User user : userList) {
            UserActivity userActivity = userActivityRepository.findByDateTodayAndUser(user.getId());
            
            if (userActivity != null && userActivity.getDateIn() != null && userActivity.getDateOut() == null) {
                long startTime = userActivity.getDateIn().getTime();
                long workingPeriodTime = TimeUnit.MINUTES.toMillis(user.getWorkPeriodMinutes());

                Date worked = new Date(workingPeriodTime + startTime);

                userActivity.setDateOut(worked);
                userActivityRepository.save(userActivity);

                sendMessage(slackSession.findChannelByName(slackConfig.getChannel()), String.format("user : `%s` did not set his log out date, setting to : `%s`, worked: `%s`", user.getName(), worked, DateUtil.getWorkedTime(userActivity.getDateIn(), userActivity.getDateOut())));
                logger.info("setting timesheet dateOut for user: '{}' to date: '{}'", user.getName(), worked);
            }
        }

    }
}
