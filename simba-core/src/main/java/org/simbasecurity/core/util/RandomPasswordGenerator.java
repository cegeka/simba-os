package org.simbasecurity.core.util;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

public class RandomPasswordGenerator {
	
	private List<List<Character>> allowedCharsPerPattern = new ArrayList<List<Character>>();

	private int minSizeOfPwd;
	private int maxSizeOfPwd;

	@SuppressWarnings("unused")
	private RandomPasswordGenerator(){
		
	}
	
	public RandomPasswordGenerator(int minSizeOfPwd, int maxSizeOfPwd){
		this.minSizeOfPwd = minSizeOfPwd;
		this.maxSizeOfPwd = maxSizeOfPwd;		
	}
	
	/**
	 * Add a regex pattern that will be used to generate the password.  You call this method multiple times to add more regex to complicate your password. 
	 * @param regex Regex pattern to match.
	 */
	public void addPattern(String regex){
		allowedCharsPerPattern.add(compileAllowedChars(regex));
	}

    private List<Character> compileAllowedChars(String regex) {
        Pattern pattern = Pattern.compile(regex);
        List<Character> allowedChars = new ArrayList<Character>();
        for (char c = ' '; c <= '~'; c++) {
            if (pattern.matcher(Character.valueOf(c).toString()).matches()) {
               allowedChars.add(c);
            }
        }
        return allowedChars;
    }

    public String generate(){
        try {
            Random rnd = SecureRandom.getInstance("SHA1PRNG");
            Collections.shuffle(allowedCharsPerPattern, rnd);

            int passwordSize = rnd.nextInt(maxSizeOfPwd - minSizeOfPwd) + minSizeOfPwd;

            int allowedCharsIdx = 0;
            StringBuilder result = new StringBuilder(passwordSize);

            for (int i = 0; i < passwordSize; i++) {
                List<Character> characters = allowedCharsPerPattern.get(allowedCharsIdx);

                int charIdx = rnd.nextInt(characters.size());
                result.append(characters.get(charIdx));

                allowedCharsIdx++;
                if (allowedCharsIdx >= allowedCharsPerPattern.size()) {
                    allowedCharsIdx = 0;
                }
            }
            return result.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
	}
}
