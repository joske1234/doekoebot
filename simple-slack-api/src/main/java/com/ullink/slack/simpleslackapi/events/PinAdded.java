package com.ullink.slack.simpleslackapi.events;

import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackFile;
import com.ullink.slack.simpleslackapi.SlackUser;
import lombok.Data;

@Data
public class PinAdded implements SlackEvent {

    private SlackUser sender;
    private SlackChannel channel;
    private String timestamp;
    private SlackFile file;
    private String message;

    public PinAdded(SlackUser sender, SlackChannel channel, String timestamp, SlackFile file, String message) {
        this.sender = sender;
        this.channel = channel;
        this.timestamp = timestamp;
        this.file = file;
        this.message = message;
    }

    public SlackUser getSender() {
        return sender;
    }

    public void setSender(SlackUser sender) {
        this.sender = sender;
    }

    public SlackChannel getChannel() {
        return channel;
    }

    public void setChannel(SlackChannel channel) {
        this.channel = channel;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public SlackFile getFile() {
        return file;
    }

    public void setFile(SlackFile file) {
        this.file = file;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public SlackEventType getEventType() {
        return SlackEventType.PIN_ADDED;
    }

}
