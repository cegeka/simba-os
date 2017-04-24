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

package org.simbasecurity.core.util;

import static org.simbasecurity.core.exception.SimbaMessageKey.*;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.SimpleEmail;
import org.simbasecurity.core.exception.SimbaException;

public class MailBuilder {

	private Email email;
	private boolean htmlEmail;
	
	public MailBuilder(String hostName, boolean htmlEmail){
		email = new SimpleEmail();
		if(htmlEmail){
			email = new HtmlEmail();
		}
		email.setHostName(hostName);
		this.htmlEmail= htmlEmail;
	}
	
	
	public void withPort(int portNumber){
		email.setSmtpPort(portNumber);
	}
	
	public void withSubject(String subject){
		email.setSubject(subject);
	}
	
	public void withFrom(String emailFrom){
		try {
			email.setFrom(emailFrom);
		} catch (EmailException e) {
			throw new SimbaException(MAIL_ERROR,e.getMessage());
		}
	}
	
	public void withFrom(String emailFrom,String displayedNameFrom){
		try {
			email.setFrom(emailFrom,displayedNameFrom);
		} catch (EmailException e) {
			throw new SimbaException(MAIL_ERROR,e.getMessage());
		}
	}
	
	public void addTo(String emailAdressesTo){
		try {
			email.addTo(emailAdressesTo);
		} catch (EmailException e) {
			throw new SimbaException(MAIL_ERROR,e.getMessage());
		}
	}
	
	public void withMessage(String msg){
		try {
			if(htmlEmail){
				((HtmlEmail)email).setHtmlMsg(msg);
			}else{
				email.setMsg(msg);				
			}
		} catch (EmailException e) {
			throw new SimbaException(MAIL_ERROR,e.getMessage());
		}
	}
		
	public void withSecureConnection(){
		email.setSSLOnConnect(true);
	}
	
	public void withAuthentication(String username,String password){
		email.setAuthenticator(new DefaultAuthenticator(username, password));
	}
	
	public void send(){
		try {
			email.send();
		} catch (EmailException e) {
			throw new SimbaException(MAIL_ERROR,e.getMessage());
		}
	}
	
}
