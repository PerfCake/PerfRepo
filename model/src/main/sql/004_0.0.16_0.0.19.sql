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
ALTER TABLE ONLY public.report
    ADD CONSTRAINT report_unique_name UNIQUE (name, user_id);
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
	execute ''select value from user_property where name like ''''report.'''' || '' || quote_literal(key) || '' || ''''.link'''''' into link;
	execute ''select value from user_property where name like ''''report.'''' || '' || quote_literal(key) || '' || ''''.type'''''' into type;
	execute ''select user_id from user_property where name like ''''report.'''' || '' || quote_literal(key) || '' || ''''.name'''''' into userId;
	execute ''SELECT nextval(''''REPORT_SEQUENCE'''')'' into reportId;
	RAISE NOTICE ''report id %'', reportId;
	execute ''insert into report (id, code, name, link, type, user_id) values ('' || reportId || '', '' || quote_literal(key) || '' , '' || quote_literal(keyname) || '' , '' || quote_literal(link) || '' , '' || quote_literal(type) || '' , '' || userId || '') returning id'' into reportIdResult;	
  reportrow := (reportIdResult, key,keyname,link,type,userId);
	return next reportrow;
	prop1 := ''report.''|| key || ''.%'';
	prop2 := ''report.'' || key || ''.(link|name|type).*'';
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