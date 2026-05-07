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
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.internal.reviews.ui.compare;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.compare.IStreamContentAccessor;
import org.eclipse.compare.ITypedElement;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.graphics.Image;

/**
 * @author Steffen Pingel
 */
class ByteArrayInput implements ITypedElement, IStreamContentAccessor {

	byte[] content;

	private final String name;

	public ByteArrayInput(byte[] content, String name) {
		this.content = content;
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Image getImage() {
		return null;
	}

	@Override
	public String getType() {
		String extension = name.contains(".") ? name.substring(name.lastIndexOf('.') + 1) : ""; //$NON-NLS-1$ //$NON-NLS-2$
		return !extension.isEmpty() ? extension : ITypedElement.TEXT_TYPE;
	}

	@Override
	public InputStream getContents() throws CoreException {
		return new ByteArrayInputStream(content);
	}

}