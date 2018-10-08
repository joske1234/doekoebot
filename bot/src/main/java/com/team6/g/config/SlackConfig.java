package com.team6.g.config;

import com.team6.g.subscription.MessagePostedListener;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.net.Proxy;

@Configuration
@PropertySource("classpath:slack.properties")
public class SlackConfig {
    private static final Logger logger = LoggerFactory.getLogger(SlackConfig.class);

    @Value("${slack.channel:doekoe}")
    private String channel;

    @Value("${slack.api.key}")
    private String key;

    @Value("${threads.core.pool.size}")
    private Integer corePoolSize;

    @Value("${threads.core.max.pool.size}")
    private Integer maxPoolSize;

    @Value("${threads.core.queue.capicity}")
    private Integer queueCapicity;

    @Autowired
    public MessagePostedListener messagePostedListener;


    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(this.corePoolSize);
        executor.setMaxPoolSize(this.maxPoolSize);
        executor.setQueueCapacity(this.queueCapicity);

        return executor;
    }

    @Bean
    public SlackSession slackSession() {
        SlackSession slackSession = SlackSessionFactory.getSlackSessionBuilder(this.key).withAutoreconnectOnDisconnection(true).build();
        
        logger.info("registering listener: messagePostedListener");

        slackSession.addMessagePostedListener(messagePostedListener);

        return slackSession;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(Integer corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public Integer getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(Integer maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public Integer getQueueCapicity() {
        return queueCapicity;
    }

    public void setQueueCapicity(Integer queueCapicity) {
        this.queueCapicity = queueCapicity;
    }
}
