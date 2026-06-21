package com.quickbite.user_service.models.dtos;

import org.openapitools.jackson.nullable.JsonNullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {
    private JsonNullable<String> fullName = JsonNullable.undefined();
    private JsonNullable<String> username = JsonNullable.undefined();
    private JsonNullable<String> password = JsonNullable.undefined();
}
