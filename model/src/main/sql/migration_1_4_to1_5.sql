-----------------------------------------------------------------------------------------------
--                                                                                           --
-- Upgrade of db schema from version 1.4 to 1.5                                           --
--                                                                                           --
-----------------------------------------------------------------------------------------------

BEGIN;


ALTER TABLE test ALTER COLUMN description DROP NOT NULL;
ALTER TABLE metric ALTER COLUMN description DROP NOT NULL;

ALTER TABLE test_execution_tag DROP COLUMN id;
DROP sequence test_execution_tag_sequence;

ALTER TABLE test_metric DROP COLUMN id;
DROP sequence test_metric_sequence;


COMMIT;
