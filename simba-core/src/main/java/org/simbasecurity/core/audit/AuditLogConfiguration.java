package org.simbasecurity.core.audit;

import org.jasypt.digest.StandardStringDigester;
import org.jasypt.digest.StringDigester;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuditLogConfiguration {

    @Bean
    public StringDigester integrityDigest() {
        return new StandardStringDigester();
    }
}
