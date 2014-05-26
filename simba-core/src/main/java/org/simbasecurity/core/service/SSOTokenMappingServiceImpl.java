package org.simbasecurity.core.service;

import java.util.Collection;

import org.simbasecurity.api.service.thrift.SSOToken;
import org.simbasecurity.core.domain.SSOTokenMapping;
import org.simbasecurity.core.domain.SSOTokenMappingEntity;
import org.simbasecurity.core.domain.repository.SSOTokenMappingDatabaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SSOTokenMappingServiceImpl implements SSOTokenMappingService {

    @Autowired private SSOTokenMappingDatabaseRepository ssoTokenMappingDatabaseRepository;

    @Override
    public SSOTokenMapping createMapping(SSOToken token) {
        SSOTokenMappingEntity mapping = new SSOTokenMappingEntity(token);
        ssoTokenMappingDatabaseRepository.persist(mapping);
        return mapping;
    }

    @Override
    public SSOToken getSSOToken(String ssoTokenKey) {
        if (ssoTokenKey == null) return null;
        SSOTokenMapping tokenMapping = ssoTokenMappingDatabaseRepository.findByToken(ssoTokenKey);
        return tokenMapping == null ? null : tokenMapping.getSSOToken();
    }

    @Override
    public void destroyMapping(String ssoTokenKey) {
        if (ssoTokenKey != null) {
            ssoTokenMappingDatabaseRepository.remove(ssoTokenKey);
        }
    }

    @Override
    public void purgeExpiredMappings() {
        Collection<SSOTokenMapping> mappings = ssoTokenMappingDatabaseRepository.findAll();

        for (SSOTokenMapping mapping : mappings) {
            if (mapping.isExpired()) {
                ssoTokenMappingDatabaseRepository.remove(mapping);
            }
        }
    }
}
