package com.team6.g.subscription;

import com.team6.g.messageprocessors.PublicMessageProcessor;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted;
import com.ullink.slack.simpleslackapi.listeners.SlackMessagePostedListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessagePostedListener implements SlackMessagePostedListener {
    private static final Logger logger = LoggerFactory.getLogger(MessagePostedListener.class);

    @Autowired
    PublicMessageProcessor publicMessageCommandProcessor;

    @Override
    public void onEvent(SlackMessagePosted event, SlackSession session) {
        // Make sure it doesn't capture our own messages
        if (session.sessionPersona().getId().equals(event.getSender().getId()) && !session.sessionPersona().isBot()) {
            return;
        }

        logger.info("incoming message from : '{}' : '#{}' -> '{}'", event.getSender().getUserName(), event.getChannel().getName(), event.getMessageContent());

        if (!event.getChannel().isDirect()) {
            publicMessageCommandProcessor.process(event, session, event.getMessageContent());
        }
    }
}
