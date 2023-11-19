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
package org.eclipse.mylyn.wikitext.parser.util;

/**
 * An interface that defines a matcher, much the same as a {@link java.util.regex.Matcher}.
 *
 * @see MatcherAdaper
 * @author David Green
 * @since 3.0
 */
public interface Matcher {
	public String group(int group);

	public int start(int group);

	public int end(int group);
}
