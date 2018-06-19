package org.perfrepo.dto.test_execution;

import java.util.Arrays;

/**
 * Represents a attachment of a test execution.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class AttachmentDto {

    private Long id;

    private String filename;

    private String mimeType;

    private long size;

    private byte[] content;

    private String hash;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AttachmentDto)) return false;

        AttachmentDto that = (AttachmentDto) o;

        if (getFilename() != null ? !getFilename().equals(that.getFilename()) : that.getFilename() != null)
            return false;
        if (getMimeType() != null ? !getMimeType().equals(that.getMimeType()) : that.getMimeType() != null)
            return false;
        return Arrays.equals(getContent(), that.getContent());
    }

    @Override
    public int hashCode() {
        int result = getFilename() != null ? getFilename().hashCode() : 0;
        result = 31 * result + (getMimeType() != null ? getMimeType().hashCode() : 0);
        result = 31 * result + Arrays.hashCode(getContent());
        return result;
    }
}