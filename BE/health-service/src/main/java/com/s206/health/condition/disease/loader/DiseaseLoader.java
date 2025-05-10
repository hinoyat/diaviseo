package com.s206.health.condition.disease.loader;

import com.s206.health.condition.disease.entity.Disease;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class DiseaseLoader {

    @PersistenceContext
    private EntityManager entityManager;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void loadDiseases() {
        if (isAlreadyLoaded()) return;

        save("고혈압", "심혈관계");
        save("당뇨병", "대사성");
        save("고지혈증", "대사성");
        save("심부전", "심혈관계");
        save("협심증", "심혈관계");
        save("심근경색", "심혈관계");
        save("뇌졸중", "뇌혈관");
        save("간경변", "간질환");
        save("지방간", "간질환");
        save("신부전", "신장질환");
        save("만성신염", "신장질환");
        save("천식", "호흡기");
        save("만성 폐쇄성 폐질환", "호흡기");
        save("결핵", "감염성");
        save("위염", "소화기");
        save("위궤양", "소화기");
        save("대장염", "소화기");
        save("크론병", "소화기");
        save("갑상선 기능 저하증", "내분비");
        save("갑상선 기능 항진증", "내분비");
        save("골다공증", "근골격계");
        save("류마티스 관절염", "면역계");
        save("루푸스", "면역계");
        save("건선", "피부질환");
        save("아토피 피부염", "피부질환");
        save("비염", "호흡기");
        save("알츠하이머병", "신경계");
        save("파킨슨병", "신경계");
        save("간암", "암");
        save("폐암", "암");
        save("위암", "암");
        save("유방암", "암");
        save("전립선암", "암");
        save("자궁경부암", "암");
        save("간질", "신경계");
        save("우울증", "정신과");
        save("공황장애", "정신과");
        save("불안장애", "정신과");
        save("수면장애", "정신과");
        save("식이장애", "정신과/영양");
    }

    private boolean isAlreadyLoaded() {
        Long count = entityManager
                .createQuery("SELECT COUNT(d) FROM Disease d", Long.class)
                .getSingleResult();
        return count > 0;
    }

    private void save(String name, String category) {
        Disease disease = new Disease();
        disease.setName(name);
        disease.setCategory(category);
        entityManager.persist(disease);
    }
}
