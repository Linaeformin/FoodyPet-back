package org.example.foodypet.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginFormDto {

    // 아이디
    @NotBlank(message = "아이디는 필수 입력 값입니다.")
    private String userId;

    // 비밀번호
    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    private String password;
}
