/*******************************************************************************
 * Copyright (c) 2024 George Lindholm
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html.
 *
 * Contributors:
 *      See git history
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.util;

import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Duplicate Guava's ToStringHelper() format
 *
 * @since 4.4
 */
public class WikiToStringStyle extends ToStringStyle {
	private static final long serialVersionUID = 1L;

	public static final WikiToStringStyle WIKI_TO_STRING_STYLE = new WikiToStringStyle();

	private WikiToStringStyle() {
		setUseShortClassName(true);
		setUseIdentityHashCode(false);
		setFieldSeparator(", "); //$NON-NLS-1$
		setContentStart("{"); //$NON-NLS-1$
		setContentEnd("}"); //$NON-NLS-1$
		setNullText("null"); //$NON-NLS-1$
	}
}
