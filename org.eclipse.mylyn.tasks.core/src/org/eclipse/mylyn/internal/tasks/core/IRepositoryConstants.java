/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core;

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

	public static final String CATEGORY_TASKS = "org.eclipse.mylyn.category.tasks"; //$NON-NLS-1$

	public static final String CATEGORY_BUGS = "org.eclipse.mylyn.category.bugs"; //$NON-NLS-1$

	public static final String CATEGORY_BUILD = "org.eclipse.mylyn.category.build"; //$NON-NLS-1$

	public static final String CATEGORY_REVIEW = "org.eclipse.mylyn.category.review"; //$NON-NLS-1$

	public static final String CATEGORY_OTHER = "org.eclipse.mylyn.category.other"; //$NON-NLS-1$

}
