package com.example.scheduler.jobs;

import java.time.LocalTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * PART 1: The three basic ways to schedule a job.
 */
@Component
public class BasicJobs {

    private static final Logger log = LoggerFactory.getLogger(BasicJobs.class);
//
//    @Value("${app.instance-name:#{T(java.lang.ProcessHandle).current().pid()}}")
//    private String instance;
//
//    /**
//     * fixedRate = "start a new run every 5 seconds, no matter how long the last one took".
//     * Like a metronome: tick... tick... tick...
//     */
//    @Scheduled(fixedRate = 5000)
//    public void fixedRateJob() {
//        log.info("[{}] [1] fixedRate job ran at {}", instance, LocalTime.now().withNano(0));
//    }
//
//    /**
//     * fixedDelay = "wait 5 seconds AFTER the last run finishes, then run again".
//     * This job takes 2 seconds to finish, so you will see a gap of about 7 seconds between runs.
//     * (2 seconds of work + 5 seconds of waiting)
//     */
//    @Scheduled(fixedDelay = 5000, initialDelay = 2000)
//    public void fixedDelayJob() throws InterruptedException {
//        log.info("[{}] [2] fixedDelay job STARTED at {}", instance, LocalTime.now().withNano(0));
//        Thread.sleep(2000);   // pretend we are doing some work
//        log.info("[{}] [2] fixedDelay job FINISHED at {}", instance, LocalTime.now().withNano(0));
//    }
//
//    /**
//     * cron = "run at specific clock times".
//     * Spring cron has 6 parts:  second  minute  hour  day-of-month  month  day-of-week
//     *
//     * "*&#47;10 * * * * *"  means  "every 10 seconds".
//     * Other examples:
//     *   "0 0 9 * * *"      -> every day at 9:00:00 AM
//     *   "0 *&#47;5 * * * *"    -> every 5 minutes
//     *   "0 0 9 * * MON-FRI" -> 9 AM on weekdays only
//     */
//    @Scheduled(cron = "*/10 * * * * *")
//    public void cronJob() {
//        log.info("[{}] [3] cron job ran at {}", instance, LocalTime.now().withNano(0));
//    }
}
