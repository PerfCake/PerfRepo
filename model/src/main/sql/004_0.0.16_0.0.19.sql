--
-- 004 - Schema update from 0.0.16 to 0.0.19 - report tables
--

--
-- Name: report; Type: TABLE; Schema: public; Owner: perfrepo; Tablespace: 
--
CREATE TABLE report (
    id bigint NOT NULL,
    code character varying(255) NOT NULL,
    name character varying(255) NOT NULL,
    link character varying(2047) NOT NULL,
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