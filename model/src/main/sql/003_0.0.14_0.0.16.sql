--
-- 003 - Schema update from 0.0.14 to 0.0.16 - added indexes
--
CREATE INDEX value_test_execution ON value(test_execution_id);
CREATE INDEX value_parameter_value ON value_parameter(value_id);
CREATE INDEX test_execution_test ON test_execution(test_id);
CREATE INDEX test_metric_test ON test_metric(test_id);
CREATE INDEX test_metric_metric ON test_metric(metric_id);

CREATE INDEX test_execution_tag_test_execution ON test_execution_tag(test_execution_id);
CREATE INDEX test_execution_parameter_test_execution ON test_execution_parameter(test_execution_id);