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

	String OLD_PROPERTY_SYNCTIME = "synctime"; //$NON-NLS-1$

	String PROPERTY_SYNCTIMESTAMP = "lastsynctimestamp"; //$NON-NLS-1$

	String PROPERTY_TIMEZONE = "timezone"; //$NON-NLS-1$

	String PROPERTY_ENCODING = "encoding"; //$NON-NLS-1$

	String PROPERTY_VERSION = "version"; //$NON-NLS-1$

	String PROPERTY_CONNECTOR_KIND = "kind"; //$NON-NLS-1$

	String PROPERTY_CATEGORY = "category"; //$NON-NLS-1$

	String PROPERTY_URL = "url"; //$NON-NLS-1$

	String PROPERTY_LABEL = "label"; //$NON-NLS-1$

	String PROPERTY_DELIM = ":"; //$NON-NLS-1$

	String KIND_UNKNOWN = "<unknown>"; //$NON-NLS-1$

	/**
	 * @deprecated Use {@link TaskRepository#CATEGORY_TASKS} instead
	 */
	@Deprecated
	String CATEGORY_TASKS = TaskRepository.CATEGORY_TASKS;

	/**
	 * @deprecated Use {@link TaskRepository#CATEGORY_BUGS} instead
	 */
	@Deprecated
	String CATEGORY_BUGS = TaskRepository.CATEGORY_BUGS;

	/**
	 * @deprecated Use {@link TaskRepository#CATEGORY_BUILD} instead
	 */
	@Deprecated
	String CATEGORY_BUILD = TaskRepository.CATEGORY_BUILD;

	/**
	 * @deprecated Use {@link TaskRepository#CATEGORY_REVIEW} instead
	 */
	@Deprecated
	String CATEGORY_REVIEW = TaskRepository.CATEGORY_REVIEW;

	String CATEGORY_OTHER = "org.eclipse.mylyn.category.other"; //$NON-NLS-1$

	String TEMPLATE_VALUE_PREFIX = "template.value.prefix."; //$NON-NLS-1$

	/**
	 * Key for a repository property storing a stringified boolean ("true" or "false") telling whether to use token authentication for a
	 * Mylyn task repository.
	 */
	String PROPERTY_USE_TOKEN = IRepositoryConstants.class.getPackage().getName() + ".REPO_USE_TOKEN"; //$NON-NLS-1$

}
