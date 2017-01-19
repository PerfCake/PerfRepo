package org.perfrepo.web.service;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.perfrepo.web.model.Metric;
import org.perfrepo.enums.MetricComparator;
import org.perfrepo.web.model.Tag;
import org.perfrepo.web.model.Test;
import org.perfrepo.web.model.TestExecution;
import org.perfrepo.web.model.TestExecutionAttachment;
import org.perfrepo.web.model.TestExecutionParameter;
import org.perfrepo.web.model.Value;
import org.perfrepo.web.model.to.SearchResultWrapper;
import org.perfrepo.web.model.to.TestExecutionSearchCriteria;
import org.perfrepo.web.model.user.Group;
import org.perfrepo.web.model.user.User;
import org.perfrepo.web.service.exceptions.UnauthorizedException;
import org.perfrepo.web.util.TestUtils;
import org.perfrepo.web.util.UserSessionMock;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link TestExecutionService}.
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
@RunWith(Arquillian.class)
public class TestExecutionServiceTest {

    @Inject
    private UserService userService;

    @Inject
    private TestService testService;

    @Inject
    private TestExecutionService testExecutionService;

    @Inject
    private GroupService groupService;

    private List<Group> groups = new ArrayList<>();
    private User testUser;
    private User adminUser;
    private List<Test> tests = new ArrayList<>();
    private List<Metric> metrics = new ArrayList<>();

    @Deployment
    public static Archive<?> createDeployment() {
        return TestUtils.createDeployment();
    }

    @Before
    public void init() {
        adminUser = createUser("admin");
        adminUser.setType(User.UserType.SUPER_ADMIN);
        UserSessionMock.setLoggedUser(adminUser); // hack, because we need some super admin to create a super admin :)
        userService.createUser(adminUser);
        UserSessionMock.setLoggedUser(adminUser);

        Group group = createGroup("test_group");
        groups.add(groupService.createGroup(group));

        Group group2 = createGroup("other_group");
        groups.add(groupService.createGroup(group2));

        User user = createUser("test_user");
        userService.createUser(user);
        testUser = user;

        groupService.addUserToGroup(user, group);
        UserSessionMock.setLoggedUser(user);

        Test test1 = new Test();
        fillTest("test1", test1);
        test1.setGroup(groups.get(0));
        tests.add(testService.createTest(test1));

        Metric metric1 = createMetric("metric1");
        metrics.add(testService.addMetric(metric1, test1));
        Metric metric2 = createMetric("metric2");
        metrics.add(testService.addMetric(metric2, test1));

        UserSessionMock.setLoggedUser(adminUser);
        Test test2 = new Test();
        fillTest("test2", test2);
        test2.setGroup(groups.get(1));
        tests.add(testService.createTest(test2));
        UserSessionMock.setLoggedUser(user);
    }

    @After
    public void cleanUp() throws UnauthorizedException {
        UserSessionMock.setLoggedUser(adminUser);
        for (User user: userService.getAllUsers()) {
            userService.removeUser(user);
        }

        for (TestExecution testExecution: testExecutionService.getAllTestExecutions()) {
            testExecutionService.removeTestExecution(testExecution);
        }

        for (Group group: groupService.getAllGroups()) {
            groupService.removeGroup(group);
        }

        //removing of the test also removes test executions and metrics
        for (Test test: testService.getAllTests()) {
            testService.removeTest(test);
        }
    }

    @org.junit.Test
    public void testTestExecutionBasicCRUDOperations() {
        TestExecution testExecution = new TestExecution();
        fillTestExecution("exec1", tests.get(0), testExecution);

        TestExecution createdTestExecution = testExecutionService.createTestExecution(testExecution);

        assertNotNull(createdTestExecution);
        assertTestExecution(testExecution, createdTestExecution);

        // test update
        TestExecution testExecutionToUpdate = createdTestExecution;
        fillTestExecution("updated_exec", tests.get(0), testExecutionToUpdate);

        TestExecution updatedTestExecution = testExecutionService.updateTestExecution(testExecutionToUpdate);
        assertTestExecution(testExecutionToUpdate, updatedTestExecution);

        // test delete
        TestExecution testExecutionToDelete = updatedTestExecution;
        testExecutionService.removeTestExecution(testExecutionToDelete);
        assertNull(testExecutionService.getTestExecution(testExecutionToDelete.getId()));
    }

