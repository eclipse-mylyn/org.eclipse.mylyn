/*******************************************************************************
 * Copyright (c) 2011, 2012 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.tasks.index.core;

import org.eclipse.osgi.util.NLS;

/**
 * @author David Green
 */
class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.tasks.index.core.messages"; //$NON-NLS-1$

	public static String TaskListIndex_field_notes;

	public static String TaskListIndex_field_attachment;

	public static String TaskListIndex_field_content;

	public static String TaskListIndex_field_identifier;

	public static String TaskListIndex_field_person;

	public static String TaskListIndex_field_repository_url;

	public static String TaskListIndex_indexerJob;

	public static String TaskListIndex_task_rebuilding_index;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}

}
