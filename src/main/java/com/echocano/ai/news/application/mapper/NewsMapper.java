package com.echocano.ai.news.application.mapper;

import com.echocano.ai.news.domain.News;
import com.echocano.ai.news.infrastructure.adapter.output.newsapi.dto.ArticleResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValueMappingStrategy;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL,
        nullValueCheckStrategy = NullValueCheckStrategy.ON_IMPLICIT_CONVERSION)
public interface NewsMapper {

    List<News> toDomain(List<ArticleResponse> dtos);
}
