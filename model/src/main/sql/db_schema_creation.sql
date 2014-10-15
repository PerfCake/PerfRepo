--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: metric; Type: TABLE; Schema: public; Owner: perfrepo; Tablespace: 
--

CREATE TABLE metric (
    id bigint NOT NULL,
    comparator character varying(255),
    name character varying(2047) NOT NULL,
    description character varying(10239) NOT NULL
);


ALTER TABLE public.metric OWNER TO perfrepo;

--
-- Name: metric_sequence; Type: SEQUENCE; Schema: public; Owner: perfrepo
--

CREATE SEQUENCE metric_sequence
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.metric_sequence OWNER TO perfrepo;

--
-- Name: tag; Type: TABLE; Schema: public; Owner: perfrepo; Tablespace: 
--

CREATE TABLE tag (
    id bigint NOT NULL,
    name character varying(255)
);


ALTER TABLE public.tag OWNER TO perfrepo;

--
-- Name: tag_sequence; Type: SEQUENCE; Schema: public; Owner: perfrepo
--

CREATE SEQUENCE tag_sequence
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.tag_sequence OWNER TO perfrepo;

--
-- Name: test; Type: TABLE; Schema: public; Owner: perfrepo; Tablespace: 
--

CREATE TABLE test (
    id bigint NOT NULL,
    groupid character varying(255) NOT NULL,
    name character varying(2047) NOT NULL,
    uid character varying(2047) NOT NULL,
    description character varying(10239) NOT NULL
);


ALTER TABLE public.test OWNER TO perfrepo;

--
-- Name: test_execution; Type: TABLE; Schema: public; Owner: perfrepo; Tablespace: 
--

CREATE TABLE test_execution (
    id bigint NOT NULL,
    name character varying(255) NOT NULL,
    test_id bigint NOT NULL,
    started timestamp without time zone NOT NULL,
    locked boolean DEFAULT false NOT NULL
);


ALTER TABLE public.test_execution OWNER TO perfrepo;

--
-- Name: test_execution_attachment; Type: TABLE; Schema: public; Owner: perfrepo; Tablespace: 
--

CREATE TABLE test_execution_attachment (
    id bigint NOT NULL,
    content oid NOT NULL,
    filename character varying(2047) NOT NULL,
    mimetype character varying(255) NOT NULL,
    test_execution_id bigint NOT NULL
);


ALTER TABLE public.test_execution_attachment OWNER TO perfrepo;

--
-- Name: test_execution_attachment_sequence; Type: SEQUENCE; Schema: public; Owner: perfrepo
--

CREATE SEQUENCE test_execution_attachment_sequence
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.test_execution_attachment_sequence OWNER TO perfrepo;

--
-- Name: test_execution_parameter; Type: TABLE; Schema: public; Owner: perfrepo; Tablespace: 
--

CREATE TABLE test_execution_parameter (
    id bigint NOT NULL,
    name character varying(2047),
    value character varying(2047),
    test_execution_id bigint NOT NULL
);


ALTER TABLE public.test_execution_parameter OWNER TO perfrepo;

--
-- Name: test_execution_parameter_sequence; Type: SEQUENCE; Schema: public; Owner: perfrepo
--

CREATE SEQUENCE test_execution_parameter_sequence
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.test_execution_parameter_sequence OWNER TO perfrepo;

--
-- Name: test_execution_sequence; Type: SEQUENCE; Schema: public; Owner: perfrepo
--

CREATE SEQUENCE test_execution_sequence
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.test_execution_sequence OWNER TO perfrepo;

--
-- Name: test_execution_tag; Type: TABLE; Schema: public; Owner: perfrepo; Tablespace: 
--

CREATE TABLE test_execution_tag (
    id bigint NOT NULL,
    tag_id bigint NOT NULL,
    test_execution_id bigint NOT NULL
);


ALTER TABLE public.test_execution_tag OWNER TO perfrepo;

