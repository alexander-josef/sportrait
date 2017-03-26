-- update userprofile to contain the google id instead of the old username "admin"
-- delete not used mappings in userprofile 2 userrole mapping (old registrations ?)
-- change the mapping table to contain the PKs instead of the username (as it was needed by securityFilter)
update userprofiles set username =  '106605843591930483394' where username = 'admin';
delete from userprofiles2userroles where username <> 'admin';
update userprofiles2userroles set username = '1'  where username = 'admin';
alter table userprofiles2userroles alter column username type bigint using username::bigint;
