package com.example.scheduler.jobs;

import java.time.LocalTime;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * PART 3: Schedule lock (ShedLock).
 *
 * Problem: in real life your app runs as 2, 3, or 10 copies (servers/pods).
 * Every copy has its own scheduler, so a job like "send daily emails" would run
 * once PER COPY -> customers get 3 emails. Bad!
 *
 * Solution: before running, each copy tries to grab a lock stored in a shared database.
 * Only the copy that gets the lock runs the job. The others skip.
 */
@Component
public class LockedJob {

    private static final Logger log = LoggerFactory.getLogger(LockedJob.class);

    @Value("${app.instance-name:#{T(java.lang.ProcessHandle).current().pid()}}")
    private String instance;

    @Scheduled(cron = "${jobs.locked.cron}")
    @SchedulerLock(
            name = "sendDailyEmailsJob",   // the lock's name. Same name = same lock across all copies.
            lockAtMostFor = "PT20S",       // safety net: if the copy crashes mid-job, the lock auto-expires after 20s
            lockAtLeastFor = "PT5S"
    )
    public void sendDailyEmails() throws InterruptedException {
        log.info("[{}] [5] LOCKED job STARTED at {}  <-- only one copy should print this", instance, LocalTime.now().withNano(0));
        Thread.sleep(18000);   // pretend we are sending emails
        log.info("[{}] [5] LOCKED job FINISHED at {}", instance, LocalTime.now().withNano(0));
    }
}
