/*******************************************************************************
 * Copyright (c) 2004, 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core;

import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * @author Mik Kersten
 * @since 2.0
 */
public interface IRepositoryConstants {

	public static final String OLD_PROPERTY_SYNCTIME = "synctime"; //$NON-NLS-1$

	public static final String PROPERTY_SYNCTIMESTAMP = "lastsynctimestamp"; //$NON-NLS-1$

	public static final String PROPERTY_TIMEZONE = "timezone"; //$NON-NLS-1$

	public static final String PROPERTY_ENCODING = "encoding"; //$NON-NLS-1$

	public static final String PROPERTY_VERSION = "version"; //$NON-NLS-1$

	public static final String PROPERTY_CONNECTOR_KIND = "kind"; //$NON-NLS-1$

	public static final String PROPERTY_CATEGORY = "category"; //$NON-NLS-1$

	public static final String PROPERTY_URL = "url"; //$NON-NLS-1$

	public static final String PROPERTY_LABEL = "label"; //$NON-NLS-1$

	public static final String PROPERTY_DELIM = ":"; //$NON-NLS-1$

	public static final String KIND_UNKNOWN = "<unknown>"; //$NON-NLS-1$

	/**
	 * @deprecated Use {@link TaskRepository#CATEGORY_TASKS} instead
	 */
	@Deprecated
	public static final String CATEGORY_TASKS = TaskRepository.CATEGORY_TASKS;

	/**
	 * @deprecated Use {@link TaskRepository#CATEGORY_BUGS} instead
	 */
	@Deprecated
	public static final String CATEGORY_BUGS = TaskRepository.CATEGORY_BUGS;

	/**
	 * @deprecated Use {@link TaskRepository#CATEGORY_BUILD} instead
	 */
	@Deprecated
	public static final String CATEGORY_BUILD = TaskRepository.CATEGORY_BUILD;

	/**
	 * @deprecated Use {@link TaskRepository#CATEGORY_REVIEW} instead
	 */
	@Deprecated
	public static final String CATEGORY_REVIEW = TaskRepository.CATEGORY_REVIEW;

	public static final String CATEGORY_OTHER = "org.eclipse.mylyn.category.other"; //$NON-NLS-1$

	public static final String TEMPLATE_VALUE_PREFIX = "template.value.prefix."; //$NON-NLS-1$

}
