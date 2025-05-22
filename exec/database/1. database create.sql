DROP SCHEMA IF EXISTS `health_db` ;

-- -----------------------------------------------------
-- Schema health_db
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `health_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci ;
-- -----------------------------------------------------
-- Schema notification_db
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `notification_db` ;

-- -----------------------------------------------------
-- Schema notification_db
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `notification_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci ;
-- -----------------------------------------------------
-- Schema user_db
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `user_db` ;

-- -----------------------------------------------------
-- Schema user_db
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `user_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci ;
USE `health_db` ;

-- -----------------------------------------------------
-- Table `health_db`.`body_tb`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `health_db`.`body_tb` ;

CREATE TABLE IF NOT EXISTS `health_db`.`body_tb` (
  `body_id` INT NOT NULL AUTO_INCREMENT,
  `body_fat` DECIMAL(38,2) NULL DEFAULT NULL,
  `created_at` DATETIME(6) NOT NULL,
  `deleted_at` DATETIME(6) NULL DEFAULT NULL,
  `height` DECIMAL(38,2) NULL DEFAULT NULL,
  `input_type` ENUM('MANUAL', 'OCR') NOT NULL,
  `is_deleted` BIT(1) NOT NULL,
  `measurement_date` DATE NOT NULL,
  `skeletal_muscle` DECIMAL(38,2) NULL DEFAULT NULL,
  `updated_at` DATETIME(6) NOT NULL,
  `user_id` INT NOT NULL,
  `weight` DECIMAL(38,2) NOT NULL,
  PRIMARY KEY (`body_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `health_db`.`disease_tb`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `health_db`.`disease_tb` ;

CREATE TABLE IF NOT EXISTS `health_db`.`disease_tb` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `category` VARCHAR(50) NOT NULL,
  `name` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `UKertlamwaatspqbfr3ebsxuymy` (`name` ASC) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 41
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `health_db`.`exercise_category_tb`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `health_db`.`exercise_category_tb` ;

