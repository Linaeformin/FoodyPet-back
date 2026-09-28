package org.example.foodypet.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.foodypet.common.entity.BaseTimeEntity;

@Entity
@Table(name = "users")
@Getter @Builder @AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", length = 50, nullable = false, unique = true)
    private String userId;

    @Column(length = 20, nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Lob
    @Column(name = "user_img")
    private String userImg;

    @Lob
    private String intro;

    public void updateUserImg(String userImg) {
        this.userImg = userImg;
    }
}