    @org.junit.Test
    public void testAttachmentCRUDOperations() {
        TestExecution testExecution = new TestExecution();
        fillTestExecution("exec1", tests.get(0), testExecution);

        TestExecution createdTestExecution = testExecutionService.createTestExecution(testExecution);

        TestExecutionAttachment attachment = createAttachment("attachment", createdTestExecution);
        TestExecutionAttachment createdAttachment = testExecutionService.addAttachment(attachment);

        assertAttachment(attachment, createdAttachment);

        TestExecutionAttachment retrievedAttachment = testExecutionService.getAttachment(createdAttachment.getId());
        assertAttachment(createdAttachment, retrievedAttachment);

        List<TestExecutionAttachment> retrievedAttachments = testExecutionService.getAttachments(testExecution);
        assertEquals(1, retrievedAttachments.size());
        assertAttachment(retrievedAttachment, retrievedAttachments.get(0));

        testExecutionService.removeAttachment(retrievedAttachment);
        assertNull(testExecutionService.getAttachment(createdAttachment.getId()));
        assertTrue(testExecutionService.getAttachments(testExecution).isEmpty());
    }

    @org.junit.Test
    public void testParameterCRUDOperations() {
        TestExecution testExecution = new TestExecution();
        fillTestExecution("exec1", tests.get(0), testExecution);

        TestExecution createdTestExecution = testExecutionService.createTestExecution(testExecution);

        TestExecutionParameter parameter = new TestExecutionParameter();
        fillParameter("parameter", createdTestExecution, parameter);
        TestExecutionParameter createdParameter = testExecutionService.addParameter(parameter);

        assertParameter(parameter, createdParameter);

        TestExecutionParameter retrievedParameter = testExecutionService.getParameter(createdParameter.getId());
        assertParameter(createdParameter, retrievedParameter);

        TestExecutionParameter parameterToUpdate = retrievedParameter;
        fillParameter("updated_parameter", createdTestExecution, parameterToUpdate);
        TestExecutionParameter updatedParameter = testExecutionService.updateParameter(parameterToUpdate);
        assertParameter(parameterToUpdate, updatedParameter);

        List<TestExecutionParameter> retrievedParameters = testExecutionService.getParameters(testExecution);
        assertEquals(1, retrievedParameters.size());
        assertParameter(retrievedParameter, retrievedParameters.get(0));

        testExecutionService.removeParameter(retrievedParameter);
        assertNull(testExecutionService.getParameter(createdParameter.getId()));
        assertTrue(testExecutionService.getParameters(testExecution).isEmpty());
    }
    
    @org.junit.Test
    public void testValuesCRUDOperations() {
        TestExecution testExecution = new TestExecution();
        fillTestExecution("exec1", tests.get(0), testExecution);

        TestExecution createdTestExecution = testExecutionService.createTestExecution(testExecution);

        Value value = new Value();
        fillValue(1, metrics.get(0), createdTestExecution, value);
        Value createdValue = testExecutionService.addValue(value);

        assertValue(value, createdValue);

        Value retrievedValue = testExecutionService.getValue(createdValue.getId());
        assertValue(createdValue, retrievedValue);

        Value valueToUpdate = retrievedValue;
        fillValue(2, metrics.get(0), createdTestExecution, valueToUpdate);
        Value updatedValue = testExecutionService.updateValue(valueToUpdate);
        assertValue(valueToUpdate, updatedValue);

        List<Value> retrievedValues = testExecutionService.getValues(metrics.get(0), testExecution);
        assertEquals(1, retrievedValues.size());
        assertValue(retrievedValue, retrievedValues.get(0));

        testExecutionService.removeValue(retrievedValue);
        assertNull(testExecutionService.getValue(createdValue.getId()));
        assertTrue(testExecutionService.getValues(metrics.get(0), testExecution).isEmpty());
    }

