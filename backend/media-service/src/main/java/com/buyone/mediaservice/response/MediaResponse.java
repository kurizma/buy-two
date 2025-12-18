package com.buyone.mediaservice.response;

import java.time.Instant;

public record MediaResponse(
        String id,
        String ownerId,
        String url,          // e.g. /media/images/{id}
        Instant createdAt
) {}