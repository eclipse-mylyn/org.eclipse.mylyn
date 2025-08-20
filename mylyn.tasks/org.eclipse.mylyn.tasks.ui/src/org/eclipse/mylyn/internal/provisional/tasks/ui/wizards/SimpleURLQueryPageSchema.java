/*******************************************************************************
 * Copyright (c) 2015 Frank Becker and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.provisional.tasks.ui.wizards;

import org.eclipse.mylyn.tasks.core.data.TaskAttribute;

@SuppressWarnings("nls")
public class SimpleURLQueryPageSchema extends AbstractQueryPageSchema {
	private static final SimpleURLQueryPageSchema instance = new SimpleURLQueryPageSchema();

	public SimpleURLQueryPageSchema() {
	}

	public static SimpleURLQueryPageSchema getInstance() {
		return instance;
	}

	public final Field wholeUrl = createField("wholeQueryURL", "URL:", TaskAttribute.TYPE_URL);
}
