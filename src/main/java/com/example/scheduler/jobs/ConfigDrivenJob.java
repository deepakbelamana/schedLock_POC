package com.example.scheduler.jobs;

import java.time.LocalTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * PART 2: Schedule config.
 *
 * Problem: if we hard-code "every 10 seconds" in the Java file, changing it means
 * editing code and redeploying.
 *
 * Solution: put the timing in application.properties and read it with ${...}.
 * Now ops/devs can change the timing (and even turn the job off) without touching Java code.
 * You can also override per environment: dev = every 10s, prod = every night at 2 AM.
 */
@Component
@ConditionalOnProperty(name = "jobs.report.enabled", havingValue = "true")   // job exists only if enabled = true
public class ConfigDrivenJob {

    private static final Logger log = LoggerFactory.getLogger(ConfigDrivenJob.class);

    @Value("${app.instance-name:#{T(java.lang.ProcessHandle).current().pid()}}")
    private String instance;

    @Scheduled(cron = "${jobs.report.cron}")   // <-- timing comes from application.properties
    public void generateReport() {
        log.info("[{}] [4] config-driven report job ran at {}", instance, LocalTime.now().withNano(0));
    }
}
