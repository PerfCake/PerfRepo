package org.perfrepo.web.adapter.dummy_impl.builders;

import org.perfrepo.dto.user.UserDto;

/**
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class UserDtoBuilder {

    private UserDto userDto;

    public UserDtoBuilder() {
        userDto = new UserDto();
    }

    public UserDtoBuilder username(String username) {
        userDto.setUsername(username);
        return this;
    }

    public UserDtoBuilder firstName(String firstName) {
        userDto.setFirstName(firstName);
        return this;
    }

    public UserDtoBuilder lastName(String lastName) {
        userDto.setLastName(lastName);
        return this;
    }

    public UserDtoBuilder email(String email) {
        userDto.setEmail(email);
        return this;
    }

    public UserDtoBuilder password(String password) {
        userDto.setPassword(password);
        return this;
    }

    public UserDto build() {
        return userDto;
    }
}
