/*******************************************************************************
 * Copyright (c) 2013, 2024 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     ArSysOp - ongoing support
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.maven.internal;

import static java.util.Objects.requireNonNull;

import java.io.File;

import org.apache.commons.lang3.Validate;

public class SourceFileTraversal {

	public interface Visitor {
		public void accept(String relativePath, File sourceFile);
	}

	private final File root;

	public SourceFileTraversal(File root) {
		this.root = requireNonNull(root);
		Validate.isTrue(root.exists(), "Root folder must exist"); //$NON-NLS-1$
		Validate.isTrue(root.isDirectory(), "Root folder must be a folder"); //$NON-NLS-1$
	}

	public void traverse(Visitor visitor) {
		traverse("", root, visitor); //$NON-NLS-1$
	}

	void traverse(String relativePath, File file, Visitor visitor) {
		if (file.isFile()) {
			visitor.accept(relativePath, file);
		} else if (file.isDirectory()) {
			File[] children = file.listFiles();
			if (children != null) {
				String childRelativePath = file.equals(root) ? "" : file.getName(); //$NON-NLS-1$
				if (relativePath.length() > 0) {
					childRelativePath = relativePath + File.separator + childRelativePath;
				}
				for (File child : children) {
					traverse(childRelativePath, child, visitor);
				}
			}
		}
	}
}
