--
-- 004 - Schema update from 0.0.16 to 0.0.19 - report tables
--

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
    access_type character varying(20) NOT NULL,
    access_level character varying(20) NOT NULL,
    group_id bigint,
    user_id bigint
);


ALTER TABLE public.permission OWNER TO perfrepo;
ALTER TABLE ONLY public.permission
    ADD CONSTRAINT permission_pkey PRIMARY KEY (id);
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
-- Name: report_permission; Type: TABLE; Schema: public; Owner: perfrepo; Tablespace:
--

CREATE TABLE report_permission (
    id bigint NOT NULL,
    report_id bigint NOT NULL,
    permission_id bigint NOT NULL
);


ALTER TABLE public.report_permission OWNER TO perfrepo;
ALTER TABLE ONLY public.report_permission
    ADD CONSTRAINT report_permission_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.report_permission
    ADD CONSTRAINT report_permission_report_fkey FOREIGN KEY (report_id) REFERENCES "report"(id);
ALTER TABLE ONLY public.report_permission
    ADD CONSTRAINT report_permission_permission_fkey FOREIGN KEY (permission_id) REFERENCES "permission"(id);

--
-- Name: report_permission_sequence; Type: SEQUENCE; Schema: public; Owner: perfrepo
--

CREATE SEQUENCE report_permission_sequence
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.report_permission_sequence OWNER TO perfrepo;

--
-- Name: user, add password column
--

ALTER TABLE public.user
	ADD COLUMN password character varying(300);


--
-- User/Group data
--

update 'user' set password='lP17jm/z+qOOyDBlcMZVnQ==' where username='teamsoa';
update 'user' set password='nPNCv/fN+SBVjmDHRrpBmg==' where username='teamjdg';
update 'user' set password='hqOsT74gt4HDT5aMBb4lBQ==' where username='teamseam';
update 'user' set password='TOVFEodr0QUICxsy9IzvMg==' where username='teambrms';
update 'user' set password='/+aGXwHMbhcz5HDdSx9FRg==' where username='perfrepouser';

insert into public.group (id, name) values (nextVal('group_sequence'), 'soa');
insert into public.group (id, name) values (nextVal('group_sequence'), 'jdg');
insert into public.group (id, name) values (nextVal('group_sequence'), 'seam');
insert into public.group (id, name) values (nextVal('group_sequence'), 'brms');
insert into public.group (id, name) values (nextVal('group_sequence'), 'perfrepouser');

insert into user_group (id, user_id, group_id) values (nextVal('user_group_sequence'), (select id from public.user where username='teamsoa'), (select id from public.group where name='soa'));
insert into user_group (id, user_id, group_id) values (nextVal('user_group_sequence'), (select id from public.user where username='teamjdg'), (select id from public.group where name='jdg'));
insert into user_group (id, user_id, group_id) values (nextVal('user_group_sequence'), (select id from public.user where username='teamseam'), (select id from public.group where name='seam'));
insert into user_group (id, user_id, group_id) values (nextVal('user_group_sequence'), (select id from public.user where username='teambrms'), (select id from public.group where name='brms'));
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

