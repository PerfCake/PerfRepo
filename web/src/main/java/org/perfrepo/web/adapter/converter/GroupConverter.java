package org.perfrepo.web.adapter.converter;

import org.perfrepo.dto.group.GroupDto;
import org.perfrepo.web.model.user.Group;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Converter for Group between entity and DTO.
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public class GroupConverter {

    private GroupConverter() { }

    public static GroupDto convertFromEntityToDto(Group group) {
        if (group == null) {
            return null;
        }

        GroupDto dto = new GroupDto();
        dto.setId(group.getId());
        dto.setName(group.getName());
        return dto;
    }

    public static List<GroupDto> convertFromEntityToDto(List<Group> groups) {
        return groups.stream().map(group -> convertFromEntityToDto(group)).collect(Collectors.toList());
    }

    public static Group convertFromDtoToEntity(GroupDto dto) {
        if (dto == null) {
            return null;
        }

        Group group = new Group();
        group.setId(dto.getId());
        group.setName(dto.getName());

        return group;
    }

}