--
-- Name: test_execution_tag_sequence; Type: SEQUENCE; Schema: public; Owner: perfrepo
--

CREATE SEQUENCE test_execution_tag_sequence
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.test_execution_tag_sequence OWNER TO perfrepo;

--
-- Name: test_metric; Type: TABLE; Schema: public; Owner: perfrepo; Tablespace: 
--

CREATE TABLE test_metric (
    id bigint NOT NULL,
    metric_id bigint NOT NULL,
    test_id bigint NOT NULL
);


ALTER TABLE public.test_metric OWNER TO perfrepo;

--
-- Name: test_metric_sequence; Type: SEQUENCE; Schema: public; Owner: perfrepo
--

CREATE SEQUENCE test_metric_sequence
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.test_metric_sequence OWNER TO perfrepo;

--
-- Name: test_sequence; Type: SEQUENCE; Schema: public; Owner: perfrepo
--

CREATE SEQUENCE test_sequence
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.test_sequence OWNER TO perfrepo;

--
-- Name: user; Type: TABLE; Schema: public; Owner: perfrepo; Tablespace: 
--

CREATE TABLE "user" (
    id bigint NOT NULL,
    username character varying(2047) NOT NULL,
    first_name character varying(2047) NOT NULL,
    last_name character varying(2047) NOT NULL,
    email character varying(2047) NOT NULL
);


ALTER TABLE public."user" OWNER TO perfrepo;

--
-- Name: user_property; Type: TABLE; Schema: public; Owner: perfrepo; Tablespace: 
--

CREATE TABLE user_property (
    id bigint NOT NULL,
    user_id bigint NOT NULL,
    name character varying(2047) NOT NULL,
    value character varying(2047) NOT NULL
);


ALTER TABLE public.user_property OWNER TO perfrepo;

--
-- Name: user_property_sequence; Type: SEQUENCE; Schema: public; Owner: perfrepo
--

CREATE SEQUENCE user_property_sequence
    START WITH 100
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.user_property_sequence OWNER TO perfrepo;

--
-- Name: user_sequence; Type: SEQUENCE; Schema: public; Owner: perfrepo
--

CREATE SEQUENCE user_sequence
    START WITH 100
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.user_sequence OWNER TO perfrepo;

--
-- Name: value; Type: TABLE; Schema: public; Owner: perfrepo; Tablespace: 
--

CREATE TABLE value (
    id bigint NOT NULL,
    result_value double precision,
    metric_id bigint NOT NULL,
    test_execution_id bigint NOT NULL
);


ALTER TABLE public.value OWNER TO perfrepo;

--
-- Name: value_parameter; Type: TABLE; Schema: public; Owner: perfrepo; Tablespace: 
--

CREATE TABLE value_parameter (
    id bigint NOT NULL,
    name character varying(255),
    value character varying(255),
    value_id bigint NOT NULL
);


ALTER TABLE public.value_parameter OWNER TO perfrepo;

--
-- Name: value_parameter_sequence; Type: SEQUENCE; Schema: public; Owner: perfrepo
--

CREATE SEQUENCE value_parameter_sequence
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.value_parameter_sequence OWNER TO perfrepo;

--
-- Name: value_sequence; Type: SEQUENCE; Schema: public; Owner: perfrepo
--

CREATE SEQUENCE value_sequence
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.value_sequence OWNER TO perfrepo;

--
-- Name: metric_pkey; Type: CONSTRAINT; Schema: public; Owner: perfrepo; Tablespace: 
--

ALTER TABLE ONLY metric
    ADD CONSTRAINT metric_pkey PRIMARY KEY (id);


--
-- Name: tag_pkey; Type: CONSTRAINT; Schema: public; Owner: perfrepo; Tablespace: 
--

ALTER TABLE ONLY tag
    ADD CONSTRAINT tag_pkey PRIMARY KEY (id);


--
-- Name: test_execution_attachment_pkey; Type: CONSTRAINT; Schema: public; Owner: perfrepo; Tablespace: 
--