    @org.junit.Test
    public void testTagsCRUDOperations() {
        TestExecution testExecution1 = new TestExecution();
        fillTestExecution("exec1", tests.get(0), testExecution1);
        TestExecution createdTestExecution1 = testExecutionService.createTestExecution(testExecution1);

        TestExecution testExecution2 = new TestExecution();
        fillTestExecution("exec2", tests.get(0), testExecution2);
        TestExecution createdTestExecution2 = testExecutionService.createTestExecution(testExecution2);

        Tag tag1 = new Tag();
        fillTag("tag1", tag1);
        Tag createdTag1 = testExecutionService.addTag(tag1, createdTestExecution1);
        Tag tag2 = new Tag();
        fillTag("tag2", tag2);
        Tag createdTag2 = testExecutionService.addTag(tag2, createdTestExecution1);
        testExecutionService.addTag(tag2, createdTestExecution2);

        Set<Tag> tagsForExec1 = testExecutionService.getTags(createdTestExecution1);
        Set<Tag> tagsForExec2 = testExecutionService.getTags(createdTestExecution2);

        Set<Tag> expectedForExec1 = new HashSet<>(Arrays.asList(tag1, tag2));
        assertEquals(expectedForExec1.size(), tagsForExec1.size());
        assertTrue(expectedForExec1.stream().allMatch(expected -> expectedForExec1.stream()
                .anyMatch(actual -> expected.getName().equals(actual.getName()))));

        Set<Tag> expectedForExec2 = new HashSet<>(Arrays.asList(tag2));
        assertEquals(expectedForExec2.size(), tagsForExec2.size());
        assertTrue(expectedForExec2.stream().allMatch(expected -> expectedForExec2.stream()
                .anyMatch(actual -> expected.getName().equals(actual.getName()))));

        // test disassociation
        testExecutionService.removeTagFromTestExecution(createdTag1, createdTestExecution1);
        Set<Tag> allTags = testExecutionService.getAllTags();
        Set<Tag> expectedAllTags = new HashSet<>(Arrays.asList(tag2));
        assertEquals(expectedAllTags.size(), allTags.size());
        assertTrue(expectedAllTags.stream().allMatch(expected -> allTags.stream()
                .anyMatch(actual -> expected.getName().equals(actual.getName()))));

        testExecutionService.removeTagFromTestExecution(createdTag2, createdTestExecution2);
        Set<Tag> allTags2 = testExecutionService.getAllTags();
        Set<Tag> expectedAllTags2 = new HashSet<>(Arrays.asList(tag2));
        assertEquals(expectedAllTags2.size(), allTags2.size());
        assertTrue(expectedAllTags2.stream().allMatch(expected -> allTags2.stream()
                .anyMatch(actual -> expected.getName().equals(actual.getName()))));


        testExecutionService.removeTagFromTestExecution(createdTag2, createdTestExecution1);
        assertTrue(testExecutionService.getAllTags().isEmpty());
    }

