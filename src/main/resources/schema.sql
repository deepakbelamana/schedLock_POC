-- The table ShedLock uses to remember "who holds which lock".
-- One row per lock name.
CREATE TABLE IF NOT EXISTS shedlock (
    name       VARCHAR(64)  NOT NULL,   -- lock name, e.g. sendDailyEmailsJob
    lock_until TIMESTAMP(3) NOT NULL,   -- lock is held until this time
    locked_at  TIMESTAMP(3) NOT NULL,   -- when the lock was taken
    locked_by  VARCHAR(255) NOT NULL,   -- which app copy took it
    PRIMARY KEY (name)
);
