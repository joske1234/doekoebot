package com.team6.g.commands;

import com.team6.g.model.User;
import com.team6.g.model.Word;
import com.team6.g.model.WordCountStatistics;
import com.team6.g.repository.UserRepository;
import com.team6.g.repository.WordCountRepository;
import com.team6.g.repository.WordRepository;
import com.ullink.slack.simpleslackapi.SlackChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WordCommandHelper extends AbstractCommand {
    private static final Logger logger = LoggerFactory.getLogger(WordCommandHelper.class);

    @Autowired
    UserRepository userRepository;

    @Autowired
    WordCountRepository wordCountRepository;

    @Autowired
    WordRepository wordRepository;

    @Override
    public void handle(SlackChannel slackChannel, User user, List<String> args) {
        if (args.size() > 1 && "stats".equals(args.get(1))) {
            String userStr = args.get(2);
            String wordStr = args.size() == 4 ? args.get(3) : null;

            // !word stats top 0-25
            if (args.size() == 4 && "top".equals(userStr)) {
                printTopStats(slackChannel, Integer.valueOf(wordStr));
                return;
            }

            // !words stats all
            if (args.size() == 3 && "all".equals(userStr)) {
                printAllStats(slackChannel);
                return;
            }

            User statsUser = userRepository.findByName(userStr);
            Word word = wordRepository.findByWord(wordStr);
            
            // !word stats maikel test1
            if (args.size() == 4 && (word != null && statsUser != null)) {
                printUserWordStats(slackChannel, statsUser, word);
                return;
            }

            // !word stats maikel
            if (args.size() == 3 && (word == null && statsUser != null)) {
                printSpecificUserStats(slackChannel, statsUser);
                return;
            }
        }
        
        // !word list
        if ("list".equals(args.get(1))) {
            List<Word> wordList = wordRepository.findAll();
            StringBuilder sb = new StringBuilder();

            wordList.forEach(word -> sb.append(
                    String.format("`%s`, ", word.getWord())
            ));

            sendMessage(slackChannel, String.format("Words list (%d words): %s", wordList.size(), sb.toString().substring(0, sb.toString().length() - 2)));
        }

        // !word add
        if ("add".equals(args.get(1))) {
            Word word = wordRepository.findByWord(args.get(2));

            if (word == null) {
                wordRepository.save(new Word.WordBuilder().withWord(args.get(2)).build());
                sendMessage(slackChannel, String.format("Word: `%s` successfully added", args.get(2)));
            } else {
                sendMessage(slackChannel, String.format("Word already exists, n00b"));
            }
        }
        
        // !word search
        if ("search".equals(args.get(1))) {
            List<Word> word = wordRepository.findByWordContaining(args.get(2));
            
            sendMessage(slackChannel, String.format("debug mode yo: %s", word.toString()));
        }
        
        // !word remove
        if ("remove".equals(args.get(1)) && "maikel".equals(user.getName())) {
            Word word = wordRepository.findByWord(args.get(2));
            
            if (word != null) {
                wordRepository.delete(word);
                sendMessage(slackChannel, String.format("%s deleted", word.getWord()));
            } else {
                sendMessage(slackChannel, "nee");
            }
        }
    }
    
    private void printTopStats(SlackChannel slackChannel, Integer top) {
        if (top > 20 || top < 1) {
            sendMessage(slackChannel, "uh no?");
            return;
        }
        
        printWordCountStatisticsList(wordCountRepository.findStatistics(), slackChannel, top + 1);
    }

    private void printAllStats(SlackChannel slackChannel) {
        printWordCountStatisticsList(wordCountRepository.findStatistics(), slackChannel, null);
    }
    
    private void printUserWordStats(SlackChannel slackChannel, User user, Word word) {
        List<WordCountStatistics> wordCountStatisticsList = wordCountRepository.findAllUserStatisticsGroupByUser(word);
        int position = 1;
        
        for (WordCountStatistics wordCountStatistics : wordCountStatisticsList) {
            if (wordCountStatistics.getUser().getId() == user.getId()) {
                break;
            }
            position++;
        }

        printWordCountStatisticsList(wordCountRepository.findStatisticsByUserAndWord(user, word), slackChannel, position);
    }

    private void printSpecificUserStats(SlackChannel slackChannel, User user) {
        printWordCountStatisticsList(wordCountRepository.findStatisticsByUser(user), slackChannel, null);
    }

    private void printWordCountStatisticsList(List<WordCountStatistics> wordCountStatisticsList, SlackChannel slackChannel, Integer incr) {
        StringBuilder sb = new StringBuilder();
        int[] i = {1};

        wordCountStatisticsList.forEach(wordCountStatistics -> {
            long count = wordCountStatistics.getCount();
            float percent = ((float) count / wordCountRepository.findByWord(wordCountStatistics.getWord()).size()) * 100;

            if (incr == null) {
                sb.append(messageFormatter(i[0], wordCountStatistics.getUser().getName(), wordCountStatistics.getWord().getWord(), count, percent));
            } else if (incr > 1) {
                sb.append(messageFormatter(incr, wordCountStatistics.getUser().getName(), wordCountStatistics.getWord().getWord(), count, percent));
            } else {
                if (i[0] <= incr) {
                    sb.append(messageFormatter(i[0], wordCountStatistics.getUser().getName(), wordCountStatistics.getWord().getWord(), count, percent));
                }
            }
            
            i[0]++;
        });

        sendMessage(slackChannel, sb.toString());
    }

    private String messageFormatter(Integer incr, String user, String key, Long amount, float percentage) {
        return String.format("#_%d_ *%s* has said `%s` a total of `%d` (%.1f%%) times!\n", incr, user, key, amount, percentage);
    }
}
