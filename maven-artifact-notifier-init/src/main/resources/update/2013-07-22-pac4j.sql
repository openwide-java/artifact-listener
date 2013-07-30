BEGIN;

ALTER TABLE user_ RENAME COLUMN openididentifier TO remoteidentifier;

COMMIT;
