/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.reviews.ui.compare;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.commons.io.FilenameUtils;
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

	public String getName() {
		return name;
	}

	public Image getImage() {
		return null;
	}

	public String getType() {
		String extension = FilenameUtils.getExtension(name);
		return extension != null && extension.length() > 0 ? extension : ITypedElement.TEXT_TYPE;
	}

	public InputStream getContents() throws CoreException {
		return new ByteArrayInputStream(content);
	}

}