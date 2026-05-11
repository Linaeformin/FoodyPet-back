package org.example.foodypet.domain.user.entity;

import org.example.foodypet.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_notification_settings")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserNotificationSetting extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "community_notification_enabled", nullable = false)
    private Boolean communityNotificationEnabled = true;

    @Column(name = "diet_notification_enabled", nullable = false)
    private Boolean dietNotificationEnabled = true;
}
