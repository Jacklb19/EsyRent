package co.ucc.esyrent.domain.valueobject;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class AttachmentMetadata {

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Column(name = "size_bytes", nullable = false)
    private Long sizeBytes;

    @Column(name = "storage_path", nullable = false)
    private String storagePath;

    protected AttachmentMetadata() {
    }

    public AttachmentMetadata(String fileName, String contentType, Long sizeBytes, String storagePath) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.sizeBytes = sizeBytes;
        this.storagePath = storagePath;
    }

    public String getFileName() {
        return fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public Long getSizeBytes() {
        return sizeBytes;
    }

    public String getStoragePath() {
        return storagePath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AttachmentMetadata that)) {
            return false;
        }
        return Objects.equals(fileName, that.fileName)
                && Objects.equals(contentType, that.contentType)
                && Objects.equals(sizeBytes, that.sizeBytes)
                && Objects.equals(storagePath, that.storagePath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileName, contentType, sizeBytes, storagePath);
    }
}