CREATE TABLE IF NOT EXISTS `health_db`.`exercise_category_tb` (
  `exercise_category_id` INT NOT NULL AUTO_INCREMENT,
  `created_at` DATETIME(6) NOT NULL,
  `deleted_at` DATETIME(6) NULL DEFAULT NULL,
  `exercise_category_name` VARCHAR(50) NOT NULL,
  `is_deleted` BIT(1) NOT NULL,
  `updated_at` DATETIME(6) NOT NULL,
  PRIMARY KEY (`exercise_category_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `health_db`.`exercise_tb`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `health_db`.`exercise_tb` ;

CREATE TABLE IF NOT EXISTS `health_db`.`exercise_tb` (
  `exercise_id` INT NOT NULL AUTO_INCREMENT,
  `created_at` DATETIME(6) NOT NULL,
  `deleted_at` DATETIME(6) NULL DEFAULT NULL,
  `exercise_calorie` INT NOT NULL,
  `exercise_date` DATETIME(6) NOT NULL,
  `exercise_time` INT NOT NULL,
  `exercise_type_id` INT NOT NULL,
  `is_deleted` BIT(1) NOT NULL,
  `updated_at` DATETIME(6) NOT NULL,
  `user_id` INT NOT NULL,
  PRIMARY KEY (`exercise_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `health_db`.`exercise_type_tb`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `health_db`.`exercise_type_tb` ;

CREATE TABLE IF NOT EXISTS `health_db`.`exercise_type_tb` (
  `exercise_type_id` INT NOT NULL AUTO_INCREMENT,
  `created_at` DATETIME(6) NOT NULL,
  `deleted_at` DATETIME(6) NULL DEFAULT NULL,
  `exercise_calorie` INT NOT NULL,
  `exercise_category_id` INT NOT NULL,
  `exercise_english_name` VARCHAR(50) NOT NULL,
  `exercise_name` VARCHAR(50) NOT NULL,
  `exercise_number` INT NOT NULL,
  `is_deleted` BIT(1) NOT NULL,
  `updated_at` DATETIME(6) NOT NULL,
  PRIMARY KEY (`exercise_type_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `health_db`.`favorite_exercise_tb`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `health_db`.`favorite_exercise_tb` ;

CREATE TABLE IF NOT EXISTS `health_db`.`favorite_exercise_tb` (
  `favorite_id` INT NOT NULL AUTO_INCREMENT,
  `created_at` DATETIME(6) NOT NULL,
  `user_id` INT NOT NULL,
  `exercise_type_id` INT NOT NULL,
  PRIMARY KEY (`favorite_id`),
  INDEX `FK8e0fkxk9ggotw4ub34fxrii9o` (`exercise_type_id` ASC) VISIBLE,
  CONSTRAINT `FK8e0fkxk9ggotw4ub34fxrii9o`
    FOREIGN KEY (`exercise_type_id`)
    REFERENCES `health_db`.`exercise_type_tb` (`exercise_type_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `health_db`.`food_information_tb`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `health_db`.`food_information_tb` ;

CREATE TABLE IF NOT EXISTS `health_db`.`food_information_tb` (
  `food_id` INT NOT NULL AUTO_INCREMENT,
  `base_amount` VARCHAR(255) NOT NULL,
  `calorie` INT NOT NULL,
  `carbohydrate` DECIMAL(6,2) NOT NULL,
  `cholesterol` DECIMAL(6,2) NOT NULL,
  `created_at` DATETIME(6) NOT NULL,
  `deleted_at` DATETIME(6) NULL DEFAULT NULL,
  `fat` DECIMAL(6,2) NOT NULL,
  `food_name` VARCHAR(50) NOT NULL,
  `is_deleted` BIT(1) NOT NULL,
  `protein` DECIMAL(6,2) NOT NULL,
  `saturated_fat` DECIMAL(6,2) NOT NULL,
  `sodium` DECIMAL(6,2) NOT NULL,
  `sweet` DECIMAL(6,2) NOT NULL,
  `trans_fat` DECIMAL(6,2) NOT NULL,
  `updated_at` DATETIME(6) NOT NULL,
  PRIMARY KEY (`food_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `health_db`.`favorite_food_tb`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `health_db`.`favorite_food_tb` ;

CREATE TABLE IF NOT EXISTS `health_db`.`favorite_food_tb` (
  `favorite_id` INT NOT NULL AUTO_INCREMENT,
  `created_at` DATETIME(6) NOT NULL,
  `user_id` INT NOT NULL,
  `food_id` INT NOT NULL,
  PRIMARY KEY (`favorite_id`),
  UNIQUE INDEX `UKdjmbfy4epmyni2ei32mmnaokn` (`user_id` ASC, `food_id` ASC) VISIBLE,
  INDEX `FKs2l80b9ylgbv15abqaaeir565` (`food_id` ASC) VISIBLE,
  CONSTRAINT `FKs2l80b9ylgbv15abqaaeir565`
    FOREIGN KEY (`food_id`)
    REFERENCES `health_db`.`food_information_tb` (`food_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `health_db`.`female_nutrient_intake`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `health_db`.`female_nutrient_intake` ;

CREATE TABLE IF NOT EXISTS `health_db`.`female_nutrient_intake` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `age_group` ENUM('ALL', 'ELDERLY', 'FIFTIES', 'FORTIES', 'SIXTIES', 'TEENS', 'THIRTIES', 'TWENTIES') NOT NULL,
  `calories` DOUBLE NOT NULL,
  `carbohydrates` DOUBLE NOT NULL,
  `fat` DOUBLE NOT NULL,
  `protein` DOUBLE NOT NULL,
  `sodium` DOUBLE NOT NULL,
  `sugar` DOUBLE NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 9
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `health_db`.`female_nutrient_standard`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `health_db`.`female_nutrient_standard` ;

CREATE TABLE IF NOT EXISTS `health_db`.`female_nutrient_standard` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `age_group` ENUM('ALL', 'ELDERLY', 'FIFTIES', 'FORTIES', 'SIXTIES', 'TEENS', 'THIRTIES', 'TWENTIES') NOT NULL,
  `calories_max` INT NULL DEFAULT NULL,
  `calories_min` INT NOT NULL,
  `carbohydrates_max` INT NULL DEFAULT NULL,
  `carbohydrates_min` INT NOT NULL,
  `fat_max` DOUBLE NULL DEFAULT NULL,
  `fat_min` DOUBLE NOT NULL,
  `protein_max` INT NULL DEFAULT NULL,
  `protein_min` INT NOT NULL,
  `sodium` INT NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 7
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `health_db`.`food_allergy_tb`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `health_db`.`food_allergy_tb` ;

CREATE TABLE IF NOT EXISTS `health_db`.`food_allergy_tb` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `UKn5plqkd4ab11lcs10srwim0ro` (`name` ASC) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 27
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `health_db`.`food_set_tb`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `health_db`.`food_set_tb` ;

CREATE TABLE IF NOT EXISTS `health_db`.`food_set_tb` (
  `food_set_id` INT NOT NULL AUTO_INCREMENT,
  `created_at` DATETIME(6) NOT NULL,
  `deleted_at` DATETIME(6) NULL DEFAULT NULL,
  `is_deleted` BIT(1) NOT NULL,
  `name` VARCHAR(50) NOT NULL,
  `updated_at` DATETIME(6) NOT NULL,
  `user_id` INT NOT NULL,
  PRIMARY KEY (`food_set_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `health_db`.`food_set_food_tb`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `health_db`.`food_set_food_tb` ;

CREATE TABLE IF NOT EXISTS `health_db`.`food_set_food_tb` (
  `food_set_food_id` INT NOT NULL AUTO_INCREMENT,
  `quantity` FLOAT NOT NULL,
  `food_id` INT NOT NULL,
  `food_set_id` INT NOT NULL,
  PRIMARY KEY (`food_set_food_id`),
  INDEX `FK50748bcms24ei5jibouhnhi6s` (`food_id` ASC) VISIBLE,
  INDEX `FKat0gl22g4d5ha7n818e38utg` (`food_set_id` ASC) VISIBLE,
  CONSTRAINT `FK50748bcms24ei5jibouhnhi6s`
    FOREIGN KEY (`food_id`)
    REFERENCES `health_db`.`food_information_tb` (`food_id`),
  CONSTRAINT `FKat0gl22g4d5ha7n818e38utg`
    FOREIGN KEY (`food_set_id`)
    REFERENCES `health_db`.`food_set_tb` (`food_set_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `health_db`.`male_nutrient_intake`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `health_db`.`male_nutrient_intake` ;

CREATE TABLE IF NOT EXISTS `health_db`.`male_nutrient_intake` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `age_group` ENUM('ALL', 'ELDERLY', 'FIFTIES', 'FORTIES', 'SIXTIES', 'TEENS', 'THIRTIES', 'TWENTIES') NOT NULL,
  `calories` DOUBLE NOT NULL,
  `carbohydrates` DOUBLE NOT NULL,
  `fat` DOUBLE NOT NULL,
  `protein` DOUBLE NOT NULL,
  `sodium` DOUBLE NOT NULL,
  `sugar` DOUBLE NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 9
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `health_db`.`male_nutrient_standard`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `health_db`.`male_nutrient_standard` ;

CREATE TABLE IF NOT EXISTS `health_db`.`male_nutrient_standard` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `age_group` ENUM('ALL', 'ELDERLY', 'FIFTIES', 'FORTIES', 'SIXTIES', 'TEENS', 'THIRTIES', 'TWENTIES') NOT NULL,
  `calories_max` INT NULL DEFAULT NULL,
  `calories_min` INT NOT NULL,
  `carbohydrates_max` INT NULL DEFAULT NULL,
  `carbohydrates_min` INT NOT NULL,
  `fat_max` DOUBLE NULL DEFAULT NULL,
  `fat_min` DOUBLE NOT NULL,
  `protein_max` INT NULL DEFAULT NULL,
  `protein_min` INT NOT NULL,
  `sodium` INT NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 7
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `health_db`.`meal_tb`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `health_db`.`meal_tb` ;

CREATE TABLE IF NOT EXISTS `health_db`.`meal_tb` (
  `meal_id` INT NOT NULL AUTO_INCREMENT,
  `created_at` DATETIME(6) NOT NULL,
  `deleted_at` DATETIME(6) NULL DEFAULT NULL,
  `is_deleted` BIT(1) NOT NULL,
  `is_meal` BIT(1) NOT NULL,
  `meal_date` DATE NOT NULL,
  `updated_at` DATETIME(6) NOT NULL,
  `user_id` INT NOT NULL,
  PRIMARY KEY (`meal_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `health_db`.`meal_time_tb`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `health_db`.`meal_time_tb` ;

CREATE TABLE IF NOT EXISTS `health_db`.`meal_time_tb` (
  `meal_time_id` INT NOT NULL AUTO_INCREMENT,
  `created_at` DATETIME(6) NOT NULL,
  `deleted_at` DATETIME(6) NULL DEFAULT NULL,
  `eating_time` TIME NOT NULL,
  `is_deleted` BIT(1) NOT NULL,
  `meal_time_image_url` VARCHAR(250) NULL DEFAULT NULL,
  `meal_type` ENUM('BREAKFAST', 'DINNER', 'LUNCH', 'SNACK') NOT NULL,
  `updated_at` DATETIME(6) NOT NULL,
  `meal_id` INT NOT NULL,
  PRIMARY KEY (`meal_time_id`),
  INDEX `FKjj3qfbtg1tri1oj9xlatuw7vn` (`meal_id` ASC) VISIBLE,
  CONSTRAINT `FKjj3qfbtg1tri1oj9xlatuw7vn`
    FOREIGN KEY (`meal_id`)
    REFERENCES `health_db`.`meal_tb` (`meal_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `health_db`.`meal_food_tb`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `health_db`.`meal_food_tb` ;

CREATE TABLE IF NOT EXISTS `health_db`.`meal_food_tb` (
  `meal_food_id` INT NOT NULL AUTO_INCREMENT,
  `food_image_url` VARCHAR(250) NULL DEFAULT NULL,
  `quantity` INT NOT NULL,
  `food_id` INT NOT NULL,
  `meal_time_id` INT NOT NULL,
  PRIMARY KEY (`meal_food_id`),
  INDEX `FKn5rbxrg373a62nymvop08qg3x` (`food_id` ASC) VISIBLE,
  INDEX `FKxmt6apujhqxkem9l6vv92g2g` (`meal_time_id` ASC) VISIBLE,
  CONSTRAINT `FKn5rbxrg373a62nymvop08qg3x`
    FOREIGN KEY (`food_id`)
    REFERENCES `health_db`.`food_information_tb` (`food_id`),
  CONSTRAINT `FKxmt6apujhqxkem9l6vv92g2g`
    FOREIGN KEY (`meal_time_id`)
    REFERENCES `health_db`.`meal_time_tb` (`meal_time_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `health_db`.`step_count_tb`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `health_db`.`step_count_tb` ;

CREATE TABLE IF NOT EXISTS `health_db`.`step_count_tb` (
  `step_count_id` INT NOT NULL AUTO_INCREMENT,
  `created_at` DATETIME(6) NOT NULL,
  `deleted_at` DATETIME(6) NULL DEFAULT NULL,
  `is_deleted` BIT(1) NOT NULL,
  `step_count` INT NULL DEFAULT NULL,
  `step_date` DATE NOT NULL,
  `updated_at` DATETIME(6) NOT NULL,
  `user_id` INT NOT NULL,
  PRIMARY KEY (`step_count_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `health_db`.`user_allergy_tb`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `health_db`.`user_allergy_tb` ;

CREATE TABLE IF NOT EXISTS `health_db`.`user_allergy_tb` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `created_at` DATETIME(6) NOT NULL,
  `user_id` INT NOT NULL,
  `allergy_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `UKrd9jr99e6tw7vpe6oa5kjtb6i` (`user_id` ASC, `allergy_id` ASC) VISIBLE,
  INDEX `FK53e7g20pi7g3wfsyn28j41ime` (`allergy_id` ASC) VISIBLE,
  CONSTRAINT `FK53e7g20pi7g3wfsyn28j41ime`
    FOREIGN KEY (`allergy_id`)
    REFERENCES `health_db`.`food_allergy_tb` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `health_db`.`user_disease_tb`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `health_db`.`user_disease_tb` ;

CREATE TABLE IF NOT EXISTS `health_db`.`user_disease_tb` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `created_at` DATETIME(6) NOT NULL,
  `user_id` INT NOT NULL,
  `disease_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `UKfbvyfabj4al22pgn7nxd479xl` (`user_id` ASC, `disease_id` ASC) VISIBLE,
  INDEX `FKpgh7mbmbu6oi5wv9d61m33lb1` (`disease_id` ASC) VISIBLE,
  CONSTRAINT `FKpgh7mbmbu6oi5wv9d61m33lb1`
    FOREIGN KEY (`disease_id`)
    REFERENCES `health_db`.`disease_tb` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

USE `notification_db` ;

-- -----------------------------------------------------
-- Table `notification_db`.`notification_tb`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `notification_db`.`notification_tb` ;

CREATE TABLE IF NOT EXISTS `notification_db`.`notification_tb` (
  `notification_id` BIGINT NOT NULL AUTO_INCREMENT,
  `is_deleted` BIT(1) NOT NULL,
  `is_read` BIT(1) NOT NULL,
  `message` VARCHAR(255) NULL DEFAULT NULL,
  `notification_type` ENUM('DIET', 'EXERCISE', 'INACTIVE', 'WEIGHT') NOT NULL,
  `sent_at` DATETIME(6) NULL DEFAULT NULL,
  `title` VARCHAR(255) NULL DEFAULT NULL,
  `user_id` INT NOT NULL,
  PRIMARY KEY (`notification_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

USE `user_db` ;

-- -----------------------------------------------------
-- Table `user_db`.`user_tb`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `user_db`.`user_tb` ;

CREATE TABLE IF NOT EXISTS `user_db`.`user_tb` (
  `user_id` INT NOT NULL AUTO_INCREMENT,
  `birthday` DATE NOT NULL,
  `consent_personal` BIT(1) NOT NULL,
  `created_at` DATETIME(6) NOT NULL,
  `deleted_at` DATETIME(6) NULL DEFAULT NULL,
  `email` VARCHAR(50) NOT NULL,
  `fcm_token` VARCHAR(255) NULL DEFAULT NULL,
  `gender` ENUM('F', 'M') NOT NULL,
  `goal` ENUM('WEIGHT_GAIN', 'WEIGHT_LOSS', 'WEIGHT_MAINTENANCE') NOT NULL,
  `height` DECIMAL(6,2) NOT NULL,
  `is_deleted` BIT(1) NOT NULL,
  `location_personal` CHAR(1) NOT NULL,
  `name` VARCHAR(50) NOT NULL,
  `nickname` VARCHAR(50) NOT NULL,
  `notification_enabled` BIT(1) NOT NULL,
  `phone` VARCHAR(20) NOT NULL,
  `provider` VARCHAR(50) NOT NULL,
  `updated_at` DATETIME(6) NOT NULL,
  `weight` DECIMAL(6,2) NOT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE INDEX `UKjjhogmvl1sxad3l1018wkl5wr` (`phone` ASC) VISIBLE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `user_db`.`user_physical_info_tb`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `user_db`.`user_physical_info_tb` ;

CREATE TABLE IF NOT EXISTS `user_db`.`user_physical_info_tb` (
  `user_physical_info_id` BIGINT NOT NULL AUTO_INCREMENT,
  `birthday` DATE NOT NULL,
  `created_at` DATETIME(6) NOT NULL,
  `date` DATE NOT NULL,
  `goal` ENUM('WEIGHT_GAIN', 'WEIGHT_LOSS', 'WEIGHT_MAINTENANCE') NOT NULL,
  `height` DECIMAL(6,2) NOT NULL,
  `updated_at` DATETIME(6) NOT NULL,
  `weight` DECIMAL(6,2) NOT NULL,
  `user_id` INT NOT NULL,
  PRIMARY KEY (`user_physical_info_id`),
  UNIQUE INDEX `UKk42nqlwyetqpgv7ybit3q961u` (`user_id` ASC, `date` ASC) VISIBLE,
  CONSTRAINT `FKpadjxfjwgt283roqmowcfke41`
    FOREIGN KEY (`user_id`)
    REFERENCES `user_db`.`user_tb` (`user_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;