package com.team6.g.subscription;

import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.events.PresenceChange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PresenceChangeListener implements com.ullink.slack.simpleslackapi.listeners.PresenceChangeListener {
    private static final Logger logger = LoggerFactory.getLogger(PresenceChangeListener.class);

    @Override
    public void onEvent(PresenceChange event, SlackSession session) {
        logger.info("user: '{}' changed presence to: '{}'", event.getUserId(), event.getPresence());
    }
}
