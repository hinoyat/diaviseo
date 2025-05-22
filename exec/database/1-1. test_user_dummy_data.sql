INSERT INTO `user_db`.`user_tb` (
    `user_id`,
    `birthday`,
    `consent_personal`,
    `created_at`,
    `deleted_at`,
    `email`,
    `fcm_token`,
    `gender`,
    `goal`,
    `height`,
    `is_deleted`,
    `location_personal`,
    `name`,
    `nickname`,
    `notification_enabled`,
    `phone`,
    `provider`,
    `updated_at`,
    `weight`
) VALUES (
    1,                                 -- user_id (예: PK)
    '1997-05-21',                      -- birthday
    true,                              -- consent_personal
    NOW(),                             -- created_at
    NULL,                              -- deleted_at
    'testuser@example.com',            -- email
    'dummy_fcm_token_123',             -- fcm_token
    'MALE',                            -- gender (예: 'MALE' or 'FEMALE')
    '체중 감량',                         -- goal
    175,                               -- height (cm)
    false,                             -- is_deleted
    '서울특별시 강남구',                 -- location_personal
    '홍길동',                            -- name
    '길동이',                            -- nickname
    true,                              -- notification_enabled
    '01012345678',                     -- phone
    'google',                          -- provider
    NOW(),                             -- updated_at
    70                                 -- weight (kg)
);
