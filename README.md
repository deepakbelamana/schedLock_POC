# Scheduler POC: scheduled jobs, schedule config and schedule lock

A small Spring Boot project to learn three things, one at a time:

1. **Scheduled jobs**: make a method run by itself on a timer
2. **Schedule config**: keep the timing in `application.properties` instead of in Java code
3. **Schedule lock (ShedLock)**: when you run 2+ copies of the app, make sure only ONE copy runs the job

---

## The ideas in plain words

### 1. Scheduled job = an alarm clock for a method
You write a normal method, put `@Scheduled` on it, and Spring calls it for you again and again.
Nobody has to click anything. Examples: send reminder emails, clean old data, make a nightly report.

You need `@EnableScheduling` once in the app. It is the master switch. No switch, no alarms.

Three ways to set the alarm:

| Style | Meaning | Real-life example |
|---|---|---|
| `fixedRate = 5000` | Start every 5 seconds, even if the last run is still going | A metronome: tick, tick, tick |
| `fixedDelay = 5000` | Wait 5 seconds AFTER the last run finishes | "Take a 5 minute break after each lecture" |
| `cron = "..."` | Run at exact clock times | "Every day at 9 AM" |

A cron has 6 parts: `second minute hour day-of-month month day-of-week`

- `*/10 * * * * *`: every 10 seconds
- `0 0 9 * * *`: every day at 9:00 AM
- `0 0 9 * * MON-FRI`: 9 AM, weekdays only

### 2. Schedule config = move the timing out of the code
If "every 20 seconds" is written inside the Java file, changing it means editing code and deploying again.
Instead, write it in `application.properties`:

```
jobs.report.cron=*/20 * * * * *
```

and in Java use `@Scheduled(cron = "${jobs.report.cron}")`.

Now you can change the timing without touching Java. Dev can run every 10 seconds, prod can run once a night.
You can also add `jobs.report.enabled=false` to switch the job off completely.

### 3. Schedule lock = "only one person can hold the pen"
In real projects your app runs as many copies (servers or pods). Each copy has its own alarm clock.
So at 9 AM, **every copy** runs "send daily emails", and customers get 3 emails.

ShedLock fixes this with a shared notebook (a database table called `shedlock`):

1. At 9 AM, all copies try to write their name in the notebook against `sendDailyEmailsJob`.
2. Only the first one succeeds. It runs the job.
3. The others see the name is already there and skip this round.

The two settings you must understand:

- `lockAtMostFor`: a safety timer. If the copy that took the lock crashes mid-job, the lock opens by itself after this time. Set it **longer than your job normally takes**.
- `lockAtLeastFor`: the lock stays closed at least this long, even if the job finishes early. This stops another copy from running it again right after.

---

## Project layout

```
src/main/java/com/example/scheduler/
  SchedulerPocApplication.java      -> @EnableScheduling (master switch)
  config/ShedLockConfig.java        -> tells ShedLock to use the database for the lock
  jobs/BasicJobs.java               -> Part 1: fixedRate, fixedDelay, cron
  jobs/ConfigDrivenJob.java         -> Part 2: timing read from application.properties
  jobs/LockedJob.java               -> Part 3: @SchedulerLock
src/main/resources/
  application.properties            -> all the timings live here
  schema.sql                        -> creates the shedlock table
```

---

## How to run

You need Java 17+ and Maven.

**Terminal 1 (copy A):**
```
mvn spring-boot:run
```

**Terminal 2 (copy B), same folder:**
```
mvn spring-boot:run -Dspring-boot.run.arguments="--app.instance-name=B --server.port=8081"
```

Both must be started from this same folder, because they share the database file in `./data`.

---

## What you should see

Every log line starts with the copy name: `[A]` or `[B]`.

- Jobs 1 to 4 (`fixedRate`, `fixedDelay`, `cron`, config-driven) print from **both** A and B. Nothing stops them, so each copy runs its own.
- Job 5 (the **LOCKED** one) runs every 15 seconds, and only **one** of A or B prints `STARTED` each time.
  It may be A one time and B the next. Whoever wins the race runs it.

That one difference is the whole point of ShedLock.

---

## Try these experiments

1. **Stop ShedLock and watch it break.** Comment out the `@SchedulerLock(...)` line in `LockedJob`, restart both copies, and you will see A and B both run job 5.
2. **Change the timing without code.** Edit `jobs.report.cron` to `*/5 * * * * *`, restart, and the report job speeds up.
3. **Turn a job off.** Set `jobs.report.enabled=false` and restart. The job disappears.
4. **See the crash safety net.** While job 5 is running (it sleeps 3 seconds), kill that copy with Ctrl+C. The other copy will pick the job up on a later round once the lock expires.
5. **Look inside the notebook.** Open the H2 database file in `./data` (or add the H2 console) and run `SELECT * FROM shedlock;` to see `locked_by` and `lock_until`.

---

## Things that trip up beginners

- **Default scheduler has 1 thread.** With 1 thread, a slow job makes the others wait. We set `spring.task.scheduling.pool.size=4` to avoid this.
- **ShedLock does not make jobs run less often.** It only stops two copies from running the *same scheduled moment* together.
- **Clocks matter.** `usingDbTime()` makes every copy use the database's clock, so a copy with a wrong system clock can't cheat.
- **Real projects use a real database** (Postgres, MySQL, Oracle...). H2 is only here so you need no setup.
