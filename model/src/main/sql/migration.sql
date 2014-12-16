-----------------------------------------------------------------------------------------------
--                                                                                           --
-- Upgrade of db schema from version 0.0.19 to 1.0                                           --
--                                                                                           --
-----------------------------------------------------------------------------------------------

ALTER TABLE permission DROP CONSTRAINT CheckGroupOrUserIsFilled;

--
-- function which creates default access rights for existing reports
--

create or replace function reports_rights ()
  RETURNS setof permission AS
  '
  DECLARE
      reportrow report;
      permissionId integer;
	  groupId integer;
  BEGIN

  for reportrow in select * from report loop
	for groupId in select group_id from user_group where user_id=reportrow.user_id loop
		execute ''SELECT nextval(''''PERMISSION_SEQUENCE'''')'' into permissionId;
		execute ''insert into permission (id, access_type, access_level, group_id, report_id) VALUES ('' || permissionId || '', ''''WRITE'''', ''''GROUP'''', '' || groupId|| '', '' || reportrow.id|| '')'';
	end loop;
	  execute ''SELECT nextval(''''PERMISSION_SEQUENCE'''')'' into permissionId;
      execute ''insert into permission (id, access_type, access_level, report_id) VALUES ('' || permissionId|| '', ''''READ'''', ''''PUBLIC'''', '' || reportrow.id|| '')'';
  end loop;  
  return;
  END
  '
LANGUAGE plpgsql VOLATILE;

ALTER TABLE test_execution DROP COLUMN locked;

ALTER TABLE test_execution DROP COLUMN job_id;

ALTER TABLE user_group DROP COLUMN id;

ALTER TABLE 'user' ADD COLUMN first_name varchar(2047);

ALTER TABLE 'user' ADD COLUMN last_name varchar(2047);

ALTER TABLE permission ADD COLUMN report_id bigint;

UPDATE metric set comparator='HB';

