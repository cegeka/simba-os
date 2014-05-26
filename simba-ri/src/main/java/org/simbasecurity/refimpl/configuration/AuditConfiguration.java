package org.simbasecurity.refimpl.configuration;

import java.util.Arrays;
import java.util.List;

import org.simbasecurity.core.audit.provider.AuditLogProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuditConfiguration {

    @Qualifier("archivedAuditLogProvider")
    @Autowired
    private AuditLogProvider archivedAuditLogProvider;

    @Qualifier("standardAuditLogProvider")
    @Autowired
    private AuditLogProvider standardAuditLogProvider;

    @Bean
    public List<AuditLogProvider> configureAuditLogProviders() {
        return Arrays.<AuditLogProvider>asList(standardAuditLogProvider, archivedAuditLogProvider);
    }
}
