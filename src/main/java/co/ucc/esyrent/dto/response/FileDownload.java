package co.ucc.esyrent.dto.response;

import org.springframework.core.io.Resource;

public record FileDownload(Resource resource, String contentType, String fileName) {
}
