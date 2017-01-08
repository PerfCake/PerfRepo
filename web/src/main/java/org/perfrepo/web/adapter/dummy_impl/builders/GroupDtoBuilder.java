package org.perfrepo.web.adapter.dummy_impl.builders;

import org.perfrepo.dto.group.GroupDto;

/**
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class GroupDtoBuilder {

    private GroupDto groupDto;

    public GroupDtoBuilder() {
        groupDto = new GroupDto();
    }

    public GroupDtoBuilder name(String name) {
        groupDto.setName(name);
        return this;
    }

    public GroupDto build() {
        return groupDto;
    }
}
