package com.team6.g.commands;

import com.team6.g.model.Emoji;
import com.team6.g.model.User;
import com.team6.g.model.WordEmoji;
import com.team6.g.model.WordTypeTypeEmoji;
import com.team6.g.repository.EmojiRepository;
import com.team6.g.repository.UserRepository;
import com.team6.g.repository.WordEmojiRepository;
import com.team6.g.repository.WordTypeEmojiRepository;
import com.ullink.slack.simpleslackapi.SlackChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EmojiCommandHelper extends AbstractCommand {
    private static final Logger logger = LoggerFactory.getLogger(EmojiCommandHelper.class);

    @Autowired
    EmojiRepository emojiRepository;

    @Autowired
    WordEmojiRepository wordEmojiRepository;

    @Autowired
    WordTypeEmojiRepository wordTypeEmojiRepository;

    @Autowired
    UserRepository userRepository;

    @Override
    public void handle(SlackChannel slackChannel, User user, List<String> args) {
        if (args.size() != 4) {
            sendMessage(slackChannel, "!emoji <add|remove> word emoji");
            return;
        }

        String word = args.get(2);
        String emoji = args.get(3);
        String command = args.get(1);

        if (!emoji.startsWith(":") && !emoji.endsWith(":")) {
            sendMessage(slackChannel, "*fout* het is !emoji <add|remove> word emoji");
            return;
        }

        emoji = emoji.substring(1, emoji.length() - 1);
        Emoji emojiObj = emojiRepository.findByEmoji(emoji);
        WordTypeTypeEmoji wordObj = wordTypeEmojiRepository.findByWord(word);

        if ("add".equals(command)) {
            handleAddCommand(slackChannel, word, emoji, emojiObj, wordObj, user);
        } else if ("remove".equals(command)) {
            handleRemoveCommand(slackChannel, emoji, word);
        } else {
            logger.error("did not find command '{}'", command);
        }
    }

    private void handleRemoveCommand(SlackChannel slackChannel, String emoji, String word) {
        WordEmoji wordEmoji = wordEmojiRepository.findByEmojiEmojiAndWordWord(emoji, word);

        if (wordEmoji == null) {
            sendMessage(slackChannel, "word/emoji combination doesn't exist, mondje pls");
        } else {
            wordEmojiRepository.delete(wordEmoji);
        }
    }

    private void handleAddCommand(SlackChannel slackChannel, String word, String emoji, Emoji emojiObj, WordTypeTypeEmoji wordObj, User user) {
        if (emojiObj == null) {
            emojiObj = new Emoji.EmojiBuilder().withEmoji(emoji).build();
            emojiRepository.save(emojiObj);
        }

        if (wordObj == null) {
            wordObj = new WordTypeTypeEmoji();
            wordObj.setWord(word);

            wordTypeEmojiRepository.save(wordObj);
        }

        if (user == null) {
            logger.error("Could not create emoji with user: '{}'", user.getName());
        }

        wordEmojiRepository.save(new WordEmoji.WordEmojiBuilder()
                .withEmoji(emojiObj)
                .withAddedBy(user)
                .withWord(wordObj)
                .build());

        logger.info("added word with word: '{}' and emoji: '{}' added by : '{}'", word, emoji, user.getName());

        sendMessage(slackChannel, String.format("added new word: '%s' with emoji: :%s:", word, emoji));
    }
}