    @org.junit.Test
    public void testFindTagsByPrefix() {
        TestExecution testExecution1 = new TestExecution();
        fillTestExecution("exec1", tests.get(0), testExecution1);
        TestExecution createdTestExecution1 = testExecutionService.createTestExecution(testExecution1);

        TestExecution testExecution2 = new TestExecution();
        fillTestExecution("exec2", tests.get(0), testExecution2);
        TestExecution createdTestExecution2 = testExecutionService.createTestExecution(testExecution2);

        Tag tag1 = new Tag();
        fillTag("aa_tag1", tag1);
        Tag createdTag1 = testExecutionService.addTag(tag1, createdTestExecution1);
        Tag tag2 = new Tag();
        fillTag("ab_tag2", tag2);
        Tag createdTag2 = testExecutionService.addTag(tag2, createdTestExecution1);
        testExecutionService.addTag(tag2, createdTestExecution2);

        Set<Tag> actualResult = testExecutionService.getTagsByPrefix("a");
        List<Tag> expectedResult = Arrays.asList(tag1, tag2);
        assertEquals(expectedResult.size(), actualResult.size());
        assertTrue(expectedResult.stream().allMatch(expected -> actualResult.stream()
                .anyMatch(actual -> expected.getName().equals(actual.getName()))));

        Set<Tag> actualResult2 = testExecutionService.getTagsByPrefix("aa");
        List<Tag> expectedResult2 = Arrays.asList(tag1);
        assertEquals(expectedResult2.size(), actualResult2.size());
        assertTrue(expectedResult2.stream().allMatch(expected -> actualResult2.stream()
                .anyMatch(actual -> expected.getName().equals(actual.getName()))));
    }

    @org.junit.Test
    public void testTagMassOperations() {
        TestExecution testExecution1 = new TestExecution();
        fillTestExecution("exec1", tests.get(0), testExecution1);
        TestExecution createdTestExecution1 = testExecutionService.createTestExecution(testExecution1);

        Tag tag1 = new Tag();
        fillTag("tag1", tag1);
        Tag tag2 = new Tag();
        fillTag("tag2", tag2);
        Set<Tag> tagsToAdd = new HashSet<>(Arrays.asList(tag1, tag2));

        testExecutionService.addTagsToTestExecutions(tagsToAdd, new HashSet<>(Arrays.asList(createdTestExecution1)));

        Set<Tag> tagsForExec1 = testExecutionService.getTags(createdTestExecution1);
        assertEquals(tagsToAdd.size(), tagsForExec1.size());
        assertTrue(tagsToAdd.stream().allMatch(expected -> tagsForExec1.stream()
                .anyMatch(actual -> expected.getName().equals(actual.getName()))));

        testExecutionService.removeTagsFromTestExecutions(tagsToAdd, new HashSet<>(Arrays.asList(createdTestExecution1)));
        assertTrue(testExecutionService.getTags(createdTestExecution1).isEmpty());
    }

    @org.junit.Test
    public void testFindParametersByPrefix() {
        TestExecution testExecution1 = new TestExecution();
        fillTestExecution("exec1", tests.get(0), testExecution1);
        TestExecution createdTestExecution = testExecutionService.createTestExecution(testExecution1);

        TestExecutionParameter parameter1 = new TestExecutionParameter();
        fillParameter("aa_parameter", createdTestExecution, parameter1);
        TestExecutionParameter createdParameter1 = testExecutionService.addParameter(parameter1);
        TestExecutionParameter parameter2 = new TestExecutionParameter();
        fillParameter("ab_parameter", createdTestExecution, parameter2);
        TestExecutionParameter createdParameter2 = testExecutionService.addParameter(parameter2);

        List<TestExecutionParameter> actualResult = testExecutionService.getParametersByPrefix("a");
        List<TestExecutionParameter> expectedResult = Arrays.asList(parameter1, parameter2);
        assertEquals(expectedResult.size(), actualResult.size());
        assertTrue(expectedResult.stream().allMatch(expected -> actualResult.stream()
                .anyMatch(actual -> expected.getName().equals(actual.getName()))));

        List<TestExecutionParameter> actualResult2 = testExecutionService.getParametersByPrefix("aa");
        List<TestExecutionParameter> expectedResult2 = Arrays.asList(parameter1);
        assertEquals(expectedResult2.size(), actualResult2.size());
        assertTrue(expectedResult2.stream().allMatch(expected -> actualResult2.stream()
                .anyMatch(actual -> expected.getName().equals(actual.getName()))));
    }

