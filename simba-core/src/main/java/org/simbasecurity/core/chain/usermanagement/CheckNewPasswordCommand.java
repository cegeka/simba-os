package org.simbasecurity.core.chain.usermanagement;

import org.simbasecurity.core.chain.ChainContext;
import org.simbasecurity.core.chain.Command;
import org.springframework.stereotype.Component;

@Component
public class CheckNewPasswordCommand implements Command {

    @Override
    public State execute(ChainContext context) throws Exception {
        return State.CONTINUE;
    }

    @Override
    public boolean postProcess(ChainContext context, Exception exception) {
        return false;
    }
}
