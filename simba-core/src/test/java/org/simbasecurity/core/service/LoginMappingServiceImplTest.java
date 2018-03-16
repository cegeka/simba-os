package org.simbasecurity.core.service;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.simbasecurity.core.config.SimbaConfigurationParameter;
import org.simbasecurity.core.domain.LoginMapping;
import org.simbasecurity.core.domain.LoginMappingEntity;
import org.simbasecurity.core.domain.repository.LoginMappingRepository;
import org.simbasecurity.core.service.config.CoreConfigurationService;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LoginMappingServiceImplTest {

    @Rule public final MockitoRule mockitoRule = MockitoJUnit.rule();

    private static final String TOKEN = "token";
    private static final String TARGET_URL = "TARGET_URL";

    @Mock private LoginMappingRepository loginMappingRepository;
    @Mock private CoreConfigurationService configurationService;

    @InjectMocks private LoginMappingServiceImpl service;

    @Test
    public void createMapping_anyTargetURL_createMappingAndPersistsInRepository() {
        LoginMapping mapping = service.createMapping(TARGET_URL);

        assertThat(mapping.getTargetURL()).isEqualTo(TARGET_URL);
        verify(loginMappingRepository).persist(mapping);
    }

    @Test
    public void getMapping_NullToken_ReturnsNull() {
        LoginMapping mapping = service.getMapping(null);

        assertThat(mapping).isNull();
        verify(loginMappingRepository, never()).findByToken(any());
    }

    @Test
    public void getMapping_NonNullToken_LooksUpTokenInRepository() {
        LoginMappingEntity loginMappingEntity = LoginMappingEntity.create(TARGET_URL);
        when(loginMappingRepository.findByToken(TOKEN)).thenReturn(loginMappingEntity);

        LoginMapping mapping = service.getMapping(TOKEN);

        assertThat(mapping).isSameAs(loginMappingEntity);
    }

    @Test
    public void isExpired_NullToken_IsAlwaysExpired() {
        boolean expired = service.isExpired(null);

        assertThat(expired).isTrue();

        verify(loginMappingRepository, never()).findByToken(any());
    }

    @Test
    public void isExpired_NotExpiredToken() {
        when(configurationService.getValue(SimbaConfigurationParameter.MAX_LOGIN_ELAPSED_TIME)).thenReturn(2L);

        LoginMappingEntity loginMappingEntity = LoginMappingEntity.create(TARGET_URL);
        when(loginMappingRepository.findByToken(TOKEN)).thenReturn(loginMappingEntity);

        boolean expired = service.isExpired(TOKEN);

        assertThat(expired).isFalse();
    }

    @Test
    public void isExpired_ExpiredToken() {
        // Negative value to force mappings to always be expired
        when(configurationService.getValue(SimbaConfigurationParameter.MAX_LOGIN_ELAPSED_TIME)).thenReturn(-1L);

        LoginMappingEntity loginMappingEntity = LoginMappingEntity.create(TARGET_URL);
        when(loginMappingRepository.findByToken(TOKEN)).thenReturn(loginMappingEntity);

        boolean expired = service.isExpired(TOKEN);

        assertThat(expired).isTrue();
    }

    @Test
    public void purgeExpiredMappings_NotExpiredTokens_AreNotRemovedFromRepository() {
        when(configurationService.getValue(SimbaConfigurationParameter.MAX_LOGIN_ELAPSED_TIME)).thenReturn(2L);

        LoginMappingEntity loginMappingEntityOne = LoginMappingEntity.create(TARGET_URL);
        LoginMappingEntity loginMappingEntityTwo = LoginMappingEntity.create(TARGET_URL);
        when(loginMappingRepository.findAll()).thenReturn(Arrays.asList(loginMappingEntityOne, loginMappingEntityTwo));

        service.purgeExpiredMappings();

        verify(loginMappingRepository, never()).remove(loginMappingEntityOne);
        verify(loginMappingRepository, never()).remove(loginMappingEntityTwo);
    }

    @Test
    public void purgeExpiredMappings_ExpiredTokens_AreRemovedFromRepository() {
        // Negative value to force mappings to always be expired
        when(configurationService.getValue(SimbaConfigurationParameter.MAX_LOGIN_ELAPSED_TIME)).thenReturn(-1L);

        LoginMappingEntity loginMappingEntityOne = LoginMappingEntity.create(TARGET_URL);
        LoginMappingEntity loginMappingEntityTwo = LoginMappingEntity.create(TARGET_URL);
        when(loginMappingRepository.findAll()).thenReturn(Arrays.asList(loginMappingEntityOne, loginMappingEntityTwo));

        service.purgeExpiredMappings();

        verify(loginMappingRepository).remove(loginMappingEntityOne);
        verify(loginMappingRepository).remove(loginMappingEntityTwo);
    }



    @Test
    public void removeMapping_anyToken_removesFromRepository() {
        service.removeMapping(TOKEN);

        verify(loginMappingRepository).remove(TOKEN);
    }
}