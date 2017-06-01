/*
 * Copyright 2013-2017 Simba Open Source
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.simbasecurity.core.domain.generator;

import org.simbasecurity.core.domain.validator.PasswordValidator;
import org.simbasecurity.core.exception.SimbaException;
import org.simbasecurity.core.service.config.ConfigurationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.simbasecurity.core.config.SimbaConfigurationParameter.*;

/**
 * A password generator implementation.
 *
 * @author Bart Cremers
 * @since 3.0.0
 */
@Component
public class PasswordGeneratorImpl implements PasswordGenerator {

    private ConfigurationServiceImpl configurationService;
    private PasswordValidator passwordValidator;

    private final Random random;

    public PasswordGeneratorImpl() {
        this(new SecureRandom());
    }

    public PasswordGeneratorImpl(Random random) {
        this.random = random;
    }

    @Override
    public String generatePassword() {
        int length = getPasswordLength();

        return generate(length);
    }

    public String generate(int length) {
        StringBuilder password = new StringBuilder(length);
        List<RuleCharacters> rules = getRuleCharacters();

        do {
            password.setLength(0);
            for (int i = 0; i < length; i++) {
                RuleCharacters ruleCharacters = rules.get(random.nextInt(rules.size()));
                String validCharacters = ruleCharacters.getValidCharacters();
                password.append(validCharacters.charAt(random.nextInt(validCharacters.length())));
            }
        } while (!passwordIsValid(password));
        return password.toString();
    }

    private boolean passwordIsValid(StringBuilder password) {
        try {
            passwordValidator.validatePassword(password.toString());
            return true;
        } catch (SimbaException ignored) {
            return false;
        }
    }

    private Integer getPasswordLength() {
        Integer minLength = configurationService.getValue(PASSWORD_MIN_LENGTH);
        Integer maxLength = configurationService.getValue(PASSWORD_MAX_LENGTH);
        return Math.min(minLength * 4, maxLength);
    }

    private List<RuleCharacters> getRuleCharacters() {
        String validCharacters = configurationService.getValue(PASSWORD_VALID_CHARACTERS);
        List<Character> allValidCharacters = IntStream.rangeClosed(0, 256)
                                                      .boxed()
                                                      .map(i -> (char) ((int) i))
                                                      .filter(c -> String.valueOf(c).matches(validCharacters))
                                                      .collect(Collectors.toList());

        List<String> complexityRule = configurationService.getValue(PASSWORD_COMPLEXITY_RULE);
        return complexityRule.stream()
                             .map(rule -> new RuleCharacters(rule, allValidCharacters))
                             .collect(Collectors.toList());
    }

    @Autowired
    public void setConfigurationService(ConfigurationServiceImpl configurationService) {
        this.configurationService = configurationService;
    }

    @Autowired
    public void setPasswordValidator(PasswordValidator passwordValidator) {
        this.passwordValidator = passwordValidator;
    }

    private static class RuleCharacters {
        private String validCharacters;

        public RuleCharacters(String rule, List<Character> allValidCharacters) {
            this.validCharacters = allValidCharacters.stream()
                                                     .filter(c -> String.valueOf(c).matches(rule))
                                                     .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                                                     .toString();
        }

        public String getValidCharacters() {
            return validCharacters;
        }

        public int getNumberOfCharacters() {
            return validCharacters.length();
        }
    }



}
