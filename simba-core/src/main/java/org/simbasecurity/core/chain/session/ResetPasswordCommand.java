package org.simbasecurity.core.chain.session;

import org.simbasecurity.core.chain.ChainContext;
import org.simbasecurity.core.chain.Command;
import org.springframework.stereotype.Component;

/**
 * The ResetPasswordCommand will send a mail to the given e-mail adress by which the user can reset his password
 * executes the required actions.
 *
 * @since 3.1.7
 */
@Component
public class ResetPasswordCommand implements Command {
    @Override
    public State execute(ChainContext context) throws Exception {
        context.redirectToPasswordReset();
        return State.FINISH;
    }

    @Override
    public boolean postProcess(ChainContext context, Exception exception) {
        return false;
    }
}
