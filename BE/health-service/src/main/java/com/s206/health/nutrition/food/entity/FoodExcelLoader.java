//package com.s206.health.nutrition.food.entity;
//
//import com.s206.health.nutrition.food.repository.FoodRepository;
//import org.apache.poi.ss.usermodel.*;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.context.event.ApplicationReadyEvent;
//import org.springframework.context.event.EventListener;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.io.InputStream;
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//
//@Component
//public class FoodExcelLoader {
//
//    @Autowired
//    private FoodRepository foodRepository;
//
//    @EventListener(ApplicationReadyEvent.class)
//    @Transactional
//    public void loadData() {
//        try {
//            if (isDataLoaded()) {
//                return;
//            }
//
//            loadFoodDataFromExcel("food_information.xlsx");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private boolean isDataLoaded() {
//        return foodRepository.count() > 0;
//    }
//
//    public void loadFoodDataFromExcel(String resourceFileName) throws Exception {
//        InputStream is = getClass().getClassLoader().getResourceAsStream(resourceFileName);
//        if (is == null) {
//            throw new IllegalArgumentException("리소스 파일을 찾을 수 없습니다: " + resourceFileName);
//        }
//
//        Workbook workbook = new XSSFWorkbook(is);
//        Sheet sheet = workbook.getSheetAt(0);
//
//        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
//            Row row = sheet.getRow(i);
//            if (row == null) continue;
//
//            String foodName = getString(row, 0);
//            if (foodRepository.existsByFoodName(foodName)) continue;
//
//            Food food = Food.builder()
//                    .foodName(foodName)
//                    .calorie((int) getNumeric(row, 1))
//                    .protein(BigDecimal.valueOf(getNumeric(row, 2)))
//                    .fat(BigDecimal.valueOf(getNumeric(row, 3)))
//                    .carbohydrate(BigDecimal.valueOf(getNumeric(row, 4)))
//                    .sweet(BigDecimal.valueOf(getNumeric(row, 5)))
//                    .sodium(BigDecimal.valueOf(getNumeric(row, 6)))
//                    .cholesterol(BigDecimal.valueOf(getNumeric(row, 7)))
//                    .saturatedFat(BigDecimal.valueOf(getNumeric(row, 8)))
//                    .transFat(BigDecimal.valueOf(getNumeric(row, 9)))
//                    .baseAmount(getString(row, 10))
//                    .createdAt(LocalDateTime.now())
//                    .updatedAt(LocalDateTime.now())
//                    .isDeleted(false)
//                    .build();
//
//            foodRepository.save(food);
//        }
//
//        workbook.close();
//        is.close();
//    }
//
//    private String getString(Row row, int cellIndex) {
//        Cell cell = row.getCell(cellIndex);
//        if (cell == null) return "";
//        return switch (cell.getCellType()) {
//            case STRING -> cell.getStringCellValue().trim();
//            case NUMERIC -> String.valueOf((int) cell.getNumericCellValue());
//            default -> "";
//        };
//    }
//
//    private double getNumeric(Row row, int cellIndex) {
//        Cell cell = row.getCell(cellIndex);
//        return (cell == null) ? 0 : cell.getNumericCellValue();
//    }
//}

// TODO: 개발 종료 시 미사용이면 삭제