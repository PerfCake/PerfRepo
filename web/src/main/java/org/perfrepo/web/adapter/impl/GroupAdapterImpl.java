package org.perfrepo.web.adapter.impl;

import org.perfrepo.dto.group.GroupDto;
import org.perfrepo.web.adapter.GroupAdapter;
import org.perfrepo.web.adapter.converter.GroupConverter;
import org.perfrepo.web.service.GroupService;

import javax.inject.Inject;
import java.util.List;

/**
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public class GroupAdapterImpl implements GroupAdapter {

    @Inject
    private GroupService groupService;

    @Inject
    private GroupConverter groupConverter;

    @Override
    public List<GroupDto> getAllGroups() {
        return groupConverter.convertFromEntityToDto(groupService.getAllGroups());
    }
}
