package com.team6.g.commands;

import com.team6.g.model.Emoji;
import com.team6.g.model.User;
import com.team6.g.model.WordEmoji;
import com.team6.g.model.WordTypeTypeEmoji;
import com.team6.g.model.statistics.EmojiCountStatistics;
import com.team6.g.repository.EmojiCountRepository;
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

    @Autowired
    EmojiCountRepository emojiCountRepository;
    
    @Override
    public void handle(SlackChannel slackChannel, User user, List<String> args) {
        if (args.size() < 2) {
            sendMessage(slackChannel, "!emoji stats <user>");
            sendMessage(slackChannel, "!emoji <add|remove> word emoji");
            return;
        }
        
        // !emoji stats
        if (args.get(1).equals("stats")) {
            logger.info("acquiring emojicount statistics for: '{}'", args.get(2));
            
            long totalLinesInEmojiCount = emojiCountRepository.findAll().size();
            List<EmojiCountStatistics> emojiCountStatistics;
            
            // !emoji stats maikel
            if (args.size() == 3 && !"all".equals(args.get(2))) {
                User lookupUser = userRepository.findByName(args.get(2));
                
                if (lookupUser == null) {
                    sendMessage(slackChannel, String.format("cannot find user : `%s`", args.get(2)));
                    return;
                }

                emojiCountStatistics = emojiCountRepository.findAllGroupByUser(user);
            } else if (args.size() == 3 && "all".equals(args.get(2))) {
                // !emoji stats all
                emojiCountStatistics = emojiCountRepository.findAllGroupByUser();
            } else {
                return;
            }

            int i = 1;
            StringBuilder sb = new StringBuilder();
            for (EmojiCountStatistics emojiCountStatistic : emojiCountStatistics) {
                if (i > 15) break;
                Long amountOfEmojisByName = emojiCountStatistic.getCount();
                float percentage = ((float) amountOfEmojisByName / totalLinesInEmojiCount) * 100;

                sb.append(messageFormatter(i,emojiCountStatistic, emojiCountStatistic.getCount(),percentage));
                i++;
            }

            sendMessage(slackChannel, sb.toString());
        } else if ("add".equals(args.get(1))) {
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
    }

    private String messageFormatter(Integer incr, EmojiCountStatistics emojiCountStatistics, Long count, float percentage) {
        return String.format("#_%d_ i have placed the emoji :%s: a total of `%d` times (%.1f%%) on *%s's* sentences\n", incr, emojiCountStatistics.getEmoji().getEmoji(), count, percentage, emojiCountStatistics.getUser().getName());
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
