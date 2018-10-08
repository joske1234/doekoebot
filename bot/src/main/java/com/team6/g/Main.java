package com.team6.g;

import com.team6.g.repository.QuoteRepository;
import com.team6.g.repository.UserRepository;
import com.ullink.slack.simpleslackapi.SlackSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableCaching
@EnableAsync
@ComponentScan
public class Main implements CommandLineRunner {
    @Autowired
    SlackSession slackSession;

    @Autowired
    UserRepository userRepository;

    @Autowired
    QuoteRepository quoteRepository;

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
    
    @Override
    public void run(String... args) throws Exception {
        slackSession.connect();
    }
}
