/*******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *******************************************************************************/
package org.eclipse.egit.github.core;

/**
 * 
 */
public abstract class Assert {

	/**
	 * Throw an {@link IllegalArgumentException} if the specified object is
	 * null.
	 * 
	 * @param message
	 * @param object
	 * @throws IllegalArgumentException
	 */
	public static void notNull(String message, Object object)
			throws IllegalArgumentException {
		if (object == null)
			throw new IllegalArgumentException(message);
	}

	/**
	 * Throw an {@link IllegalArgumentException} if the specified object is
	 * null.
	 * 
	 * @param object
	 * @throws IllegalArgumentException
	 */
	public static void notNull(Object object) throws IllegalArgumentException {
		notNull("Illegal null argument", object); //$NON-NLS-1$
	}

	/**
	 * Throw an {@link IllegalArgumentException} if the specified string is
	 * empty. This method does not check if the string is null.
	 * {@link #notNull(Object)} should be used to first assert the string is
	 * non-null if that is a requirement of the argument.
	 * 
	 * @param message
	 * @param string
	 * @throws IllegalArgumentException
	 */
	public static void notEmpty(String message, String string)
			throws IllegalArgumentException {
		if (string.length() == 0)
			throw new IllegalArgumentException(message);
	}

	/**
	 * Throw an {@link IllegalArgumentException} if the specified string is null
	 * or empty.
	 * 
	 * @param string
	 * @throws IllegalArgumentException
	 */
	public static void notEmpty(String string) throws IllegalArgumentException {
		notEmpty("Illegal empty string argument", string); //$NON-NLS-1$
	}
}