    @org.junit.Test
    public void testCreateExecutionWithAllObjects() {
        TestExecution testExecution = new TestExecution();
        fillTestExecution("exec1", tests.get(0), testExecution);

        Tag tag = new Tag();
        fillTag("tag", tag);
        Set<Tag> tags = new HashSet<>(Arrays.asList(tag));
        testExecution.setTags(tags);

        TestExecutionParameter parameter = new TestExecutionParameter();
        fillParameter("parameter", testExecution, parameter);
        Map<String, TestExecutionParameter> parameters = new HashMap<>();
        parameters.put(parameter.getName(), parameter);
        testExecution.setParameters(parameters);

        Value value = new Value();
        fillValue(1, metrics.get(0), testExecution, value);
        List<Value> values = Arrays.asList(value);
        testExecution.setValues(values);

        TestExecutionAttachment attachment = createAttachment("attachment", testExecution);
        List<TestExecutionAttachment> attachments = Arrays.asList(attachment);
        testExecution.setAttachments(attachments);

        // do the assertions

        TestExecution createdTestExecution = testExecutionService.createTestExecution(testExecution);
        assertNotNull(testExecutionService.getTestExecution(createdTestExecution.getId()));

        Set<Tag> actualTags = testExecutionService.getTags(createdTestExecution);
        Set<Tag> expectedTags = new HashSet<>(Arrays.asList(tag));
        assertEquals(actualTags.size(), expectedTags.size());
        assertTrue(actualTags.stream().allMatch(expected -> expectedTags.stream()
                .anyMatch(actual -> expected.getName().equals(actual.getName()))));

        List<TestExecutionParameter> actualParameters = testExecutionService.getParameters(createdTestExecution);
        List<TestExecutionParameter> expectedParameters = Arrays.asList(parameter);
        assertEquals(expectedParameters.size(), actualParameters.size());
        assertTrue(expectedParameters.stream().allMatch(expected -> actualParameters.stream()
                .anyMatch(actual -> expected.getName().equals(actual.getName()))));

        List<Value> actualValues = testExecutionService.getValues(metrics.get(0), createdTestExecution);
        List<Value> expectedValues = Arrays.asList(value);
        assertEquals(expectedValues.size(), actualValues.size());
        assertTrue(expectedValues.stream().allMatch(expected -> actualValues.stream()
                .anyMatch(actual -> expected.getResultValue().equals(actual.getResultValue()))));

        List<TestExecutionAttachment> actualAttachments = testExecutionService.getAttachments(createdTestExecution);
        List<TestExecutionAttachment> expectedAttachments = Arrays.asList(attachment);
        assertEquals(expectedAttachments.size(), actualAttachments.size());
        assertTrue(expectedAttachments.stream().allMatch(expected -> actualAttachments.stream()
                .anyMatch(actual -> expected.getFilename().equals(actual.getFilename()))));
    }

    @org.junit.Test
    public void testExecutionSearch() {
        TestExecution testExecution = new TestExecution();
        fillTestExecution("exec1", tests.get(0), testExecution);

        TestExecution createdTestExecution = testExecutionService.createTestExecution(testExecution);
        assertNotNull(testExecutionService.getTestExecution(createdTestExecution.getId()));

        // search is heavily tested in TestExecutionDAO test, hence just verify that the method is called
        TestExecutionSearchCriteria criteria = new TestExecutionSearchCriteria();
        SearchResultWrapper<TestExecution> result = testExecutionService.searchTestExecutions(criteria);

        assertEquals(1, result.getTotalSearchResultsCount());
        assertEquals(createdTestExecution.getId(), result.getResult().get(0).getId());
    }

    /*** HELPER METHODS ***/

