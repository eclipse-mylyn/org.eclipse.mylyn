/*******************************************************************************
 * Copyright (c) 2004, 2008 Eugene Kuleshov and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
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
