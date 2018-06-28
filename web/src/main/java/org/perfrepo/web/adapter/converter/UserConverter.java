package org.perfrepo.web.adapter.converter;

import org.perfrepo.dto.user.UserDto;
import org.perfrepo.web.model.user.User;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Converter for User between entity and DTO.
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public class UserConverter {

    private UserConverter() { }

    public static UserDto convertFromEntityToDto(User user) {
        if (user == null) {
            return null;
        }

        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());

        return dto;
    }

    public static List<UserDto> convertFromEntityToDto(List<User> users) {
        return users.stream().map(user -> convertFromEntityToDto(user)).collect(Collectors.toList());
    }

    public static User convertFromDtoToEntity(UserDto dto) {
        if (dto == null) {
            return null;
        }

        User user = new User();
        user.setId(dto.getId());
        user.setUsername(dto.getUsername());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());

        return user;
    }

}
