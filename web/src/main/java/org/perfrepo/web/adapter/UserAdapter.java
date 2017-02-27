package org.perfrepo.web.adapter;

import org.perfrepo.dto.user.UserDto;

import java.util.List;

/**
 * Service adapter for {@link org.perfrepo.dto.user.UserDto} object. Adapter supports CRUD and another
 * operations over these objects. TODO adapter is not complete
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public interface UserAdapter {
    /**
     * Return all users.
     *
     * @return List of all users.
     */
    List<UserDto> getAllUsers();
}
