package com.ullink.slack.simpleslackapi.events;

import com.ullink.slack.simpleslackapi.SlackChannel;
import lombok.Data;

@Data
public class SlackChannelRenamed implements SlackEvent {
    private SlackChannel slackChannel;
    private String newName;

    public SlackChannelRenamed(SlackChannel slackChannel, String newName) {
        this.slackChannel = slackChannel;
        this.newName = newName;
    }

    public SlackChannel getSlackChannel() {
        return slackChannel;
    }

    public void setSlackChannel(SlackChannel slackChannel) {
        this.slackChannel = slackChannel;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }

    @Override
    public SlackEventType getEventType() {
        return SlackEventType.SLACK_CHANNEL_RENAMED;
    }

}
