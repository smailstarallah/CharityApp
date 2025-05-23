package ma.emsi.charityapp.config;

import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.validation.Validator;

import java.util.Map;

@Configuration
@Lazy
class SpringValidatorConfiguration {

    @Bean
    @Lazy
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(final Validator validator) {
        return new HibernatePropertiesCustomizer() {

            @Override
            public void customize(Map<String, Object> hibernateProperties) {
                hibernateProperties.put("jakarta.persistence.validation.factory", validator);
            }
        };
    }
}
