BEGIN;

alter table artifactversionnotification add column status varchar(255);
update artifactversionnotification set status='SENT';

COMMIT;