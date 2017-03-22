package org.perfrepo.web.adapter.converter;

import org.perfrepo.web.model.Tag;

import java.util.Set;
import java.util.TreeSet;

/**
 * TODO: document this
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public class TagConverter {

    private TagConverter() { }

    public static String convertFromEntityToDto(Tag tag) {
        if (tag == null) {
            return null;
        }

        return tag.getName();
    }

    public static Set<String> convertFromEntityToDto(Set<Tag> tags) {
        Set<String> dtos = new TreeSet<>();
        tags.stream().forEach(tag -> dtos.add(convertFromEntityToDto(tag)));
        return dtos;
    }

    public static Tag convertFromDtoToEntity(String dto) {
        if (dto == null) {
            return null;
        }

        Tag tag = new Tag();
        tag.setName(dto);
        return tag;
    }

    public static Set<Tag> convertFromDtoToEntity(Set<String> dtos) {
        Set<Tag> tags = new TreeSet<>();
        dtos.stream().forEach(dto -> { Tag tag = new Tag(); tag.setName(dto); tags.add(tag); });
        return tags;
    }
}
