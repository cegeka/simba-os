package org.simbasecurity.core.task;

import org.simbasecurity.core.service.communication.token.UserTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PurgeExpiredTokensTask implements QuartzTask {

    private static final Logger LOG = LoggerFactory.getLogger(PurgeExpiredTokensTask.class);

    @Autowired
    private UserTokenService tokenService;

    @Override
    public void execute() {
        LOG.debug("Purge expired tokens");
        tokenService.purgeExpiredTokens();
    }
}
