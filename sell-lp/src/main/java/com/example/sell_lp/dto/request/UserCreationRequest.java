package com.example.sell_lp.dto.request;



import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class UserCreationRequest {
    @NotBlank
    @Size(min = 8, max = 20, message = "Mật khẩu phải chứa ít nhất 8 kí tự")
    String password;
    String fullName;
    @NotBlank
    String username;
    @Email
    String email;
    String phone;
    boolean isActive;
    Date createdAt;
}