ALTER TABLE ONLY test_execution_attachment
    ADD CONSTRAINT test_execution_attachment_pkey PRIMARY KEY (id);


--
-- Name: test_execution_parameter_pkey; Type: CONSTRAINT; Schema: public; Owner: perfrepo; Tablespace: 
--

ALTER TABLE ONLY test_execution_parameter
    ADD CONSTRAINT test_execution_parameter_pkey PRIMARY KEY (id);


--
-- Name: test_execution_parameter_unique_name; Type: CONSTRAINT; Schema: public; Owner: perfrepo; Tablespace: 
--

ALTER TABLE ONLY test_execution_parameter
    ADD CONSTRAINT test_execution_parameter_unique_name UNIQUE (name, test_execution_id);


--
-- Name: test_execution_pkey; Type: CONSTRAINT; Schema: public; Owner: perfrepo; Tablespace: 
--

ALTER TABLE ONLY test_execution
    ADD CONSTRAINT test_execution_pkey PRIMARY KEY (id);


--
-- Name: test_execution_tag_pkey; Type: CONSTRAINT; Schema: public; Owner: perfrepo; Tablespace: 
--

ALTER TABLE ONLY test_execution_tag
    ADD CONSTRAINT test_execution_tag_pkey PRIMARY KEY (id);


--
-- Name: test_metric_pkey; Type: CONSTRAINT; Schema: public; Owner: perfrepo; Tablespace: 
--

ALTER TABLE ONLY test_metric
    ADD CONSTRAINT test_metric_pkey PRIMARY KEY (id);


--
-- Name: test_pkey; Type: CONSTRAINT; Schema: public; Owner: perfrepo; Tablespace: 
--

ALTER TABLE ONLY test
    ADD CONSTRAINT test_pkey PRIMARY KEY (id);


--
-- Name: test_uid_key; Type: CONSTRAINT; Schema: public; Owner: perfrepo; Tablespace: 
--

ALTER TABLE ONLY test
    ADD CONSTRAINT test_uid_key UNIQUE (uid);


--
-- Name: user_pkey; Type: CONSTRAINT; Schema: public; Owner: perfrepo; Tablespace: 
--

ALTER TABLE ONLY "user"
    ADD CONSTRAINT user_pkey PRIMARY KEY (id);


--
-- Name: user_property_pkey; Type: CONSTRAINT; Schema: public; Owner: perfrepo; Tablespace: 
--

ALTER TABLE ONLY user_property
    ADD CONSTRAINT user_property_pkey PRIMARY KEY (id);


--
-- Name: user_property_unique_name; Type: CONSTRAINT; Schema: public; Owner: perfrepo; Tablespace: 
--

ALTER TABLE ONLY user_property
    ADD CONSTRAINT user_property_unique_name UNIQUE (name, user_id);


--
-- Name: user_unique_username; Type: CONSTRAINT; Schema: public; Owner: perfrepo; Tablespace: 
--

ALTER TABLE ONLY "user"
    ADD CONSTRAINT user_unique_username UNIQUE (username);


--
-- Name: value_parameter_pkey; Type: CONSTRAINT; Schema: public; Owner: perfrepo; Tablespace: 
--

ALTER TABLE ONLY value_parameter
    ADD CONSTRAINT value_parameter_pkey PRIMARY KEY (id);


--
-- Name: value_pkey; Type: CONSTRAINT; Schema: public; Owner: perfrepo; Tablespace: 
--

ALTER TABLE ONLY value
    ADD CONSTRAINT value_pkey PRIMARY KEY (id);


--
-- Name: fk6ac9171c4825995; Type: FK CONSTRAINT; Schema: public; Owner: perfrepo
--

ALTER TABLE ONLY value
    ADD CONSTRAINT fk6ac9171c4825995 FOREIGN KEY (metric_id) REFERENCES metric(id);


--
-- Name: fk6ac9171fdfcba9a; Type: FK CONSTRAINT; Schema: public; Owner: perfrepo
--

