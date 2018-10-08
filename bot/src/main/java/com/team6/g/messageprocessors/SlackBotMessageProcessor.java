package com.team6.g.messageprocessors;

import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted;

public interface SlackBotMessageProcessor {
    Boolean process(SlackMessagePosted event, SlackSession session, String message);
}
