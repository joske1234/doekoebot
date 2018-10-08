package com.team6.g.migrate;

import com.team6.g.model.Emoji;
import com.team6.g.model.History;
import com.team6.g.model.User;
import com.team6.g.model.Word;
import com.team6.g.model.WordCount;
import com.team6.g.model.WordEmoji;
import com.team6.g.repository.EmojiRepository;
import com.team6.g.repository.HistoryRepository;
import com.team6.g.repository.UserRepository;
import com.team6.g.repository.WordCountRepository;
import com.team6.g.repository.WordEmojiRepository;
import com.team6.g.repository.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.Optional;
import java.util.Scanner;

@Component
public class Migrate {
    @Autowired
    WordRepository wordRepository;
    
    @Autowired
    WordCountRepository wordCountRepository;
    
    @Autowired
    UserRepository userRepository;
    
    @Autowired
    EmojiRepository emojiRepository;
    
    @Autowired
    WordEmojiRepository wordEmojiRepository;
    
    @Autowired
    HistoryRepository historyRepository;
    
    
    public void migrate() {
        //words();
        //wordCounts();
        //emojisAndWords(); 
        //emojiscount();
        history();
    }

    private void history() {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("history.txt").getFile());

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String userStr = line.split("\\%PLACEHOLDER\\%")[0];
                String text = line.split("\\%PLACEHOLDER\\%")[1];
                String dateStr = line.split("\\%PLACEHOLDER\\%")[2];
                
                String slackDt = null;
                
                if (line.split("\\%PLACEHOLDER\\%").length > 3) {
                    slackDt = line.split("\\%PLACEHOLDER\\%")[3];
                }
                
                System.out.println(userStr + "   " + text + "  " + dateStr);
                
                User user = userRepository.findByName(userStr);
                Date date = new Date(Long.valueOf(dateStr));
                
                History history = new History.HistoryBuilder().withTs(slackDt).withMessage(text).withUser(user).withDateAdded(date).build();
                historyRepository.save(history);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void emojiscount() {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            File file = new File(classLoader.getResource("emojis.txt").getFile());

            try (Scanner scanner = new Scanner(file)) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();

                    Word word = wordRepository.findByWord(line.split(",")[0]);
                    Date date = new Date(Long.valueOf(line.split(",")[2]));
                    String emojiStr = line.split(",")[1];
                    Emoji emoji = emojiRepository.findByEmoji(emojiStr);
                    
                    if (word != null && emoji != null) {

                        wordEmojiRepository.save(new WordEmoji.WordEmojiBuilder().withWord(word).withEmoji(emoji).withAddedBy(userRepository.findByName("maikel")).withDateAdded(date).build());
                    } else {
                        System.out.println("FAIL " + line);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception ignore) {}
    }

    private void emojisAndWords() {

        try {
            ClassLoader classLoader = getClass().getClassLoader();
            File file = new File(classLoader.getResource("emojis.txt").getFile());

            try (Scanner scanner = new Scanner(file)) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
    
                    String emojiStr = line.split(",")[1];
                    String wordStr = line.split(",")[0];
                    Emoji emoji = emojiRepository.findByEmoji(emojiStr);
                    Word word = wordRepository.findByWord(wordStr);
                    Date date = new Date(Long.valueOf(line.split(",")[2]));
                    
                    if (emoji == null) {
                        emojiRepository.save(new Emoji.EmojiBuilder().withEmoji(emojiStr).withDateAdded(date).build());
                    }
    
                    if (word == null) {
                        wordRepository.save(new Word.WordBuilder().withWord(wordStr).withDateAdded(date).build());
                    }
                    
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }
    
    private void wordCounts() {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("wordcounts.txt").getFile());

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                
                //nicom,1,1486477806701
                User u = userRepository.findByName(line.split(",")[0]);
                Optional<Word> w = wordRepository.findById(Long.valueOf(line.split(",")[1]));
                Date date = new Date(Long.valueOf(line.split(",")[2]));
                
                if (u != null && w.get() != null) {
                    wordCountRepository.save(new WordCount.WordCountBuilder().withDateAdded(date).withUser(u).withWord(w.get()).build());
                } else {
                    System.out.println(line + " is null");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void words() {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("words.txt").getFile());

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                wordRepository.save(new Word.WordBuilder().withWord(line).build());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
}
