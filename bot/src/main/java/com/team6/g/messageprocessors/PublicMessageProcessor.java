package com.team6.g.messageprocessors;

import com.team6.g.commands.AbstractCommand;
import com.team6.g.config.SlackConfig;
import com.team6.g.model.Emoji;
import com.team6.g.model.History;
import com.team6.g.model.User;
import com.team6.g.model.WordCount;
import com.team6.g.model.WordTypeWordTypeCount;
import com.team6.g.repository.HistoryRepository;
import com.team6.g.repository.UserRepository;
import com.team6.g.repository.WordCountRepository;
import com.team6.g.repository.WordEmojiRepository;
import com.team6.g.repository.WordTypeEmojiRepository;
import com.team6.g.repository.WordTypeWordCountRepository;
import com.team6.g.util.MessageUtil;
import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PublicMessageProcessor extends AbstractMessageProcessor {
    private static final Logger logger = LoggerFactory.getLogger(PublicMessageProcessor.class);
    
    @Autowired
    UserRepository userRepository;
    
    @Autowired
    HistoryRepository historyRepository;
    
    @Autowired
    WordEmojiRepository wordEmojiRepository;
    
    @Autowired
    SlackConfig slackConfig;
    
    @Autowired
    WordTypeEmojiRepository wordTypeEmojiRepository;
    
    @Autowired
    WordTypeWordCountRepository wordTypeWordCountRepository;
    
    @Autowired
    WordCountRepository wordCountRepository;

    @Override
    protected Boolean internalProcess(SlackMessagePosted event, String message) {
        User user = userRepository.findByName(event.getSender().getUserName());
        
        if (user == null) {
            logger.info("creating user: '{}'", event.getSender().getUserName());
            user = userRepository.save(new User.UserBuilder().withName(event.getSender().getUserName()).build());
        }
        
        if (message.startsWith("!")) {
            String[] commandParts = message.split(" ");
            String baseCommand = commandParts[0].substring(1, commandParts[0].length());
            
            List<String> args = messageToArguments(message);

            processCommand(event.getChannel(), baseCommand, args, user);
        } else {
            processPublicMessage(event, message, user);
        }
        
        return true;
    }
    
    private void processCommand(SlackChannel slackChannel, String base, List<String> args, User user) {
        AbstractCommand abstractCommand = getCommand(base);

        if (abstractCommand != null) {
            abstractCommand.handle(slackChannel, user, args);
        } else {
            logger.error("did not find command with base: '{}', coming from: '{}', with text: '{}'", base, user.getName(), args);
        }
    }
    
    private void processPublicMessage(SlackMessagePosted slackEvent, String text, User user) {
        addMessageToHistory(user, text, slackEvent.getTimeStamp());

        // add emojis to sentence
        String message = MessageUtil.clean(text).replaceAll("[^a-zA-Z0-9'ëäöüÄÖÜßéÉèÈêÊ ]", "").toLowerCase();

        wordEmojiRepository.findAll().stream().forEach(wordEmoji -> {
            // if word has a space in it, match it on the entire sentence
            if (wordEmoji.getWord().getWord().split(" ").length > 1 && message.toLowerCase().contains(wordEmoji.getWord().getWord().toLowerCase())) {
                addEmojiToMessage(slackEvent, wordEmoji.getEmoji());
            } else {
                // parse word by word in sentence
                for (String sentenceWord : message.split(" ")) {
                    if (MessageUtil.isMatch(sentenceWord, wordEmoji.getWord().getWord())) {
                        addEmojiToMessage(slackEvent, wordEmoji.getEmoji());
                    }
                }
            }
        });

        findWordsInSentence(user, message);
    }

    private void findWordsInSentence(User user, String message) {
        wordTypeWordCountRepository.findAll().stream().forEach(word -> {
            if (word.getWord().split(" ").length > 1 && message.toLowerCase().contains(word.getWord().toLowerCase())) {
                addWordStatistics(word, user);
            } else {
                // parse word by word in sentence
                for (String sentenceWord : message.split(" ")) {
                    if (MessageUtil.isMatch(sentenceWord, word.getWord())) {
                        addWordStatistics(word, user);
                    }
                }
            }
        });
    }
    
    private void addWordStatistics(WordTypeWordTypeCount word, User user) {
        WordCount wordCount = new WordCount.WordCountBuilder().withWord(word).withUser(user).build();

        wordCountRepository.save(wordCount);
    }

    private void addEmojiToMessage(SlackMessagePosted event, Emoji emoji) {
        logger.info("adding emoji: '{}' to message: '{}'", String.format(":%s:", emoji.getEmoji()), event.getMessageContent());

        for (String singleEmoji : parseEmoji(emoji)) {
            slackConfig.slackSession().addReactionToMessage(event.getChannel(), event.getTimeStamp(), singleEmoji);   
        }
    }
    
    private List<String> parseEmoji(Emoji emoji) {
        List<String> emojis = new ArrayList<>();
        int emojiIndex = 0;

        for (String s1 : emoji.getEmoji().split("::")) {
            if (s1.startsWith("skin-tone")) {
                emojis.set(emojiIndex, emojis.get(emojiIndex) + "::" + s1);
                emojiIndex++;
            } else {
                emojis.add(s1);
            }
        }
        
        return emojis;
    }
    
    private void addMessageToHistory(User user, String text, String timeStamp) {
        History history = new History.HistoryBuilder()
                .withUser(user)
                .withMessage(text)
                .withTs(timeStamp).build();

        historyRepository.save(history);
    }
}
