/*******************************************************************************
 * Copyright (c) 2024 GK Software SE, and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *      See git history
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.internal.util;

public class Preconditions {

	/**
	 * @param expression
	 *            this should be {@code true} otherwise an {@link IllegalArgumentException} is thrown.
	 * @param errorMessage
	 *            optional error message
	 * @throws IllegalArgumentException
	 */
	public static void checkArgument(boolean expression, String... errorMessage) {
		if (!expression) {
			if (errorMessage == null || errorMessage.length == 0) {
				throw new IllegalArgumentException();
			}
			throw new IllegalArgumentException(errorMessage[0]);
		}
	}

	/**
	 * @param expression
	 *            this should be {@code true} otherwise an {@link IllegalStateException} is thrown.
	 * @throws IllegalStateException
	 */
	public static void checkState(boolean expression) {
		if (!expression) {
			throw new IllegalStateException();
		}
	}

	/**
	 * @param expression
	 *            this should be {@code true} otherwise an {@link IllegalStateException} is thrown.
	 * @param errorMessage
	 *            error message which may contain placeholders
	 * @param messageArguments
	 *            optional message arguments to be interpolated into the message
	 * @throws IllegalStateException
	 */
	public static void checkState(boolean expression, String errorMessage, Object... messageArguments) {
		if (!expression) {
			if (messageArguments == null || messageArguments.length == 0) {
				throw new IllegalStateException(errorMessage);
			}
			throw new IllegalStateException(String.format(errorMessage, messageArguments));
		}
	}
}
