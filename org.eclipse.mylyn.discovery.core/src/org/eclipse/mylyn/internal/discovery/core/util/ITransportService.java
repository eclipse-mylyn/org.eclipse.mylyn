/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.discovery.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

/**
 * @author Steffen Pingel
 */
public interface ITransportService {

	public abstract IStatus download(URI uri, OutputStream out, IProgressMonitor monitor);

	public abstract InputStream stream(URI uri, IProgressMonitor monitor) throws IOException, CoreException;

	public abstract long getLastModified(URI location, IProgressMonitor monitor) throws CoreException, IOException;

}
