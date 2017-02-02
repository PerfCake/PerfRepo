package org.perfrepo.web.adapter.converter;

import org.perfrepo.dto.test.TestDto;
import org.perfrepo.web.model.Test;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO: document this
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public class TestConverter {

    @Inject
    private GroupConverter groupConverter;

    @Inject
    private MetricConverter metricConverter;

    @Inject
    private AlertConverter alertConverter;

    public TestDto convertFromEntityToDto(Test test) {
        if (test == null) {
            return null;
        }

        TestDto dto = new TestDto();
        dto.setId(test.getId());
        dto.setName(test.getName());
        dto.setDescription(test.getDescription());
        dto.setUid(test.getUid());
        dto.setGroup(groupConverter.convertFromEntityToDto(test.getGroup()));

        return dto;
    }

    public List<TestDto> convertFromEntityToDto(List<Test> tests) {
        return tests.stream().map(test -> convertFromEntityToDto(test)).collect(Collectors.toList());
    }

    public Test convertFromDtoToEntity(TestDto dto) {
        if (dto == null) {
            return null;
        }

        Test test = new Test();
        test.setId(dto.getId());
        test.setName(dto.getName());
        test.setDescription(dto.getDescription());
        test.setUid(dto.getUid());
        test.setGroup(groupConverter.convertFromDtoToEntity(dto.getGroup()));

        return test;
    }
}
