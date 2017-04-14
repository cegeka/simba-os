package org.simbasecurity.core.domain;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LanguageTest {

    @Test
    public void fromISO639Code() {
        assertThat(Language.fromISO639Code(null)).isNull();
        assertThat(Language.fromISO639Code("random")).isNull();

        assertThat(Language.fromISO639Code("nl")).isEqualTo(Language.nl_NL);
        assertThat(Language.fromISO639Code("fr")).isEqualTo(Language.fr_FR);
        assertThat(Language.fromISO639Code("NL")).isEqualTo(Language.nl_NL);
        assertThat(Language.fromISO639Code("FR")).isEqualTo(Language.fr_FR);
    }
}