package com.ullink.slack.simpleslackapi.events;

import com.ullink.slack.simpleslackapi.SlackChannel;
import lombok.Data;

@Data
public class SlackMessageDeleted implements SlackEvent {
    private SlackChannel channel;
    private String messageTimestamp;
    private String deleteTimestamp;

    public SlackMessageDeleted(SlackChannel channel, String messageTimestamp, String deleteTimestamp) {
        this.channel = channel;
        this.messageTimestamp = messageTimestamp;
        this.deleteTimestamp = deleteTimestamp;
    }

    public String getTimeStamp()
    {
        return deleteTimestamp;
    }

    @Override
    public SlackEventType getEventType() {
        return SlackEventType.SLACK_MESSAGE_DELETED;
    }

}
