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

package org.eclipse.mylyn.wikitext.tests;

/**
 * a means of filtering a class
 * 
 * @author dgreen
 */
public interface ClassFilter {

	/**
	 * indicate if the class should be filtered. Filtered classes are excluded.
	 * 
	 * @param clazz
	 *            the class to test
	 * 
	 * @return true if the class should be filtered
	 */
	public boolean filter(Class<?> clazz);
}
