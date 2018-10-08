package com.ullink.slack.simpleslackapi.events;

import com.ullink.slack.simpleslackapi.SlackChannel;
import lombok.Data;
import lombok.NonNull;

@Data
public class SlackGroupJoined implements SlackEvent {
    @NonNull
    private SlackChannel slackChannel;

    public SlackGroupJoined(SlackChannel slackChannel) {
        this.slackChannel = slackChannel;
    }

    public SlackChannel getSlackChannel() {
        return slackChannel;
    }

    public void setSlackChannel(SlackChannel slackChannel) {
        this.slackChannel = slackChannel;
    }

    @Override
    public SlackEventType getEventType() {
        return SlackEventType.SLACK_GROUP_JOINED;
    }

}
