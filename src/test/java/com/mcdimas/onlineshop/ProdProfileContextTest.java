package com.mcdimas.onlineshop;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = {
        "SPRING_DATASOURCE_URL=jdbc:h2:mem:prodprofile;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH",
        "SPRING_DATASOURCE_USERNAME=sa",
        "SPRING_DATASOURCE_PASSWORD=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "PORT=0",
        "APP_SEED_DEMO_DATA=true"
})
@ActiveProfiles("prod")
class ProdProfileContextTest {
    @Test
    void contextLoadsWithProductionProfileAndEnvironmentBackedDatasource() {
    }
}
