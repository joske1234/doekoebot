package com.team6.g.controllers;

import com.team6.g.config.SlackConfig;
import com.team6.g.model.User;
import com.team6.g.model.UserActivity;
import com.team6.g.model.web.Badge;
import com.team6.g.repository.UserActivityRepository;
import com.team6.g.repository.UserRepository;
import com.team6.g.util.DateUtil;
import com.ullink.slack.simpleslackapi.SlackSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TimesheetController {
    @Autowired UserRepository userRepository;
    @Autowired UserActivityRepository userActivityRepository;
    @Autowired SlackSession slackSession;

    @Value( "${slack.channel}" )
    private String webDefaultChannel;

    private void sendMessage(String text) {
        slackSession.sendTyping(slackSession.findChannelByName(webDefaultChannel));
        slackSession.sendMessage(slackSession.findChannelByName(webDefaultChannel), text);
    }

    private UserActivity getUserActity(Badge badge){
        User user = userRepository.findByName(badge.getUsername());
        System.out.println(user);
        UserActivity userActivity = userActivityRepository.findByDateAndUser(user.getId(), DateUtil.atStartOfDay(badge.getBadge()));
        if (userActivity == null){
            userActivity = new UserActivity();
            userActivity.setUser(user);
        }

        return userActivity;
    }

    @PostMapping("/badge/in")
    void badgeIn(@RequestBody Badge badge){
        UserActivity userActivity = getUserActity(badge);
        userActivity.setDateIn(badge.getBadge());

        sendMessage(String.format("user : `%s` logged in at : `%s`", userActivity.getUser().getName(), badge.getBadge()));

        userActivityRepository.save(userActivity);
    }

    @PostMapping("/badge/out")
    void badgeOut(@RequestBody Badge badge){
        UserActivity userActivity = getUserActity(badge);
        userActivity.setDateOut(badge.getBadge());

        sendMessage(String.format("user : `%s` logged out at : `%s`", userActivity.getUser().getName(), badge.getBadge()));

        userActivityRepository.save(userActivity);
    }
}
