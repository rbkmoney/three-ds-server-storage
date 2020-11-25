package com.rbkmoney.threeds.server.storage.config;

import com.rbkmoney.threeds.server.storage.ThreeDsServerStorageApplication;
import com.rbkmoney.threeds.server.storage.repository.CardRangeRepository;
import com.rbkmoney.threeds.server.storage.repository.ChallengeFlowTransactionInfoRepository;
import com.rbkmoney.threeds.server.storage.repository.LastUpdatedRepository;
import com.rbkmoney.threeds.server.storage.repository.SerialNumberRepository;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = {ThreeDsServerStorageApplication.class, TestConfig.class, ExcludeDataSourceConfig.class},
        webEnvironment = RANDOM_PORT,
        properties = {"spring.main.allow-bean-definition-overriding=true"})
@TestPropertySource("classpath:application.yml")
@ContextConfiguration(initializers = AbstractConfigWithoutDao.Initializer.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class AbstractConfigWithoutDao {

    @MockBean
    protected ChallengeFlowTransactionInfoRepository challengeFlowTransactionInfoRepository;

    @MockBean
    protected CardRangeRepository cardRangeRepository;

    @MockBean
    protected SerialNumberRepository serialNumberRepository;

    @MockBean
    protected LastUpdatedRepository lastUpdatedRepository;

    public static class Initializer extends ConfigFileApplicationContextInitializer {

        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            super.initialize(configurableApplicationContext);
            TestPropertyValues.of("asyncConfig.enabled=false").applyTo(configurableApplicationContext);
        }
    }
}
