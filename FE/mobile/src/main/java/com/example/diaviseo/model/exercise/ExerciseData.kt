package com.example.diaviseo.model.exercise

data class Exercise(
    val id: Int,
    val name: String,
    val englishName: String,
    val category: String,
    val calorie: Int
)

object ExerciseData {
    val exerciseList = listOf(
        Exercise(0, "기타 운동", "OTHER_WORKOUT", "일반", 9),
        Exercise(11, "권투", "BOXING", "일반", 11),
        Exercise(16, "댄스", "DANCING", "일반", 6),
        Exercise(31, "프리스비", "FRISBEE_DISC", "일반", 6),
        Exercise(34, "체조", "GYMNASTICS", "일반", 7),
        Exercise(33, "호흡 운동", "GUIDED_BREATHING", "일반", 3),
        Exercise(44, "무술", "MARTIAL_ARTS", "일반", 9),
        Exercise(47, "패러글라이딩", "PARAGLIDING", "일반", 8),
        Exercise(82, "휠체어 운동", "WHEELCHAIR", "일반", 7),

        Exercise(9, "실내 자전거", "BIKING_STATIONARY", "유산소", 9),
        Exercise(13, "맨몸 운동", "CALISTHENICS", "유산소", 8),
        Exercise(25, "일립티컬", "ELLIPTICAL", "유산소", 9),
        Exercise(26, "운동 수업", "EXERCISE_CLASS", "유산소", 7),
        Exercise(36, "고강도 인터벌 트레이닝", "HIGH_INTENSITY_INTERVAL_TRAINING", "유산소", 12),
        Exercise(56, "달리기", "RUNNING", "유산소", 10),
        Exercise(57, "러닝머신", "RUNNING_TREADMILL", "유산소", 10),
        Exercise(68, "계단 오르기", "STAIR_CLIMBING", "유산소", 10),
        Exercise(69, "스텝머신", "STAIR_CLIMBING_MACHINE", "유산소", 9),
        Exercise(79, "걷기", "WALKING", "유산소", 5),

        Exercise(10, "부트 캠프", "BOOT_CAMP", "프리 웨이트", 11),
        Exercise(48, "필라테스", "PILATES", "프리 웨이트", 4),
        Exercise(70, "근력 운동", "STRENGTH_TRAINING", "프리 웨이트", 7),
        Exercise(71, "스트레칭", "STRETCHING", "프리 웨이트", 4),
        Exercise(81, "역도", "WEIGHTLIFTING", "프리 웨이트", 9),
        Exercise(83, "요가", "YOGA", "프리 웨이트", 4),

        Exercise(8, "자전거 타기", "BIKING", "아웃도어", 8),
        Exercise(37, "등산", "HIKING", "아웃도어", 10),
        Exercise(46, "패들링", "PADDLING", "아웃도어", 7),
        Exercise(51, "암벽 등반", "ROCK_CLIMBING", "아웃도어", 12),

        Exercise(53, "조정", "ROWING", "수상스포츠", 7),
        Exercise(54, "조정 머신", "ROWING_MACHINE", "수상스포츠", 7),
        Exercise(58, "요트 타기", "SAILING", "수상스포츠", 3),
        Exercise(59, "스쿠버 다이빙", "SCUBA_DIVING", "수상스포츠", 9),
        Exercise(72, "서핑", "SURFING", "수상스포츠", 8),
        Exercise(73, "오픈워터 수영", "SWIMMING_OPEN_WATER", "수상스포츠", 10),
        Exercise(74, "수영장 수영", "SWIMMING_POOL", "수상스포츠", 10),
        Exercise(80, "수구", "WATER_POLO", "수상스포츠", 10),

        Exercise(38, "아이스하키", "ICE_HOCKEY", "겨울 스포츠", 11),
        Exercise(39, "아이스 스케이팅", "ICE_SKATING", "겨울 스포츠", 9),
        Exercise(61, "스키", "SKIING", "겨울 스포츠", 11),
        Exercise(62, "스노보드", "SNOWBOARDING", "겨울 스포츠", 10),
        Exercise(63, "스노 슈잉", "SNOWSHOEING", "겨울 스포츠", 6),

        Exercise(2, "배드민턴", "BADMINTON", "구기 종목", 6),
        Exercise(4, "야구", "BASEBALL", "구기 종목", 5),
        Exercise(5, "농구", "BASKETBALL", "구기 종목", 8),
        Exercise(14, "크리켓", "CRICKET", "구기 종목", 5),
        Exercise(27, "펜싱", "FENCING", "구기 종목", 7),
        Exercise(28, "미식축구", "FOOTBALL_AMERICAN", "구기 종목", 10),
        Exercise(29, "호주식 축구", "FOOTBALL_AUSTRALIAN", "구기 종목", 10),
        Exercise(32, "골프", "GOLF", "구기 종목", 7),
        Exercise(35, "핸드볼", "HANDBALL", "구기 종목", 8),
        Exercise(50, "라켓볼", "RACQUETBALL", "구기 종목", 10),
        Exercise(52, "롤러 하키", "ROLLER_HOCKEY", "구기 종목", 10),
        Exercise(55, "럭비", "RUGBY", "구기 종목", 11),
        Exercise(60, "스케이팅", "SKATING", "구기 종목", 7),
        Exercise(64, "축구", "SOCCER", "구기 종목", 9),
        Exercise(65, "소프트볼", "SOFTBALL", "구기 종목", 7),
        Exercise(66, "스쿼시", "SQUASH", "구기 종목", 10),
        Exercise(75, "탁구", "TABLE_TENNIS", "구기 종목", 5),
        Exercise(76, "테니스", "TENNIS", "구기 종목", 8),
        Exercise(78, "배구", "VOLLEYBALL", "구기 종목", 6)
    )
}
