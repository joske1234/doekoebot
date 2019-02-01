package com.team6.g.commands;

import com.team6.g.model.History;
import com.team6.g.model.User;
import com.team6.g.model.statistics.HistoryStatistics;
import com.team6.g.repository.HistoryRepository;
import com.team6.g.util.DateUtil;
import com.ullink.slack.simpleslackapi.SlackChannel;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public class HistoryCommandHelper extends AbstractCommand {
    private static final Logger logger = LoggerFactory.getLogger(HistoryCommandHelper.class);

    @Autowired
    HistoryRepository historyRepository;

    @Override
    public void handle(SlackChannel slackChannel, User user, List<String> args) {
        // !history stats date 11/10/2018
        // !history stats
        if ("stats".equals(args.get(1))) {
            try {
                if (args.size() > 3) {
                    if ("date".equals(args.get(2))) {
                        logger.info("acquiring history stats with date: '{}'", args.get(2));
                        handleStatsDateCommand(slackChannel, args.get(3));
                    } else {
                        sendMessage(slackChannel, "nope, !history stats date <dd/MM/yyyy> or !history stats");
                    }

                } else if (args.size() == 3) {
                    // !history stats top
                    if ("top".equals(args.get(2))) {
                        handleStatsTopCommand(slackChannel);
                    }
                } else {
                    logger.info("acquiring history stats");
                    handleStatsCommand(slackChannel);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if ("export".equals(args.get(1))) {
            // !history export 20/03/2018
            SimpleDateFormat parser = new SimpleDateFormat("dd/MM/yyyy");
            try {
                Date date = parser.parse(args.get(2));

                List<History> historyList = historyRepository.findAllByDateAddedBetween(DateUtil.atStartOfDay(date), DateUtil.atEndOfDay(date));

                StringBuilder sb = new StringBuilder();

                for (History hist : historyList) {
                    sb.append(String.format("%s <%s> %s\n", new SimpleDateFormat("HH:mm:ss").format(hist.getDateAdded()), hist.getUser().getName(), hist.getMessage()));
                }

                slackConfig.slackSession().sendFile(slackChannel, sb.toString().getBytes(), args.get(2) + ".txt");
            } catch (ParseException e) {
                sendMessage(slackChannel, "invalid date string. dd/MM/yyyy");
            }
        }
    }

    private void handleStatsTopCommand(SlackChannel slackChannel) {
        List<HistoryStatistics> historyStatistics = historyRepository.findAllGroupByDate();
        StringBuilder sb = new StringBuilder();

        int i = 1;
        for (HistoryStatistics historyStats : historyStatistics) {
            if (i > 10) break;

            int totalLinesByUser = historyRepository.findAllByDateAddedBetween(getStartOfDay(historyStats.getDate()), getEndOfDay(historyStats.getDate())).size();
            HistoryStatistics userStats = historyRepository.findByDateBetweenAndGroup(getStartOfDay(historyStats.getDate()), getEndOfDay(historyStats.getDate())).get(0);
            float percentage = ((float) userStats.getCount() / totalLinesByUser) * 100;

            sb.append(topMessageFormatter(i, historyStats.getDate(), historyStats.getCount(), userStats.getUser(), userStats.getCount(), percentage));

            i++;
        }
        sendMessage(slackChannel, sb.toString());
    }

    private void handleStatsCommand(SlackChannel slackChannel) {
        int totalLinesInHistory = historyRepository.findAll().size();
        List<HistoryStatistics> historyStatistics = historyRepository.findAllGroupByUser();

        parseHistoryStatsCommand(slackChannel, totalLinesInHistory, historyStatistics);
    }

    private void handleStatsDateCommand(SlackChannel slackChannel, String date) throws ParseException {
        SimpleDateFormat parser = new SimpleDateFormat("dd/MM/yyyy");
        Date dateObj = parser.parse(date);

        int totalLinesInHistory = historyRepository.findAllByDateAddedBetween(getStartOfDay(dateObj), getEndOfDay(dateObj)).size();
        List<HistoryStatistics> historyStatistics = historyRepository.findByDateBetweenAndGroup(getStartOfDay(dateObj), getEndOfDay(dateObj));

        parseHistoryStatsCommand(slackChannel, totalLinesInHistory, historyStatistics);
    }

    private void parseHistoryStatsCommand(SlackChannel slackChannel, int totalLinesInHistory, List<HistoryStatistics> historyStatistics) {
        StringBuilder sb = new StringBuilder();
        int i = 1;

        for (HistoryStatistics historyStats : historyStatistics) {
            if (i > 30) break;
            Long amountOfSentencesByName = historyStats.getCount();
            float percentage = ((float) amountOfSentencesByName / totalLinesInHistory) * 100;

            sb.append(messageFormatter(i, historyStats.getUser().getName(), historyStats.getCount(), percentage));
            i++;
        }

        sendMessage(slackChannel, sb.toString());
        sendMessage(slackChannel, String.format("total of `%d` messages (:nicom: #1 inc)", totalLinesInHistory));
    }

    private String topMessageFormatter(Integer incr, Date date, Long count, User user, long totalLinesByUser, float percentage) {
        SimpleDateFormat dateOnly = new SimpleDateFormat("dd/MM/yyyy");

        return String.format("#_%d_ `%d` sentences said on `%s`. *%s* takes the lead with `%d` sentences (%.1f%%)\n", incr, count, dateOnly.format(date), user.getName(), totalLinesByUser, percentage);
    }
    
    private String messageFormatter(Integer incr, String user, Long count, float percentage) {
        return String.format("#_%d_ *%s* has said `%d` sentences (%.1f%%)\n", incr, user, count, percentage);
    }

    private Date getEndOfDay(Date date) {
        return DateUtils.addMilliseconds(DateUtils.ceiling(date, Calendar.DATE), -1);
    }

    private Date getStartOfDay(Date date) {
        return DateUtils.truncate(date, Calendar.DATE);
    }
}
