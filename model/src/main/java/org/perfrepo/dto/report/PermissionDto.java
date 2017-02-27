package org.perfrepo.dto.report;

import org.perfrepo.enums.AccessLevel;
import org.perfrepo.enums.AccessType;

/**
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class PermissionDto {

    private AccessLevel level;

    private AccessType type;

    private Long groupId;

    private Long userId;

    private String groupName;

    private String userFullName;

    public AccessLevel getLevel() {
        return level;
    }

    public void setLevel(AccessLevel level) {
        this.level = level;
    }

    public AccessType getType() {
        return type;
    }

    public void setType(AccessType type) {
        this.type = type;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }
}