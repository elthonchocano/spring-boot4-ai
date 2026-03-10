package com.echocano.ai.news.infrastructure.port.input;

import com.echocano.ai.news.infrastructure.adapter.input.dto.NewsSummaryRequest;
import com.echocano.ai.news.infrastructure.adapter.input.dto.NewsSummaryResponse;

public interface GetNewsSummaryInputPort {

    NewsSummaryResponse getSummary(NewsSummaryRequest newsSummaryRequest);
}
