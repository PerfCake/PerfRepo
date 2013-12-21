--
-- 002 - Schema update from 0.0.13 to 0.0.14 
--
ALTER TABLE test_execution 
ADD job_id bigint NULL,
ADD comment character varying(10239) NULL;
