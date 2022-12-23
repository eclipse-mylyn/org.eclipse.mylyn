/*******************************************************************************
 * Copyright (c) 2010, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.discovery.ui;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.operation.IRunnableWithProgress;

/**
 * @author Steffen Pingel
 */
public abstract class AbstractInstallJob implements IRunnableWithProgress {

	public abstract Set<String> getInstalledFeatures(IProgressMonitor monitor);

	public abstract IStatus uninstall(UninstallRequest request, IProgressMonitor progressMonitor)
			throws InvocationTargetException, InterruptedException;

}
