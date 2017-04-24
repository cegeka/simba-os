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
 * A Command encapsulates a unit of processing work to be performed, whose
 * purpose is to examine and/or modify the state of a request/response.
 * Individual commands can be assembled into a {@link org.simbasecurity.core.chain.Chain}, which allows them
 * to either complete the required processing or delegate further processing to
 * the next Command in the {@link org.simbasecurity.core.chain.Chain}.
 * <p/>
 * Command implementations should be designed in a thread-safe manner, suitable
 * for inclusion in multiple {@link org.simbasecurity.core.chain.Chain chains} that might be processed by
 * different threads simultaneously. In general, this implies that Command
 * classes should not maintain state information in instance variables.
 *
 * @since 1.0
 */
public interface Command {

    enum State {
        CONTINUE, FINISH, ERROR
    }

    /**
     * Execute a unit of processing work to be performed. This Command may
     * either complete the required processing and return {@link org.simbasecurity.core.chain.Command.State#FINISH},
     * or delegate the remaining processing to the next Command in a
     * {@link org.simbasecurity.core.chain.Chain} containing this Command by returning {@link org.simbasecurity.core.chain.Command.State#CONTINUE}
     * .
     *
     * @param context the Context to be processed by this Command
     * @return {@link org.simbasecurity.core.chain.Command.State#FINISH} if the processing of this
     *         {@link ChainContext} has been completed, or
     *         {@link org.simbasecurity.core.chain.Command.State#CONTINUE} if the processing of this
     *         {@link ChainContext} should be delegated to a subsequent Command
     *         in the enclosing {@link org.simbasecurity.core.chain.Chain}
     * @throws Exception general purpose exception to indicate abnormal termination
     */
    State execute(ChainContext context) throws Exception;

    /**
     * Execute any cleanup activities, such as releasing resources that were
     * acquired during the execution of this command.
     *
     * @param context   the Context to be processed by this command
     * @param exception the Exception (if any) that was thrown by the last Command
     *                  that was executed; or <tt>null</tt> if there was no exception
     *                  thrown
     * @return If a non-null exception was "handled" by this method (and
     *         therefore need not be rethrown), return <tt>true</tt>; otherwise
     *         return <tt>false</tt>
     */
    boolean postProcess(ChainContext context, Exception exception);
    
}
