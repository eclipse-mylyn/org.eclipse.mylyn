/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.core.validation;

/**
 * A validation rule
 * 
 * @author David Green
 */
public abstract class ValidationRule {

	/**
	 * Starting at the given offset find the next validation problem.
	 * 
	 * @param markup
	 *            the markup content in which a validation problem should be found
	 * @param offset
	 *            the offset at which to start looking for problems
	 * @param length
	 *            the length at which to stop looking for problems
	 * 
	 * @return the validation problem if found, or null if no validation problem was detected
	 */
	public abstract ValidationProblem findProblem(String markup, int offset, int length);
}
