/*******************************************************************************
 * Copyright (c) 2007, 2011 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.tests;

/**
 * a means of filtering a class
 * 
 * @author David Green
 */
public interface ClassFilter {

	/**
	 * indicate if the class should be filtered. Filtered classes are excluded.
	 * 
	 * @param clazz
	 *            the class to test
	 * @return true if the class should be filtered
	 */
	boolean filter(Class<?> clazz);
}
