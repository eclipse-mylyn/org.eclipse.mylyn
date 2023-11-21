/*******************************************************************************
 * Copyright (c) 2007, 2009 David Green and others.
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
 * Adapt a regex matcher to a {@link Matcher}.
 *
 * @author David Green
 * @since 3.0
 */
public class MatcherAdaper implements Matcher {

	private final java.util.regex.Matcher delegate;

	public MatcherAdaper(java.util.regex.Matcher delegate) {
		this.delegate = delegate;
	}

	@Override
	public int end(int group) {
		return delegate.end(group);
	}

	@Override
	public String group(int group) {
		return delegate.group(group);
	}

	@Override
	public int start(int group) {
		return delegate.start(group);
	}

}
