/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.core.maven;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;

public class SourceFileTraversal {

	public interface Visitor {
		public void accept(String relativePath, File sourceFile);
	}

	private final File root;

	public SourceFileTraversal(File root) {
		this.root = checkNotNull(root);
		checkArgument(root.exists(), "Root folder must exist");
		checkArgument(root.isDirectory(), "Root folder must be a folder");
	}

	public void traverse(Visitor visitor) {
		traverse("", root, visitor);
	}

	void traverse(String relativePath, File file, Visitor visitor) {
		if (file.isFile()) {
			visitor.accept(relativePath, file);
		} else if (file.isDirectory()) {
			File[] children = file.listFiles();
			if (children != null) {
				String childRelativePath = file.equals(root) ? "" : file.getName();
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
