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


-----------------------------------------------------------------------------------------------
--                                                                                           --
-- Upgrade of db schema from version 1.1 to 1.2 - added alerting feature                                           --
--                                                                                           --
-----------------------------------------------------------------------------------------------


CREATE TABLE test_subscriber (
    test_id bigint NOT NULL,
    user_id bigint NOT NULL
);

ALTER TABLE public.test_subscriber OWNER TO perfrepo;

ALTER TABLE ONLY public.test_subscriber
    ADD CONSTRAINT test_subscriber_pkey PRIMARY KEY (test_id, user_id);

ALTER TABLE ONLY public.test_subscriber
    ADD CONSTRAINT test_subscriber_test_fkey FOREIGN KEY (test_id) REFERENCES test(id);

ALTER TABLE ONLY public.test_subscriber
    ADD CONSTRAINT test_subscriber_user_fkey FOREIGN KEY (user_id) REFERENCES "user"(id);

CREATE INDEX test_subscriber_test ON test_subscriber(test_id);
CREATE INDEX test_subscriber_user ON test_subscriber(user_id);


CREATE TABLE alert (
  id bigint NOT NULL,
  name character varying(2097) NOT NULL,
  description character varying(2097) NOT NULL,
  condition character varying(2097) NOT NULL,
  metric_id bigint NOT NULL,
  test_id bigint NOT NULL
);


ALTER TABLE public.alert OWNER TO perfrepo;
ALTER TABLE ONLY public.alert
ADD CONSTRAINT alert_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.alert
ADD CONSTRAINT alert_metric_fkey FOREIGN KEY (metric_id) REFERENCES metric(id);
ALTER TABLE ONLY public.alert
ADD CONSTRAINT alert_test_fkey FOREIGN KEY (test_id) REFERENCES test(id);
CREATE INDEX alert_metric_id ON alert(metric_id);
CREATE INDEX alert_test_id ON alert(test_id);

CREATE SEQUENCE alert_sequence
START WITH 1
INCREMENT BY 1
NO MAXVALUE
NO MINVALUE
CACHE 1;

-----------------------------------------------------------------------------------------------
--                                                                                           --
-- Upgrade of db schema in version 1.2 - enhancement of alerting feature                     --
--                                                                                           --
-----------------------------------------------------------------------------------------------

CREATE TABLE alert_tag (
    alert_id bigint NOT NULL,
    tag_id bigint NOT NULL
);

ALTER TABLE public.alert_tag OWNER TO perfrepo;

ALTER TABLE ONLY public.alert_tag
    ADD CONSTRAINT alert_tag_pkey PRIMARY KEY (alert_id, tag_id);

ALTER TABLE ONLY public.alert_tag
    ADD CONSTRAINT alert_tag_alert_fkey FOREIGN KEY (alert_id) REFERENCES alert(id);

ALTER TABLE ONLY public.alert_tag
    ADD CONSTRAINT alert_tag_tag_fkey FOREIGN KEY (tag_id) REFERENCES tag(id);

CREATE INDEX alert_tag_alert ON alert_tag(alert_id);
CREATE INDEX alert_tag_tag ON alert_tag(tag_id);
