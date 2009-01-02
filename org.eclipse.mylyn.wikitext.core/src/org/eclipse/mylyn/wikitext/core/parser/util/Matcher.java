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
package org.eclipse.mylyn.wikitext.core.parser.util;

/**
 * An interface that defines a matcher, much the same as a {@link java.util.regex.Matcher}.
 * 
 * @see MatcherAdaper
 * 
 * @author David Green
 */
public interface Matcher {
	public String group(int group);

	public int start(int group);

	public int end(int group);
}
