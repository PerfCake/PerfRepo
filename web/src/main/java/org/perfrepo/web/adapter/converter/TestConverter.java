package org.perfrepo.web.adapter.converter;

import org.perfrepo.dto.test.TestDto;
import org.perfrepo.web.model.Test;

import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO: document this
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public class TestConverter {

    private TestConverter() { }

    public static TestDto convertFromEntityToDto(Test test) {
        if (test == null) {
            return null;
        }

        TestDto dto = new TestDto();
        dto.setId(test.getId());
        dto.setName(test.getName());
        dto.setDescription(test.getDescription());
        dto.setUid(test.getUid());
        dto.setGroup(GroupConverter.convertFromEntityToDto(test.getGroup()));

        return dto;
    }

    public static List<TestDto> convertFromEntityToDto(List<Test> tests) {
        return tests.stream().map(test -> convertFromEntityToDto(test)).collect(Collectors.toList());
    }

    public static Test convertFromDtoToEntity(TestDto dto) {
        if (dto == null) {
            return null;
        }

        Test test = new Test();
        test.setId(dto.getId());
        test.setName(dto.getName());
        test.setDescription(dto.getDescription());
        test.setUid(dto.getUid());
        test.setGroup(GroupConverter.convertFromDtoToEntity(dto.getGroup()));

        return test;
    }
}
