package com.team6.g.messageprocessors;

import com.team6.g.commands.AbstractCommand;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public abstract class AbstractMessageProcessor implements SlackBotMessageProcessor {
    @Autowired
    ApplicationContext applicationContext;

    protected abstract Boolean internalProcess(SlackMessagePosted event, String message);
    
    public Boolean process(SlackMessagePosted event, SlackSession session, String message) {
        return internalProcess(event, message);
    }

    AbstractCommand getCommand(String base) {
        for (String bean : applicationContext.getBeanDefinitionNames()) {
            String beanLowerCase = bean.toLowerCase();

            if (beanLowerCase.startsWith(base) && !beanLowerCase.startsWith("abstract")) {
                if (applicationContext.getBean(bean) instanceof AbstractCommand) {
                    return (AbstractCommand) applicationContext.getBean(bean);
                }
            }
        }

        return null;
    }

    List<String> messageToArguments(String message) {
        List<String> arguments = new ArrayList<String>();
        Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(message);

        while (m.find()) {
            arguments.add(m.group(1).replace("\"", ""));
        }

        return arguments;
    }
}
