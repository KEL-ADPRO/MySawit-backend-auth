package com.mysawit.mysawit_auth.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignRequest {
    @NotNull(message = "Buruh ID is required")
    private UUID buruhId;

    @NotNull(message = "Mandor ID is required")
    private UUID mandorId;
}
