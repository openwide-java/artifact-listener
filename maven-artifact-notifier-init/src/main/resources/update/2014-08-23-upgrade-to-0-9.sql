BEGIN;

alter table user__usergroup rename column usergroups_id to groups_id;

COMMIT;