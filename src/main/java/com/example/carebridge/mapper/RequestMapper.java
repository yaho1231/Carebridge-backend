package com.example.carebridge.mapper;

import com.example.carebridge.dto.RequestDto;
import com.example.carebridge.entity.Request;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RequestMapper {
    @Mapping(target = "status", expression = "java(request.getStatus().toString())")
    RequestDto toDto(Request request);
    
    @Mapping(target = "status", expression = "java(Request.RequestStatus.valueOf(requestDto.getStatus()))")
    Request toEntity(RequestDto requestDto);
} 