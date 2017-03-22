package org.perfrepo.web.adapter.converter;

import org.perfrepo.dto.test_execution.AttachmentDto;
import org.perfrepo.web.model.TestExecutionAttachment;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * TODO: document this
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public class AttachmentConverter {

    private AttachmentConverter() { }

    public static AttachmentDto convertFromEntityToDto(TestExecutionAttachment attachment) {
        if (attachment == null) {
            return null;
        }

        AttachmentDto dto = new AttachmentDto();
        dto.setId(attachment.getId());
        dto.setContent(attachment.getContent());
        dto.setFilename(attachment.getFilename());
        dto.setMimeType(attachment.getMimetype());

        return dto;
    }

    public static List<AttachmentDto> convertFromEntityToDto(List<TestExecutionAttachment> attachments) {
        List<AttachmentDto> dtos = new ArrayList<>();
        attachments.stream().forEach(attachment -> dtos.add(convertFromEntityToDto(attachment)));
        return dtos;
    }

    public static TestExecutionAttachment convertFromDtoToEntity(AttachmentDto dto) {
        if (dto == null) {
            return null;
        }

        TestExecutionAttachment attachment = new TestExecutionAttachment();
        attachment.setId(dto.getId());
        attachment.setContent(dto.getContent());
        attachment.setFilename(dto.getFilename());
        attachment.setMimetype(dto.getMimeType());

        return attachment;
    }

    public static List<TestExecutionAttachment> convertFromDtoToEntity(List<AttachmentDto> dtos) {
        return dtos.stream().map(dto -> convertFromDtoToEntity(dto)).collect(Collectors.toList());
    }
}
