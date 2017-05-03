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

import static org.junit.Assert.*;

import org.junit.Test;

public class RandomPasswordGeneratorTest {

	@Test
	public void testGenerateWithNumbers() {
		String regex = "[0-9]";
		int minSizeOfPwd = 1;
		int maxSizeOfPwd = 4;
		
		RandomPasswordGenerator generator = new RandomPasswordGenerator(minSizeOfPwd,maxSizeOfPwd);
		generator.addPattern(regex);
		
		String result = generator.generate();

		checkRegEx(regex + "{"+minSizeOfPwd + "," + maxSizeOfPwd +"}", minSizeOfPwd, maxSizeOfPwd, result);
	}
	
	@Test
	public void testGenerateWithSmallCase() {
		String regex = "[a-z]";
		int minSizeOfPwd = 6;
		int maxSizeOfPwd = 10;
		
		RandomPasswordGenerator generator = new RandomPasswordGenerator(minSizeOfPwd,maxSizeOfPwd);
		generator.addPattern(regex);
		
		String result = generator.generate();
		checkRegEx(regex + "{"+minSizeOfPwd + "," + maxSizeOfPwd +"}", minSizeOfPwd, maxSizeOfPwd, result);
	}

	@Test
	public void testGenerateWithUpperCase() {
		String regex = "[A-Z]";
		int minSizeOfPwd = 2;
		int maxSizeOfPwd = 4;
		
		RandomPasswordGenerator generator = new RandomPasswordGenerator(minSizeOfPwd,maxSizeOfPwd);
		generator.addPattern(regex);
		
		String result = generator.generate();
		checkRegEx(regex + "{"+minSizeOfPwd + "," + maxSizeOfPwd +"}", minSizeOfPwd, maxSizeOfPwd, result);
	}
	
	@Test
	public void testGenerateWithSpecialChars() {
		String regex = "[\\W_]";
		int minSizeOfPwd = 1;
		int maxSizeOfPwd = 3;
		
		RandomPasswordGenerator generator = new RandomPasswordGenerator(minSizeOfPwd,maxSizeOfPwd);
		generator.addPattern(regex);
		
		String result = generator.generate();
		checkRegEx(regex + "{"+minSizeOfPwd + "," + maxSizeOfPwd +"}", minSizeOfPwd, maxSizeOfPwd, result);
	}

	@Test
	public void testGenerateWithTwoExpressions() {
		String regex1 = "[0-9]";
		String regex2 = "[a-z]";
				
		int minSizeOfPwd = 3;
		int maxSizeOfPwd = 10;
		
		RandomPasswordGenerator generator = new RandomPasswordGenerator(minSizeOfPwd,maxSizeOfPwd);
		generator.addPattern(regex1);
		generator.addPattern(regex2);
				
		String result = generator.generate();

		checkRegEx("[0-9,a-z]{"+minSizeOfPwd + "," + maxSizeOfPwd +"}", minSizeOfPwd, maxSizeOfPwd, result);
	}
	
	@Test
	public void testGenerateWithMultipleExpressions() {
		String regex1 = "[0-9]";
		String regex2 = "[a-z]";
		String regex3 = "[A-Z]";
        String regex4 = "[\\W_]";
		
		int minSizeOfPwd = 3;
		int maxSizeOfPwd = 10;
		
		RandomPasswordGenerator generator = new RandomPasswordGenerator(minSizeOfPwd,maxSizeOfPwd);
		generator.addPattern(regex1);
		generator.addPattern(regex2);
		generator.addPattern(regex3);
        generator.addPattern(regex4);
		
		String result = generator.generate();

		checkRegEx("[0-9a-zA-Z\\W_]{"+minSizeOfPwd + "," + maxSizeOfPwd +"}", minSizeOfPwd, maxSizeOfPwd, result);
	}
	
	@Test
	public void testGenerateWithMultipleExpressionsWithSmallerMaxSizeThanRegexItems() {
		String regex1 = "[0-9]";
		String regex2 = "[a-z]";
		String regex3 = "[A-Z]";
		
		int minSizeOfPwd = 1;
		int maxSizeOfPwd = 2;
		
		RandomPasswordGenerator generator = new RandomPasswordGenerator(minSizeOfPwd,maxSizeOfPwd);
		generator.addPattern(regex1);
		generator.addPattern(regex2);
		generator.addPattern(regex3);
		
		String result = generator.generate();

		checkRegEx("[0-9,a-z,A-Z]{"+minSizeOfPwd + "," + maxSizeOfPwd +"}", minSizeOfPwd, maxSizeOfPwd, result);
	}
	
	private void checkRegEx(String regex, int minSizeOfPwd, int maxSizeOfPwd,
			String result) {
		assertTrue(result.matches(regex));
		if(result.length() < minSizeOfPwd || result.length() > maxSizeOfPwd ){
			fail("Length of the result doesn't match the min and max length");
		}
	}
	
}
