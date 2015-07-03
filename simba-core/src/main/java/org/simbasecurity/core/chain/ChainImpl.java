/*
 * Copyright 2013 Simba Open Source
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
 */
package org.simbasecurity.core.chain;

import org.simbasecurity.core.audit.Audit;
import org.simbasecurity.core.audit.AuditLogEventFactory;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.simbasecurity.core.audit.AuditMessages.CHAIN_FAILURE;
import static org.simbasecurity.core.audit.AuditMessages.CHAIN_SUCCESS;

/**
 * A Chain represents a configured list of {@link org.simbasecurity.core.chain.Command commands} that will be
 * executed in order to perform processing on a specified {@link ChainContext}.
 * <p/>
 * Each included {@link org.simbasecurity.core.chain.Command} will be executed in turn, until either one of
 * them returns {@link org.simbasecurity.core.chain.Command.State#FINISH}, an exception is thrown or the end
 * of the chain has been reached.
 * <p/>
 * <b>Note</b> that Chain extends @{link Command}. This allows for a chains to
 * be easily combined and processed in a hierarchical manner.
 * <p/>
 * Chain implementations should be designed in a thread-safe manner. In general,
 * this implies that Chain classes should not maintain state information in
 * instance variables.
 *
 * @since 1.0
 */
public class ChainImpl implements Chain {

    private static org.slf4j.Logger LOG = LoggerFactory.getLogger(ChainImpl.class);

    private List<Command> commands;
    
    @Autowired private Audit audit;
	@Autowired private AuditLogEventFactory auditLogFactory;

    public void setCommands(List<Command> commands) {
        this.commands = commands;
    }

    @Override
    public State execute(ChainContext context) throws Exception {
        if (context == null) {
            throw new NullPointerException("Context should never by null");
        }

        State savedResult = State.CONTINUE;
        Exception savedException = null;

        int i = 0;
        int n = commands.size();

        for (; i < n; i++) {
        	Command command = null;
            try {
                command = commands.get(i);
                savedResult = command.execute(context);
                context.increaseCommandCounter();

                if (savedResult == State.FINISH) {
                    LOG.debug("Finished in {}.", command.getClass().getSimpleName());
                    audit.log(auditLogFactory.createEventForChainSuccess(context, CHAIN_SUCCESS + command.getClass().getSimpleName()));
                    break;
                }
            } catch (Exception e) {
                savedException = e;
                String commandName = "";
				if(command != null){
					commandName = command.getClass().getName();
				}
                audit.log(auditLogFactory.createEventForChainFailure(context, CHAIN_FAILURE + commandName));
                break;
            }
        }

        if (i >= n) {
            i--;
        }

        boolean handled = false;
        for (int j = i; j >= 0; j--) {
            try {
                boolean result = commands.get(j).postProcess(context, savedException);

                if (result) {
                    handled = true;
                }
            } catch (Exception ignore) {
                // Silently ignore
            }
        }

        if (savedException != null && !handled) {
            throw savedException;
        }

        return savedResult;
    }

    @Override
    public boolean postProcess(ChainContext context, Exception exception) {
        boolean handled = false;
        for (int j = commands.size() - 1; j >= 0; j--) {
            boolean result = commands.get(j).postProcess(context, exception);

            if (result) {
                handled = true;
            }
        }
        return handled;
    }

}
