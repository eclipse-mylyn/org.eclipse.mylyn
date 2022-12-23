/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
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

package org.eclipse.mylyn.tasks.core.data;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.internal.tasks.core.data.TextTaskAttachmentSource;
import org.junit.Test;

/**
 * @author Steffen Pingel
 */
public class TaskAttachmentPartSourceTest {

	@Test
	public void testCreateInputStream_Exception() {
		final CoreException exception = new CoreException(Status.OK_STATUS);
		AbstractTaskAttachmentSource source = new TextTaskAttachmentSource("content") {
			@Override
			public InputStream createInputStream(IProgressMonitor monitor) throws CoreException {
				throw exception;
			}
		};
		TaskAttachmentPartSource partSource = new TaskAttachmentPartSource(source, "filename");
		try {
			partSource.createInputStream();
			fail("Expected IOException");
		} catch (IOException e) {
			assertSame(exception, e.getCause());
		}
	}

}
