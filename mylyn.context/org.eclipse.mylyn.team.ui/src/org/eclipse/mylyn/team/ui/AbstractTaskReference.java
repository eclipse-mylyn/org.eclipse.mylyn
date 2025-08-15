/*******************************************************************************
 * Copyright (c) 2004, 2008 Eugene Kuleshov and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Eugene Kuleshov - initial API and implementation
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.team.ui;

/**
 * @author Eugene Kuleshov
 * @author Mik Kersten
 * @since 3.0
 */
public abstract class AbstractTaskReference {

	public abstract String getTaskId();

	public abstract String getTaskUrl();

	public abstract String getRepositoryUrl();

	public abstract String getText();

}
