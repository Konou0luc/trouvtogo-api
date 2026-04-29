package com.objetcol.collectobjet.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeSeriesPointResponse {
    private String key;    // e.g. 2024-03
    private String name;   // e.g. Mar
    private long lost;
    private long found;
    private long resolved;
}
