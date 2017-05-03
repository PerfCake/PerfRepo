package org.perfrepo.web.adapter.converter;

import org.perfrepo.dto.report.PermissionDto;
import org.perfrepo.web.model.report.Permission;
import org.perfrepo.web.model.user.Group;
import org.perfrepo.web.model.user.User;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Converter for Permission between entity and DTO.
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public class PermissionConverter {

    private PermissionConverter() { }

    public static PermissionDto convertFromEntityToDto(Permission permission) {
        if (permission == null) {
            return null;
        }

        PermissionDto dto = new PermissionDto();
        dto.setLevel(permission.getLevel());
        dto.setGroupId(permission.getGroup() != null ? permission.getGroup().getId() : null);
        dto.setGroupName(permission.getGroup() != null ? permission.getGroup().getName() : null);
        dto.setType(permission.getAccessType());
        dto.setUserFullName(permission.getUser() != null ? permission.getUser().getFirstName() + " " + permission.getUser().getLastName() : null);
        dto.setUserId(permission.getUser() != null ? permission.getUser().getId() : null);

        return dto;
    }

    public static Set<PermissionDto> convertFromEntityToDto(Set<Permission> permission) {
        Set<PermissionDto> dtos = new TreeSet<>();
        permission.stream().forEach(alert -> dtos.add(convertFromEntityToDto(alert)));
        return dtos;
    }

    public static Permission convertFromDtoToEntity(PermissionDto dto) {
        if (dto == null) {
            return null;
        }

        Permission permission = new Permission();
        permission.setAccessType(dto.getType());
        permission.setLevel(dto.getLevel());
        User user = new User();
        user.setId(dto.getUserId());
        permission.setUser(user);
        Group group = new Group();
        group.setId(dto.getGroupId());
        group.setName(dto.getGroupName());
        permission.setGroup(group);

        return permission;
    }

    public static Set<Permission> convertFromDtoToEntity(List<PermissionDto> dtos) {
        Set<Permission> permissions = new TreeSet<>();
        dtos.stream().forEach(permissionDto -> permissions.add(convertFromDtoToEntity(permissionDto)));
        return permissions;
    }
}