ALTER TABLE ONLY value
    ADD CONSTRAINT fk6ac9171fdfcba9a FOREIGN KEY (test_execution_id) REFERENCES test_execution(id);


--
-- Name: fk8f4e9015fdfcba9a; Type: FK CONSTRAINT; Schema: public; Owner: perfrepo
--

ALTER TABLE ONLY test_execution_parameter
    ADD CONSTRAINT fk8f4e9015fdfcba9a FOREIGN KEY (test_execution_id) REFERENCES test_execution(id);


--
-- Name: fkc4d93bab3f0c1115; Type: FK CONSTRAINT; Schema: public; Owner: perfrepo
--

ALTER TABLE ONLY test_execution
    ADD CONSTRAINT fkc4d93bab3f0c1115 FOREIGN KEY (test_id) REFERENCES test(id);


--
-- Name: fkca230a37fdfcba9a; Type: FK CONSTRAINT; Schema: public; Owner: perfrepo
--

ALTER TABLE ONLY test_execution_attachment
    ADD CONSTRAINT fkca230a37fdfcba9a FOREIGN KEY (test_execution_id) REFERENCES test_execution(id);


--
-- Name: fke696c5fd3f0c1115; Type: FK CONSTRAINT; Schema: public; Owner: perfrepo
--

ALTER TABLE ONLY test_metric
    ADD CONSTRAINT fke696c5fd3f0c1115 FOREIGN KEY (test_id) REFERENCES test(id);


--
-- Name: fke696c5fdc4825995; Type: FK CONSTRAINT; Schema: public; Owner: perfrepo
--

ALTER TABLE ONLY test_metric
    ADD CONSTRAINT fke696c5fdc4825995 FOREIGN KEY (metric_id) REFERENCES metric(id);


--
-- Name: fkf95a5d06e904537f; Type: FK CONSTRAINT; Schema: public; Owner: perfrepo
--

ALTER TABLE ONLY test_execution_tag
    ADD CONSTRAINT fkf95a5d06e904537f FOREIGN KEY (tag_id) REFERENCES tag(id);


--
-- Name: fkf95a5d06fdfcba9a; Type: FK CONSTRAINT; Schema: public; Owner: perfrepo
--

ALTER TABLE ONLY test_execution_tag
    ADD CONSTRAINT fkf95a5d06fdfcba9a FOREIGN KEY (test_execution_id) REFERENCES test_execution(id);


--
-- Name: fkfd9de65b92d882df; Type: FK CONSTRAINT; Schema: public; Owner: perfrepo
--

ALTER TABLE ONLY value_parameter
    ADD CONSTRAINT fkfd9de65b92d882df FOREIGN KEY (value_id) REFERENCES value(id);


--
-- Name: user_property_user_fkey; Type: FK CONSTRAINT; Schema: public; Owner: perfrepo
--

ALTER TABLE ONLY user_property
    ADD CONSTRAINT user_property_user_fkey FOREIGN KEY (user_id) REFERENCES "user"(id);


--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;

-----------------------------------------------------------------------------------------------
--                                                                                           --
-- Upgrade of db schema from version 0.0.13 to 0.0.14                                        --
--                                                                                           --
-----------------------------------------------------------------------------------------------

ALTER TABLE test_execution
ADD job_id bigint NULL,
ADD comment character varying(10239) NULL;

-----------------------------------------------------------------------------------------------
--                                                                                           --
-- Upgrade of db schema from version 0.0.14 to 0.0.16                                        --
--                                                                                           --
-----------------------------------------------------------------------------------------------

CREATE INDEX value_test_execution ON value(test_execution_id);
CREATE INDEX value_parameter_value ON value_parameter(value_id);
CREATE INDEX test_execution_test ON test_execution(test_id);
CREATE INDEX test_metric_test ON test_metric(test_id);
CREATE INDEX test_metric_metric ON test_metric(metric_id);

