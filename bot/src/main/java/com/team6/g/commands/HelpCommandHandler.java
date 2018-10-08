package com.team6.g.commands;

import com.team6.g.model.User;
import com.ullink.slack.simpleslackapi.SlackChannel;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HelpCommandHandler extends AbstractCommand {
    @Override
    public void handle(SlackChannel slackChannel, User slackUser, List<String> args) {
        StringBuilder sb = new StringBuilder();
        sb.append("========= doekoebot v2.0 =========\n");
        sb.append("\n");
        sb.append("-- emojis --\n");
        sb.append("!emoji <add|remove> <word> <emoji>\n");
        sb.append("\n");
        sb.append("-- history --\n");
        sb.append("!history stats date <dd/MM/yyyy>\n");
        sb.append("!history stats\n");
        sb.append("\n");
        sb.append("-- words --\n");
        sb.append("!word stats top <0-25>\n");
        sb.append("!word stats all\n");
        sb.append("!word stats all <word>\n");
        sb.append("!word stats <user> (optional: <word>)\n");
        sb.append("!word list\n");
        sb.append("!word <add|remove>\n");
        sb.append("!word search word\n");
                
        sendMessage(slackChannel, sb.toString());
    }
}
