/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.core.storage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author Steffen Pingel
 */
public interface ICommonStorable {

	void delete(String handle) throws CoreException;

	void deleteAll() throws CoreException;

	boolean exists(String handle);

	InputStream read(String handle, IProgressMonitor monitor) throws IOException, CoreException;

	void release();

	OutputStream write(String handle, IProgressMonitor monitor) throws IOException, CoreException;

}
