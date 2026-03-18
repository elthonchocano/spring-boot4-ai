package com.echocano.ai.news.infrastructure.adapter.input;

import com.echocano.ai.news.application.validators.ValidCountryCode;
import com.echocano.ai.news.infrastructure.adapter.input.dto.NewsSummaryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "News Service", description = "Endpoints for retrieving news  AI summary")
public interface INewsResource {

    @Operation(summary = "Get News Summary", description = "Fetches latest news for a country and generates an AI summary.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "400"
                    , description = "Validation Failed"
                    , content = @Content(schema = @Schema(implementation = RestExceptionHandler.ErrorDetails.class))),
            @ApiResponse(responseCode = "503"
                    , description = "AI Service or News API is currently unavailable"
                    , content = @Content(schema = @Schema(implementation = RestExceptionHandler.ErrorDetails.class)))
    })
    ResponseEntity<NewsSummaryResponse> getSummary(
            @Parameter(description = "ISO 3166-1 country code (e.g. ca, us)") @ValidCountryCode String country);
}
