package com.s206.health.condition.allergy.loader;

import com.s206.health.condition.allergy.entity.FoodAllergy;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class FoodAllergyLoader {

    @PersistenceContext
    private EntityManager entityManager;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void loadFoodAllergies() {
        if (isAlreadyLoaded()) return;

        save("우유");
        save("계란");
        save("메밀");
        save("땅콩");
        save("대두");
        save("밀");
        save("고등어");
        save("게");
        save("새우");
        save("복숭아");
        save("토마토");
        save("호두");
        save("닭고기");
        save("쇠고기");
        save("오징어");
        save("조개류(굴, 전복, 홍합)");
        save("아몬드");
        save("잣");
        save("캐슈넛");
        save("키위");
        save("바나나");
        save("망고");
        save("파인애플");
        save("감귤");
        save("초콜릿");
        save("꿀");
    }

    private boolean isAlreadyLoaded() {
        Long count = entityManager
                .createQuery("SELECT COUNT(f) FROM FoodAllergy f", Long.class)
                .getSingleResult();
        return count > 0;
    }

    private void save(String name) {
        FoodAllergy allergy = new FoodAllergy();
        allergy.setName(name);
        entityManager.persist(allergy);
    }
}
