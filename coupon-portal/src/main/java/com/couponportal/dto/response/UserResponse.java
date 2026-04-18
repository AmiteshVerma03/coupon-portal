package com.couponportal.dto.response;

import com.couponportal.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long   id;
    private String name;
    private String email;
    private Role   role;
    private Long   tenantId;
    private String tenantName;
}
