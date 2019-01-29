package com.team6.g.messageprocessors;

import com.team6.g.commands.AbstractCommand;
import com.team6.g.config.MuteConfig;
import com.team6.g.config.SlackConfig;
import com.team6.g.model.Emoji;
import com.team6.g.model.EmojiCount;
import com.team6.g.model.History;
import com.team6.g.model.User;
import com.team6.g.model.UserActivity;
import com.team6.g.model.UserActivityType;
import com.team6.g.model.WordCount;
import com.team6.g.model.WordTypeWordTypeCount;
import com.team6.g.repository.EmojiCountRepository;
import com.team6.g.repository.HistoryRepository;
import com.team6.g.repository.UserActivityRepository;
import com.team6.g.repository.UserRepository;
import com.team6.g.repository.WordCountRepository;
import com.team6.g.repository.WordEmojiRepository;
import com.team6.g.repository.WordTypeEmojiRepository;
import com.team6.g.repository.WordTypeWordCountRepository;
import com.team6.g.util.DateUtil;
import com.team6.g.util.MessageUtil;
import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackMessageHandle;
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted;
import com.ullink.slack.simpleslackapi.replies.EmojiSlackReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.team6.g.util.DateUtil.getWorkedTime;

@Component
public class PublicMessageProcessor extends AbstractMessageProcessor {
    private static final Logger logger = LoggerFactory.getLogger(PublicMessageProcessor.class);

    private final List<String> LOG_IN_WORDS = new ArrayList<>(Arrays.asList("mogguh", "aanwezig", "hennig laat", "hennig vroeg", "goeiemorgen", "goeie morgen"));
    private final List<String> LOG_OUT_WORDS = new ArrayList<>(Arrays.asList("zuk"));
    private final Integer STATS_TRIGGER_COUNT = 250;

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

    @Autowired
    EmojiCountRepository emojiCountRepository;

    @Autowired
    UserActivityRepository userActivityRepository;

