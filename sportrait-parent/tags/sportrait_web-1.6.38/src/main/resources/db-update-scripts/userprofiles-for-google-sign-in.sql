Update userprofiles set username =  '106605843591930483394' where username = 'admin';
update userprofiles2userroles set username = '1'  where username = 'admin';
delete from userprofiles2userroles where username <> 'admin';
alter table userprofiles2userroles alter column username type bigint using username::bigint;