    private void assertTestExecution(TestExecution expected, TestExecution actual) {
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getStarted(), actual.getStarted());
        assertEquals(expected.getComment(), actual.getComment());
    }

    private void assertMetric(Metric expected, Metric actual) {
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getComparator(), actual.getComparator());
    }

    private void assertAttachment(TestExecutionAttachment expected, TestExecutionAttachment actual) {
        assertEquals(expected.getFilename(), actual.getFilename());
        assertEquals(expected.getMimetype(), actual.getMimetype());
        assertArrayEquals(expected.getContent(), actual.getContent());
    }

    private void assertParameter(TestExecutionParameter expected, TestExecutionParameter actual) {
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getValue(), actual.getValue());
    }

    private void assertValue(Value expected, Value actual) {
        assertEquals(expected.getResultValue(), actual.getResultValue());
    }

    private void assertTag(Tag expected, Tag actual) {
        assertEquals(expected.getName(), actual.getName());
    }

    private void fillTest(String prefix, Test test) {
        test.setName(prefix + "_name");
        test.setUid(prefix + "_uid");
        test.setDescription(prefix + "_description");
    }

    private void fillTestExecution(String prefix, Test test, TestExecution testExecution) {
        testExecution.setName(prefix + "_name");
        testExecution.setComment(prefix + "_comment");
        testExecution.setTest(test);
    }

    private Metric createMetric(String name) {
        Metric metric = new Metric();
        metric.setName(name + "_name");
        metric.setDescription(name + "_description");
        metric.setComparator(MetricComparator.HIGHER_BETTER);

        return metric;
    }

    private void fillParameter(String prefix, TestExecution testExecution, TestExecutionParameter parameter) {
        parameter.setName(prefix + "_name");
        parameter.setValue(prefix + "_value");
        parameter.setTestExecution(testExecution);
    }

    private void fillValue(double result, Metric metric, TestExecution testExecution, Value value) {
        value.setResultValue(result);
        value.setMetric(metric);
        value.setTestExecution(testExecution);
    }

    private void fillTag(String prefix, Tag tag) {
        tag.setName(prefix + "_name");
    }

    private User createUser(String prefix) {
        User user = new User();
        user.setFirstName(prefix + "_first_name");
        user.setLastName(prefix + "_last_name");
        user.setEmail(prefix + "@email.com");
        user.setUsername(prefix + "_username");
        user.setPassword(prefix + "_password");

        return user;
    }

    private Group createGroup(String name) {
        Group group = new Group();
        group.setName(name);

        return group;
    }

    private TestExecutionAttachment createAttachment(String prefix, TestExecution testExecution) {
        TestExecutionAttachment attachment = new TestExecutionAttachment();
        attachment.setContent((prefix + "_content").getBytes());
        attachment.setFilename(prefix + "_filename");
        attachment.setMimetype(prefix + "_mimetype");
        attachment.setTestExecution(testExecution);

        return attachment;
    }
    
    private void assertSearchResultWithoutOrdering(List<Test> expectedResult, SearchResultWrapper<Test> actualResult) {
        assertEquals(expectedResult.size(), actualResult.getTotalSearchResultsCount());
        assertEquals(expectedResult.size(), actualResult.getResult().size());
        assertTrue(expectedResult.stream().allMatch(expected -> actualResult.getResult().stream()
                .anyMatch(actual -> expected.getId().equals(actual.getId()))));
    }

    private void assertSearchResultWithOrdering(List<Test> expectedResult, SearchResultWrapper<Test> actualResult) {
        assertSearchResultWithOrdering(expectedResult, actualResult, expectedResult.size());
    }

    private void assertSearchResultWithOrdering(List<Test> expectedResult, SearchResultWrapper<Test> actualResult, int expectedTotalCount) {
        assertEquals(expectedTotalCount, actualResult.getTotalSearchResultsCount());
        assertEquals(expectedResult.size(), actualResult.getResult().size());

        IntStream.range(0, expectedResult.size())
                .forEach(index -> assertEquals(expectedResult.get(index), actualResult.getResult().get(index)));
    }

}