CREATE INDEX test_execution_tag_test_execution ON test_execution_tag(test_execution_id);
CREATE INDEX test_execution_parameter_test_execution ON test_execution_parameter(test_execution_id);

-----------------------------------------------------------------------------------------------
--                                                                                           --
-- Upgrade of db schema from version 0.0.16 to 0.0.19                                        --
--                                                                                           --
-----------------------------------------------------------------------------------------------

--
-- Name: report; Type: TABLE; Schema: public; Owner: perfrepo; Tablespace:
--
CREATE TABLE report (
  id bigint NOT NULL,
  name character varying(255) NOT NULL,
  type character varying(255) NOT NULL,
  user_id bigint NOT NULL
);


ALTER TABLE public.report OWNER TO perfrepo;
ALTER TABLE ONLY public.report
ADD CONSTRAINT report_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.report
ADD CONSTRAINT report_user_fkey FOREIGN KEY (user_id) REFERENCES "user"(id);
CREATE INDEX report_user_id ON report(user_id);

--
-- Name: report_sequence; Type: SEQUENCE; Schema: public; Owner: perfrepo
--

CREATE SEQUENCE report_sequence
START WITH 1
INCREMENT BY 1
NO MAXVALUE
NO MINVALUE
CACHE 1;

ALTER TABLE public.report_sequence OWNER TO perfrepo;

--
-- Name: report_property; Type: TABLE; Schema: public; Owner: perfrepo; Tablespace:
--

CREATE TABLE report_property (
  id bigint NOT NULL,
  report_id bigint NOT NULL,
  name character varying(2047) NOT NULL,
  value character varying(2047) NOT NULL
);


ALTER TABLE public.report_property OWNER TO perfrepo;
ALTER TABLE ONLY public.report_property
ADD CONSTRAINT report_property_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.report_property
ADD CONSTRAINT report_property_report_fkey FOREIGN KEY (report_id) REFERENCES "report"(id);
CREATE INDEX report_property_report_id ON report_property(report_id);

--
-- Name: report_property_sequence; Type: SEQUENCE; Schema: public; Owner: perfrepo
--

CREATE SEQUENCE report_property_sequence
START WITH 1
INCREMENT BY 1
NO MAXVALUE
NO MINVALUE
CACHE 1;


ALTER TABLE public.report_property_sequence OWNER TO perfrepo;

--
-- Name: favorite_parameter; Type: TABLE; Schema: public; Owner: perfrepo; Tablespace:
--

CREATE TABLE favorite_parameter (
  id bigint NOT NULL,
  user_id bigint NOT NULL,
  test_id bigint NOT NULL,
  label character varying(2047) NOT NULL,
  parameter_name character varying(2047) NOT NULL
);


ALTER TABLE public.favorite_parameter OWNER TO perfrepo;
ALTER TABLE ONLY public.favorite_parameter
ADD CONSTRAINT favorite_parameter_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.favorite_parameter
ADD CONSTRAINT favorite_parameter_user_fkey FOREIGN KEY (user_id) REFERENCES "user"(id);
ALTER TABLE ONLY public.favorite_parameter
ADD CONSTRAINT favorite_parameter_test_fkey FOREIGN KEY (test_id) REFERENCES "test"(id);
CREATE INDEX favorite_parameter_user_id ON favorite_parameter(user_id);

--
-- Name: favorite_parameter_sequence; Type: SEQUENCE; Schema: public; Owner: perfrepo
--

CREATE SEQUENCE favorite_parameter_sequence
START WITH 1
INCREMENT BY 1
NO MAXVALUE
NO MINVALUE
CACHE 1;


ALTER TABLE public.favorite_parameter_sequence OWNER TO perfrepo;

--
-- Name: group; Type: TABLE; Schema: public; Owner: perfrepo; Tablespace:
--

CREATE TABLE "group" (
  id bigint NOT NULL,
  name character varying(100) NOT NULL
);

