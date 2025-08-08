/*******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.identity.core.gravatar;

import java.util.regex.Pattern;

/**
 * Gravatar constants.
 *
 * @author Kevin Sawicki (kevin@github.com)
 */
public interface IGravatarConstants {

	/**
	 * URL
	 */
	String URL = "https://www.gravatar.com/avatar/"; //$NON-NLS-1$

	/**
	 * HASH_REGEX
	 */
	String HASH_REGEX = "[0-9a-f]{32}"; //$NON-NLS-1$

	/**
	 * HASH_PATTERN
	 */
	Pattern HASH_PATTERN = Pattern.compile(HASH_REGEX);

	/**
	 * HASH_LENGTH
	 */
	int HASH_LENGTH = 32;

	/**
	 * HASH_ALGORITHM
	 */
	String HASH_ALGORITHM = "MD5"; //$NON-NLS-1$

	/**
	 * Charset used for hashing
	 */
	String CHARSET = "CP1252"; //$NON-NLS-1$

}
