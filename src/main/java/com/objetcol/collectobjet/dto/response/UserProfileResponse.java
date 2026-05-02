package com.objetcol.collectobjet.dto.response;

import com.objetcol.collectobjet.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {

    private Long id;
    private String username;
    private String email;
    private String telephone;
    private Role role;
    private LocalDateTime createdAt;
}
