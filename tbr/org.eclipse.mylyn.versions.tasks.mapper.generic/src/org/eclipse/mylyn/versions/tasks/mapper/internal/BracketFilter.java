/*******************************************************************************
 * Copyright (c) 2012 Research Group for Industrial Software (INSO), Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Research Group for Industrial Software (INSO), Vienna University of Technology - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.versions.tasks.mapper.internal;

import java.util.Arrays;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;

/**
 *
 * @author Kilian Matt
 *
 */
public class BracketFilter extends TokenFilter {
	  private TermAttribute attribute;
	  private char[] illegalChars= {'(',')','[',']','{','}'};
	  public BracketFilter(TokenStream in) {
	    super(in);
	    attribute = (TermAttribute) addAttribute(TermAttribute.class);
	    Arrays.sort(illegalChars);
	  }

	  /** Returns the next token in the stream, or null at EOS.
	   * <p>Removes <tt>'s</tt> from the end of words.
	   * <p>Removes dots from acronyms.
	   */
	  public final boolean incrementToken() throws java.io.IOException {
		  if(!input.incrementToken()) return false;

		  char[] buffer = attribute.termBuffer();
		  char[] target = new char[buffer.length];
		  int targetPos=0;
		  for(int i =0; i < buffer.length; i++) {
			  if(Arrays.binarySearch(illegalChars,buffer[i])<0) {
				  target[targetPos++] = buffer[i];
			  }
		  }
		  attribute.setTermBuffer(target,0,targetPos);
		  return true;
	}
}
