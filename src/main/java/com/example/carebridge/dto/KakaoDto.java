package com.example.carebridge.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@Data
@NoArgsConstructor
public class KakaoDto {
    private Long id;
    private String nickname;
    private String email;

    public KakaoDto(Long id, String nickname, String email) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
    }
}