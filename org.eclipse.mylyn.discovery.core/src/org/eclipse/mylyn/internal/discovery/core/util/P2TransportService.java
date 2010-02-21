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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

/**
 * @author Steffen Pingel
 */
public class P2TransportService implements ITransportService {

	private Object transport;

	private Method downloadMethod;

	private Method streamMethod;

	private Method getLastModifiedMethod;

	public P2TransportService() throws ClassNotFoundException {
		try {
			Class<?> clazz = Class.forName("org.eclipse.equinox.internal.p2.repository.RepositoryTransport"); //$NON-NLS-1$
			Method getInstanceMethod = clazz.getDeclaredMethod("getInstance"); //$NON-NLS-1$
			transport = getInstanceMethod.invoke(null);
			downloadMethod = clazz.getDeclaredMethod("download", URI.class, OutputStream.class, IProgressMonitor.class); //$NON-NLS-1$
			streamMethod = clazz.getDeclaredMethod("stream", URI.class, IProgressMonitor.class); //$NON-NLS-1$
			getLastModifiedMethod = clazz.getDeclaredMethod("getLastModified", URI.class, IProgressMonitor.class); //$NON-NLS-1$
		} catch (LinkageError e) {
			throw new ClassNotFoundException("Failed to load P2 transport", e); //$NON-NLS-1$
		} catch (Exception e) {
			throw new ClassNotFoundException("Failed to load P2 transport", e); //$NON-NLS-1$
		}
	}

	public IStatus download(URI uri, OutputStream out, IProgressMonitor monitor) {
		try {
			return (IStatus) downloadMethod.invoke(transport, uri, out, monitor);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public InputStream stream(URI uri, IProgressMonitor monitor) throws IOException, CoreException {
		try {
			return (InputStream) streamMethod.invoke(transport, uri, monitor);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			if (e.getCause() instanceof CoreException) {
				throw (CoreException) e.getCause();
			} else if (e.getCause() instanceof IOException) {
				throw (IOException) e.getCause();
			}
			throw new RuntimeException(e);
		}
	}

	public long getLastModified(URI location, IProgressMonitor monitor) throws CoreException, IOException {
		try {
			return (Long) getLastModifiedMethod.invoke(transport, location, monitor);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			if (e.getCause() instanceof CoreException) {
				throw (CoreException) e.getCause();
			} else if (e.getCause() instanceof IOException) {
				throw (IOException) e.getCause();
			}
			throw new RuntimeException(e);
		}
	}

}
