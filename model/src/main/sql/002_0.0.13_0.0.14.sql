--
-- 002 - Schema update from 0.0.13 to 0.0.14 
--

alter TABLE test_execution ADD job_id bigint NULL;