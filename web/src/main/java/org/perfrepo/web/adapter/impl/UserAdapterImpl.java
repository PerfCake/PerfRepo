package org.perfrepo.web.adapter.impl;

import org.perfrepo.dto.user.UserDto;
import org.perfrepo.web.adapter.UserAdapter;
import org.perfrepo.web.adapter.converter.UserConverter;
import org.perfrepo.web.model.user.User;
import org.perfrepo.web.service.UserService;
import org.perfrepo.web.service.exceptions.IncorrectPasswordException;

import javax.inject.Inject;
import java.util.List;

/**
 * TODO: document this
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public class UserAdapterImpl implements UserAdapter {

    @Inject
    private UserService userService;

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserDto> dtos = UserConverter.convertFromEntityToDto(users);
        return dtos;
    }

    @Override
    public UserDto getUser(Long id) {
        User user = userService.getUser(id);
        UserDto dto = UserConverter.convertFromEntityToDto(user);
        return dto;
    }

    @Override
    public UserDto updateUser(UserDto user) throws IncorrectPasswordException {
        User entity = UserConverter.convertFromDtoToEntity(user);

        if (user.getPassword() != null && user.getNewPassword().equals(user.getNewPasswordAgain())) {
            userService.changePassword(user.getPassword(), user.getNewPassword());
        }

        UserDto updatedUser = UserConverter.convertFromEntityToDto(userService.updateUser(entity));
        return updatedUser;
    }
}