ALTER TABLE public.group OWNER TO perfrepo;
ALTER TABLE ONLY public.group
ADD CONSTRAINT group_pkey PRIMARY KEY (id);

--
-- Name: group_sequence; Type: SEQUENCE; Schema: public; Owner: perfrepo
--

CREATE SEQUENCE group_sequence
START WITH 1
INCREMENT BY 1
NO MAXVALUE
NO MINVALUE
CACHE 1;

ALTER TABLE public.group_sequence OWNER TO perfrepo;

--
-- Name: user_group; Type: TABLE; Schema: public; Owner: perfrepo; Tablespace:
--

CREATE TABLE user_group (
  id bigint NOT NULL,
  user_id bigint NOT NULL,
  group_id bigint NOT NULL
);


ALTER TABLE public.user_group OWNER TO perfrepo;
ALTER TABLE ONLY public.user_group
ADD CONSTRAINT user_group_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.user_group
ADD CONSTRAINT user_group_group_fkey FOREIGN KEY (group_id) REFERENCES "group"(id);
ALTER TABLE ONLY public.user_group
ADD CONSTRAINT user_group_user_fkey FOREIGN KEY (user_id) REFERENCES "user"(id);

--
-- Name: user_group_sequence; Type: SEQUENCE; Schema: public; Owner: perfrepo
--

CREATE SEQUENCE user_group_sequence
START WITH 1
INCREMENT BY 1
NO MAXVALUE
NO MINVALUE
CACHE 1;


ALTER TABLE public.user_group_sequence OWNER TO perfrepo;




--
-- Name: permission; Type: TABLE; Schema: public; Owner: perfrepo; Tablespace:
--

CREATE TABLE permission (
  id bigint NOT NULL,
  report_id bigint,
  access_type character varying(20) NOT NULL,
  access_level character varying(20) NOT NULL,
  group_id bigint,
  user_id bigint
);


ALTER TABLE public.permission OWNER TO perfrepo;
ALTER TABLE ONLY public.permission
ADD CONSTRAINT permission_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.permission
ADD CONSTRAINT permission_report_fkey FOREIGN KEY(report_id) REFERENCES "report"(id);
ALTER TABLE ONLY public.permission
ADD CONSTRAINT permission_group_fkey FOREIGN KEY (group_id) REFERENCES "group"(id);
ALTER TABLE ONLY public.permission
ADD CONSTRAINT permission_user_fkey FOREIGN KEY (user_id) REFERENCES "user"(id);
ALTER TABLE ONLY public.permission
ADD CONSTRAINT CheckGroupOrUserIsFilled CHECK ((CASE WHEN group_id IS NOT NULL THEN 1 ELSE 0 END + CASE WHEN user_id IS NOT NULL THEN 1 ELSE 0 END) = 1);

--
-- Name: permission_sequence; Type: SEQUENCE; Schema: public; Owner: perfrepo
--

CREATE SEQUENCE permission_sequence
START WITH 1
INCREMENT BY 1
NO MAXVALUE
NO MINVALUE
CACHE 1;


ALTER TABLE public.permission_sequence OWNER TO perfrepo;

--
-- Name: user, add password column
--

ALTER TABLE public.user
ADD COLUMN password character varying(300);


--
-- User/Group data
--

insert into public.user (id, username, first_name, last_name, email, password) values (nextVal('user_sequence'), 'perfrepouser', 'perfrepouser', 'perfrepouser', 'example@example.com', '/+aGXwHMbhcz5HDdSx9FRg==');

insert into public.group (id, name) values (nextVal('group_sequence'), 'perfrepouser');

insert into user_group (id, user_id, group_id) values (nextVal('user_group_sequence'), (select id from public.user where username='perfrepouser'), (select id from public.group where name='perfrepouser'));

--
-- Migrate script - reports from user_properties to report entities
--