    @Override
    protected Boolean internalProcess(SlackMessagePosted event, String message) {
        User user = userRepository.findByName(event.getSender().getUserName());

        if (user == null) {
            logger.info("creating user: '{}'", event.getSender().getUserName());
            user = userRepository.save(new User.UserBuilder().withName(event.getSender().getUserName()).build());
        }

        if ((MuteConfig.active != null && MuteConfig.active) && MuteConfig.userToMute.getUserName().equals(event.getSender().getUserName())) {
            slackConfig.slackSession().deleteMessage(event.getTimeStamp(), event.getChannel());
            return true;
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

        wordEmojiRepository.findAll().forEach(wordEmoji -> {
            // if word has a space in it, match it on the entire sentence
            if (wordEmoji.getWord().getWord().split(" ").length > 1 && message.toLowerCase().contains(wordEmoji.getWord().getWord().toLowerCase())) {
                addEmojiToMessage(slackEvent, wordEmoji.getEmoji(), user);
            } else {
                // parse word by word in sentence
                for (String sentenceWord : message.split(" ")) {
                    if (MessageUtil.isMatch(sentenceWord, wordEmoji.getWord().getWord())) {
                        addEmojiToMessage(slackEvent, wordEmoji.getEmoji(), user);
                    }
                }
            }
        });

        findWordsInSentence(slackEvent, user, message);
        
        if (user.getWorkPeriodMinutes() != null) {
            findTimesheetWords(slackEvent.getChannel(), user, message);
        }
    }

    private void findTimesheetWords(SlackChannel slackChannel, User user, String message) {
        List<String> wordsToMatch;
        UserActivityType userActivityType = null;

        if (DateUtil.isBeforeNoon(new Date())) {
            wordsToMatch = LOG_IN_WORDS;
            userActivityType = UserActivityType.LOG_IN;
        } else if (DateUtil.isAfterNoon(new Date())) {
            wordsToMatch = LOG_OUT_WORDS;
            userActivityType = UserActivityType.LOG_OUT;
        } else {
            return;
        }

        if (wordsToMatch.contains(message)) {
            UserActivity userActivity = userActivityRepository.findByDateTodayAndUser(user.getId());
            
            if (userActivityType == UserActivityType.LOG_OUT && (userActivity.getDateIn() != null && userActivity.getDateOut() == null)) {
                // already logged in , logout date null -> log out
                userActivity.setDateOut(new Date());
                userActivityRepository.save(userActivity);
                
                sendMessage(slackChannel, String.format("user : `%s` logged out, log in time was: `%s` worked : `%s`", user.getName(), userActivity.getDateIn(), getWorkedTime(userActivity.getDateIn(), userActivity.getDateOut())));
                sendMessage(slackChannel, String.format("user : `%s` has: `%s` overtime", user.getName(), DateUtil.calculateOverTime(userActivityRepository.findAllByUserAndDateInIsNotNullAndDateOutIsNotNull(user))));
            } else if (userActivityType == UserActivityType.LOG_IN && userActivity == null) {
                // not yet logged in today, logging in now
                userActivityRepository.save(new UserActivity.UserActivityBuilder().withUser(user).withDateIn(new Date()).withDateOut(null).build());
                sendMessage(slackChannel, String.format("user : `%s` has set activity: *%s* with date: %s", user.getName(), userActivityType, new Date()));
            } else if (userActivityType == UserActivityType.LOG_IN && userActivity.getDateIn() != null) {
                // already logged in
                sendMessage(slackChannel, String.format("user : `%s` is already logged in with date: `%s`", user.getName(), userActivity.getDateIn()));
            } else if (userActivityType == UserActivityType.LOG_OUT && userActivity.getDateOut() != null) {
                // already logged out
                sendMessage(slackChannel, String.format("user : `%s` is already logged out with date: `%s`", user.getName(), userActivity.getDateOut()));
            } else {
                logger.error("unknown route, user: '(}', userActivity: '{}'", user, userActivity);
            }
        }
    }

    private void findWordsInSentence(SlackMessagePosted slackMessagePosted, User user, String message) {
        wordTypeWordCountRepository.findAll().stream().forEach(word -> {
            if (word.getWord().split(" ").length > 1 && message.toLowerCase().contains(word.getWord().toLowerCase())) {
                addWordStatistics(slackMessagePosted, word, user);
            } else {
                // parse word by word in sentence
                for (String sentenceWord : message.split(" ")) {
                    if (MessageUtil.isMatch(sentenceWord, word.getWord())) {
                        addWordStatistics(slackMessagePosted, word, user);
                    }
                }
            }
        });
    }

    private void addWordStatistics(SlackMessagePosted slackMessagePosted, WordTypeWordTypeCount word, User user) {
        WordCount wordCount = new WordCount.WordCountBuilder().withWord(word).withUser(user).build();

        wordCountRepository.save(wordCount);
        
        checkIfWordHitsStats(slackMessagePosted.getChannel(), word, user);
    }

    private void addEmojiToMessage(SlackMessagePosted event, Emoji emoji, User user) {
        logger.info("adding emoji: '{}' to message: '{}'", String.format(":%s:", emoji.getEmoji()), event.getMessageContent());

        emojiCountRepository.save(new EmojiCount.EmojiCountBuilder().withUser(user).withEmoji(emoji).build());

        for (String singleEmoji : MessageUtil.parseEmoji(emoji)) {
            slackConfig.slackSession().addReactionToMessage(event.getChannel(), event.getTimeStamp(), singleEmoji);
        }
    }
    
    private void checkIfWordHitsStats(SlackChannel channel, WordTypeWordTypeCount word, User user) {
        int count = wordCountRepository.findByUserAndWord(user, word).size();

        if (count % STATS_TRIGGER_COUNT == 0) {
            // Message channel
            sendMessage(channel, ":partyparrot: :aussie_conga_parrot: :partyparrot: " + user.getName() + " has said ` " + word + "` a total of `" + count + "` times :partyparrot: :aussie_conga_parrot: :partyparrot:");

            List<History> historyList = historyRepository.findAll(new Sort(Sort.Direction.DESC, "id"));

            SlackMessageHandle<EmojiSlackReply> handle = slackConfig.slackSession().listEmoji();
            Map<String, String> emojis = handle.getReply().getEmojis();
            List keys = new ArrayList(emojis.keySet());

            // 24 emojis max per sentence
            for (int x = 0; x < 24; x++) {
                historyList.stream().limit(10).forEach(history -> {
                    new Thread(() -> slackConfig.slackSession().addReactionToMessage(channel, history.getTs(), (String) keys.get(new Random().nextInt(keys.size())))).run();
                });
            }
        }
    }
    
    private void addMessageToHistory(User user, String text, String timeStamp) {
        History history = new History.HistoryBuilder()
                .withUser(user)
                .withMessage(text)
                .withTs(timeStamp).build();

        historyRepository.save(history);
    }
}
