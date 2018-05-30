/*******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 * 
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.github.core;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.mylyn.tasks.core.IRepositoryQuery;

/**
 * Utilities for working with {@link IRepositoryQuery} objects.
 * 
 * @author Kevin Sawicki (kevin@github.com)
 */
public abstract class QueryUtils {

	/**
	 * DELIMITER
	 */
	public static final String DELIMITER = "::"; //$NON-NLS-1$

	/**
	 * Set attribute
	 * 
	 * @param key
	 * @param values
	 * @param query
	 */
	public static void setAttribute(String key, Collection<String> values,
			IRepositoryQuery query) {
		if (key == null || query == null)
			return;

		if (values != null && !values.isEmpty()) {
			StringBuilder value = new StringBuilder();
			for (String entry : values)
				value.append(entry).append(DELIMITER);
			query.setAttribute(key, value.toString());
		} else
			query.setAttribute(key, null);

	}

	/**
	 * Get attribute
	 * 
	 * @param key
	 * @param query
	 * @return non-null but possibly empty list
	 */
	public static List<String> getAttributes(String key, IRepositoryQuery query) {
		if (key == null || query == null)
			return Collections.emptyList();

		String attribute = query.getAttribute(key);
		if (attribute == null || attribute.length() == 0)
			return Collections.emptyList();

		List<String> attrs = new LinkedList<String>();
		String[] values = attribute.split(DELIMITER);
		for (String value : values)
			if (value.length() > 0)
				attrs.add(value);

		return attrs;
	}

}