create or replace function migrate_reports ()
  RETURNS setof report AS
  '
  DECLARE
      key text;
      reportrow report;
      keyname text;
      link text;
      type text;
      userId integer;
      reportId integer;
      reportIdResult integer;
      upValue text;
      upCode text;
      upId integer;
      upIdResult integer;
      prop1 text;
      prop2 text;
  BEGIN

  for key in select distinct substring(name, 8, position(''.'' IN substring(name, 8)) -1) as code from user_property where name like ''report.%'' loop
      execute ''select value from user_property where name like ''''report.'''' || '' || quote_literal(key) || '' || ''''.name'''''' into keyname;
      execute ''select value from user_property where name like ''''report.'''' || '' || quote_literal(key) || '' || ''''.type'''''' into type;
      execute ''select user_id from user_property where name like ''''report.'''' || '' || quote_literal(key) || '' || ''''.name'''''' into userId;
      execute ''SELECT nextval(''''REPORT_SEQUENCE'''')'' into reportId;
      RAISE NOTICE ''report id %'', reportId;
      execute ''insert into report (id, name, type, user_id) values ('' || reportId || '', '' || quote_literal(keyname) || '' , '' || quote_literal(type) || '' , '' || userId || '') returning id'' into reportIdResult;
    reportrow := (reportIdResult,keyname,type,userId);
      return next reportrow;
      prop1 := ''report.''|| key || ''.%'';
      prop2 := ''report.'' || key || ''.(name|type).*'';
    for upCode, upValue in select substring(name, 9 + length(key)), value from user_property where name like prop1 and name !~ prop2 and user_id=userId  loop
       execute ''SELECT nextval(''''REPORT_PROPERTY_SEQUENCE'''')'' into upId;
       execute ''insert into report_property (id, report_id,  name, value) values (''||upid||'',''||reportIdResult||'',''||quote_literal(upCode)||'',''||quote_literal(upValue)||'') returning id'' into upIdResult;
       RAISE NOTICE ''report property id %'', upIdResult;
    end loop;
  end loop;

  return;
  END
  '
LANGUAGE plpgsql VOLATILE;

--
-- Migrate script - favorite parameters from user_properties to favorite parameters entities
--

create or replace function migrate_favorite_parameters ()
  RETURNS setof favorite_parameter AS
  '
  DECLARE
      key text;
      favparamrow favorite_parameter;
      favparamIdResult integer;
      value text;
      label text;
      parameterName text;
      favparamId integer;
      userId integer;
      testId integer;
  BEGIN

  for key in select id from user_property where name like ''fav.param.%'' loop
      execute ''select trim(both ''''|'''' from substring(value from ''''^[^\|]*\|'''')) from user_property where id = '' || key into testId;
      execute ''select trim(both ''''|'''' from substring(value from ''''\|[^\|]*\|'''')) from user_property where id = '' || key into parameterName;
      execute ''select trim(both ''''|'''' from substring(value from ''''\|[^\|]*$'''')) from user_property where id = '' || key into label;
      execute ''select user_id from user_property where id = '' || key into userId;

      execute ''SELECT nextval(''''FAVORITE_PARAMETER_SEQUENCE'''')'' into favparamId;

      RAISE NOTICE ''User property with ID % migrated'', key;
      execute ''insert into favorite_parameter (id, label, parameter_name, user_id, test_id) values ('' || favparamId || '', '' || quote_literal(label) || '' , '' || quote_literal(parameterName) || '' , '' || userId || '', '' || testId || '') returning id'' into favparamIdResult;
      favparamrow := (favparamIdResult,userId,testId,label,parameterName);
      return next favparamrow;
  end loop;

  return;
  END
  '
LANGUAGE plpgsql VOLATILE;



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
  end loop;

  execute ''SELECT nextval(''''PERMISSION_SEQUENCE'''')'' into permissionId;
  execute ''insert into permission (id, access_type, access_level, report_id) VALUES ('' || permissionId|| '', ''''READ'''', ''''PUBLIC'''', '' || reportrow.id|| '')'';

  return;
  END
  '
LANGUAGE plpgsql VOLATILE;