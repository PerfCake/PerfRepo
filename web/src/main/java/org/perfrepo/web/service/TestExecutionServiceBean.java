package org.perfrepo.web.service;

import org.perfrepo.web.model.Metric;
import org.perfrepo.web.model.Tag;
import org.perfrepo.web.model.Test;
import org.perfrepo.web.model.TestExecution;
import org.perfrepo.web.model.TestExecutionAttachment;
import org.perfrepo.web.model.TestExecutionParameter;
import org.perfrepo.web.model.Value;
import org.perfrepo.web.model.to.SearchResultWrapper;
import org.perfrepo.web.model.to.TestExecutionSearchCriteria;
import org.perfrepo.web.dao.MetricDAO;
import org.perfrepo.web.dao.TagDAO;
import org.perfrepo.web.dao.TestDAO;
import org.perfrepo.web.dao.TestExecutionAttachmentDAO;
import org.perfrepo.web.dao.TestExecutionDAO;
import org.perfrepo.web.dao.TestExecutionParameterDAO;
import org.perfrepo.web.dao.ValueDAO;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * TODO: document this
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class TestExecutionServiceBean implements TestExecutionService {

    @Inject
    private TestDAO testDAO;

    @Inject
    private MetricDAO metricDAO;

    @Inject
    private TestExecutionDAO testExecutionDAO;

    @Inject
    private ValueDAO valueDAO;

    @Inject
    private TestExecutionParameterDAO testExecutionParameterDAO;

    @Inject
    private TestExecutionAttachmentDAO testExecutionAttachmentDAO;

    @Inject
    private AlertingService alertingService;

    @Inject
    private TagDAO tagDAO;

    /******** Methods related directly to test execution object ********/

    @Override
    public TestExecution createTestExecution(TestExecution testExecution) {
        Test managedTest = testDAO.get(testExecution.getTest().getId());
        testExecution.setTest(managedTest);

        TestExecution createdExecution = testExecutionDAO.create(testExecution);

        Set<Tag> tags = testExecution.getTags();
        for (Tag tag: tags) {
            addTag(tag, createdExecution);
        }

        Map<String, TestExecutionParameter> parameters = testExecution.getParameters();
        for (TestExecutionParameter parameter: parameters.values()) {
            parameter.setTestExecution(createdExecution);
            addParameter(parameter);
        }

        List<Value> values = testExecution.getValues();
        for (Value value: values) {
            value.setTestExecution(createdExecution);
            addValue(value);
        }

        List<TestExecutionAttachment> attachments = testExecution.getAttachments();
        for (TestExecutionAttachment attachment: attachments) {
            attachment.setTestExecution(createdExecution);
            addAttachment(attachment);
        }

        // TODO: add alerting

        return createdExecution;
    }

    @Override
    public TestExecution updateTestExecution(TestExecution updatedTestExecution) {
        return testExecutionDAO.merge(updatedTestExecution);
    }

    @Override
    public void removeTestExecution(TestExecution testExecution) {
        TestExecution managedExecution = testExecutionDAO.get(testExecution.getId());

        Set<Tag> tags = new HashSet<>(managedExecution.getTags());
        for (Tag tag: tags) {
            removeTagFromTestExecution(tag, managedExecution);
        }

        // other entities are removed by DB associations

        testExecutionDAO.remove(managedExecution);
    }

    @Override
    public TestExecution getTestExecution(Long id) {
        return testExecutionDAO.get(id);
    }

    @Override
    public List<TestExecution> getAllTestExecutions() {
        return testExecutionDAO.getAll();
    }

    @Override
    public SearchResultWrapper<TestExecution> searchTestExecutions(TestExecutionSearchCriteria search) {
        return testExecutionDAO.searchTestExecutions(search);
    }

    /******** Methods related to test execution attachments ********/

    @Override
    public TestExecutionAttachment addAttachment(TestExecutionAttachment attachment) {
        TestExecution managedExecution = testExecutionDAO.get(attachment.getTestExecution().getId());
        attachment.setTestExecution(managedExecution);

        return testExecutionAttachmentDAO.create(attachment);
    }

    @Override
    public void removeAttachment(TestExecutionAttachment attachment) {
        TestExecutionAttachment managedAttachment = testExecutionAttachmentDAO.get(attachment.getId());
        testExecutionAttachmentDAO.remove(managedAttachment);
    }

    @Override
    public TestExecutionAttachment getAttachment(Long id) {
        return testExecutionAttachmentDAO.get(id);
    }

    @Override
    public List<TestExecutionAttachment> getAttachments(TestExecution testExecution) {
        return testExecutionAttachmentDAO.findByExecution(testExecution.getId());
    }

    /******** Methods related to test execution parameters ********/

    @Override
    public TestExecutionParameter addParameter(TestExecutionParameter parameter) {
        TestExecution managedExecution = testExecutionDAO.get(parameter.getTestExecution().getId());
        parameter.setTestExecution(managedExecution);

        return testExecutionParameterDAO.create(parameter);
    }

    @Override
    public TestExecutionParameter updateParameter(TestExecutionParameter parameter) {
        return testExecutionParameterDAO.merge(parameter);
    }

    @Override
    public void removeParameter(TestExecutionParameter parameter) {
        TestExecutionParameter managedParameter = testExecutionParameterDAO.get(parameter.getId());
        testExecutionParameterDAO.remove(managedParameter);
    }

    @Override
    public TestExecutionParameter getParameter(Long id) {
        return testExecutionParameterDAO.get(id);
    }

    @Override
    public List<TestExecutionParameter> getParametersByPrefix(String prefix) {
        return testExecutionParameterDAO.findByPrefix(prefix);
    }

    @Override
    public List<TestExecutionParameter> getParameters(TestExecution testExecution) {
        return testExecutionParameterDAO.findByExecution(testExecution.getId());
    }

    /******** Methods related to values ********/

    @Override
    public Value addValue(Value value) {
        TestExecution managedExecution = testExecutionDAO.get(value.getTestExecution().getId());
        Metric managedMetric = metricDAO.get(value.getMetric().getId());

        value.setTestExecution(managedExecution);
        value.setMetric(managedMetric);

        return valueDAO.create(value);
    }

    @Override
    public Value updateValue(Value value) {
        return valueDAO.merge(value);
    }

    @Override
    public void removeValue(Value value) {
        Value managedValue = valueDAO.get(value.getId());
        valueDAO.remove(managedValue);
    }

    @Override
    public Value getValue(Long id) {
        return valueDAO.get(id);
    }

    @Override
    public List<Value> getValues(Metric metric, TestExecution testExecution) {
        return valueDAO.findByMetricAndExecution(metric.getId(), testExecution.getId());
    }

    /******** Methods related to tags ********/

    @Override
    public Tag addTag(Tag tag, TestExecution testExecution) {
        TestExecution managedExecution = testExecutionDAO.get(testExecution.getId());

        Tag managedTag = tagDAO.findByName(tag.getName());
        if (managedTag == null) {
            managedTag = tagDAO.create(tag);
        }

        managedExecution.getTags().add(managedTag);
        managedTag.getTestExecutions().add(managedExecution);

        return managedTag;
    }

    @Override
    public void removeTagFromTestExecution(Tag tag, TestExecution testExecution) {
        TestExecution managedExecution = testExecutionDAO.get(testExecution.getId());
        Tag managedTag = tagDAO.get(tag.getId());

        managedExecution.getTags().remove(managedTag);
        managedTag.getTestExecutions().remove(managedExecution);

        if (managedTag.getTestExecutions().isEmpty()) {
            tagDAO.remove(managedTag);
        }
    }

    @Override
    public Set<Tag> getTags(TestExecution testExecution) {
        return new TreeSet<>(tagDAO.findByExecution(testExecution.getId()));
    }

    @Override
    public Set<Tag> getAllTags() {
        return new TreeSet<>(tagDAO.getAll());
    }

    @Override
    public Set<Tag> getTagsByPrefix(String prefix) {
        return tagDAO.findByPrefix(prefix);
    }

    @Override
    public void addTagsToTestExecutions(Set<Tag> tags, Collection<TestExecution> testExecutions) {
        for (TestExecution testExecution: testExecutions) {
            for (Tag tag: tags) {
                addTag(tag, testExecution);
            }
        }
    }

    @Override
    public void removeTagsFromTestExecutions(Set<Tag> tags, Collection<TestExecution> testExecutions) {
        for (TestExecution testExecution: testExecutions) {
            for (Tag tag: tags) {
                removeTagFromTestExecution(tag, testExecution);
            }
        }
    }

}
