package co.ucc.esyrent.mapper;

import co.ucc.esyrent.domain.entity.Attachment;
import co.ucc.esyrent.dto.response.FileResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FileMapper {

    @Mapping(target = "fileName", source = "metadata.fileName")
    @Mapping(target = "contentType", source = "metadata.contentType")
    @Mapping(target = "sizeBytes", source = "metadata.sizeBytes")
    @Mapping(target = "storagePath", source = "metadata.storagePath")
    FileResponse toResponse(Attachment attachment);
}
