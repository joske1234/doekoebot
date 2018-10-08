package com.ullink.slack.simpleslackapi.events;

import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackUser;
import lombok.Data;

@Data
public class ReactionAdded implements SlackEvent {

    private String emojiName;
    private SlackUser user;
    private SlackUser itemUser;
    private SlackChannel channel;
    private String messageID;
    private String fileID;
    private String fileCommentID;
    private String timestamp;

    public ReactionAdded(String emojiName, SlackUser user, SlackUser itemUser, SlackChannel channel, String messageID, String fileID, String fileCommentID, String timestamp) {
        this.emojiName = emojiName;
        this.user = user;
        this.itemUser = itemUser;
        this.channel = channel;
        this.messageID = messageID;
        this.fileID = fileID;
        this.fileCommentID = fileCommentID;
        this.timestamp = timestamp;
    }

    public String getEmojiName() {
        return emojiName;
    }

    public void setEmojiName(String emojiName) {
        this.emojiName = emojiName;
    }

    public SlackUser getUser() {
        return user;
    }

    public void setUser(SlackUser user) {
        this.user = user;
    }

    public SlackUser getItemUser() {
        return itemUser;
    }

    public void setItemUser(SlackUser itemUser) {
        this.itemUser = itemUser;
    }

    public SlackChannel getChannel() {
        return channel;
    }

    public void setChannel(SlackChannel channel) {
        this.channel = channel;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getFileID() {
        return fileID;
    }

    public void setFileID(String fileID) {
        this.fileID = fileID;
    }

    public String getFileCommentID() {
        return fileCommentID;
    }

    public void setFileCommentID(String fileCommentID) {
        this.fileCommentID = fileCommentID;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public SlackEventType getEventType() {
        return SlackEventType.REACTION_ADDED;
    }
}
