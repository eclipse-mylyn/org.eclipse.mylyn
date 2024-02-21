/*******************************************************************************
 * Copyright (c) 2015, 2024 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     Alexander Fedorov (ArSysOp) - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.commonmark.internal;

public class ToStringHelper {

	private static final String ELIPSES = "..."; //$NON-NLS-1$

	private static final int STRING_MAX_LENGTH = 20;

	public static String toStringValue(String text) {
		if (text == null) {
			return text;
		}
		String stringValue = text;
		if (stringValue.length() > 20) {
			stringValue = stringValue.substring(0, STRING_MAX_LENGTH) + ELIPSES;
		}
		return stringValue.replace("\t", "\\t").replace("\n", "\\n").replace("\r", "\\r"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
	}

	private ToStringHelper() {
		// prevent instantiation
	}
}
