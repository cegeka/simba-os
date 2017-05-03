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
package org.simbasecurity.core.domain.repository;

import java.util.Collection;

import org.simbasecurity.core.domain.LoginMapping;


public interface LoginMappingRepository extends AbstractRepository<LoginMapping>{

	/**
	 * @return all stored mappings
	 */
	Collection<LoginMapping> findAll();
	
	/**
	 * @param token the token
	 * @return the {@link LoginMapping} defined by the token; or <tt>null</tt> if none is found. 
	 *  It can also be null if it is expired (tokens are cleaned up)
	 * @throws IllegalStateException when to many mappings are found for the token
	 */
	LoginMapping findByToken(String token);
	
	/**
	 * Remove the mapping specified by the token.
	 * 
	 * @param token the token
	 */
	void remove(String token);
}
