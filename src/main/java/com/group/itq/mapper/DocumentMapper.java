package com.group.itq.mapper;

import com.group.itq.dto.DocumentDto;
import com.group.itq.dto.DocumentWithHistoryDto;
import com.group.itq.model.Document;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DocumentMapper {

    DocumentDto documentToDocumentDto(Document document);

    DocumentWithHistoryDto documentToDocumentWithHistoryDto(Document document);
}
