package com.ullink.slack.simpleslackapi.events;

import com.ullink.slack.simpleslackapi.SlackUser;
import com.ullink.slack.simpleslackapi.SlackChannel;
import lombok.Data;

@Data
public class SlackChannelArchived implements SlackEvent {
    private SlackChannel slackChannel;
    private SlackUser slackuser;

    public SlackChannelArchived(SlackChannel slackChannel, SlackUser slackuser) {
        this.slackChannel = slackChannel;
        this.slackuser = slackuser;
    }

    public SlackUser getUser()
    {
        return slackuser;
    }

    public SlackChannel getSlackChannel() {
        return slackChannel;
    }

    public void setSlackChannel(SlackChannel slackChannel) {
        this.slackChannel = slackChannel;
    }

    public SlackUser getSlackuser() {
        return slackuser;
    }

    public void setSlackuser(SlackUser slackuser) {
        this.slackuser = slackuser;
    }

    @Override
    public SlackEventType getEventType()
    {
        return SlackEventType.SLACK_CHANNEL_ARCHIVED;
    }

}
