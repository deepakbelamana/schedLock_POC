package com.example.scheduler.config;

import javax.sql.DataSource;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * ShedLock setup.
 *
 * Think of it like a "token" kept in a shared database table.
 * Before a job runs, the app tries to grab the token.
 *  - Got it?      -> run the job.
 *  - Already taken -> skip this round (someone else is running it).
 */
@Configuration
@EnableSchedulerLock(defaultLockAtMostFor = "PT30S")   // safety: a lock is never held longer than 30s by default
public class ShedLockConfig {

    @Bean
    public LockProvider lockProvider(DataSource dataSource) {
        return new JdbcTemplateLockProvider(
                JdbcTemplateLockProvider.Configuration.builder()
                        .withJdbcTemplate(new JdbcTemplate(dataSource))
                        .usingDbTime()   // use the database's clock, so all app copies agree on "now"
                        .build()
        );
    }
}
