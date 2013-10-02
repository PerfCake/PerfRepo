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


--
-- PostgreSQL database dump complete
--

