package com.buyone.mediaservice.response;

import java.util.List;

public record MediaListResponse(List<MediaResponse> images, int count, int maxImages) {}

