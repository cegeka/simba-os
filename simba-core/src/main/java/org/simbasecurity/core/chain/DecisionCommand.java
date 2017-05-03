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
package org.simbasecurity.core.chain;

/**
 * The decision command is a {@link org.simbasecurity.core.chain.Command} allowing to execute one of to
 * {@link org.simbasecurity.core.chain.Command commands} depending on a {@link org.simbasecurity.core.chain.Decision}.
 *
 * @see org.simbasecurity.core.chain.Decision
 * @see org.simbasecurity.core.chain.Command
 * @since 1.0
 */
public class DecisionCommand implements Command {
    private Decision decision;
    private Command trueCommand;
    private Command falseCommand;

    public DecisionCommand(Decision decision, Command trueCommand, Command falseCommand) {
        this.trueCommand = trueCommand;
        this.falseCommand = falseCommand;
        this.decision = decision;
    }

    @Override
    public State execute(ChainContext context) throws Exception {
        if (decision.applies(context)) {
            return trueCommand.execute(context);
        }
        return falseCommand.execute(context);
    }

    @Override
    public boolean postProcess(ChainContext context, Exception exception) {
        if (decision.applies(context)) {
            return trueCommand.postProcess(context, exception);
        }
        return falseCommand.postProcess(context, exception);
    }

}
