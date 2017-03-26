--
-- addon script; needed to delete and set the sequences again ; to be executed after data_stub.sql
-- 

drop sequence sequence_genericlevelid;
drop sequence sequence_userprofileid;
drop sequence sequence_producttypeid;
-- took out the dropping of the view-tables; separate script and ant task added to build script
-- needed so that the manually inserted entries dont prohibit future insertions because the sequence starts with 1
-- N O T E  that in PostgreSQL prior to version 7.4 the oracle syntax "create sequence xxx start with ..." does not work. hence this postgreSQL version of the script
create sequence sequence_genericlevelid start 100;
create sequence sequence_userprofileid start 100;
create sequence sequence_producttypeid start 100;
