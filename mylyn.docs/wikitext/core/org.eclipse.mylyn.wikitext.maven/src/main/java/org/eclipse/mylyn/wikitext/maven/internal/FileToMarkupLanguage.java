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
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.maven.internal;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.mylyn.wikitext.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.util.ServiceLocator;

public class FileToMarkupLanguage {

	private final Map<String, MarkupLanguage> extensionToMarkupLanguage;

	public FileToMarkupLanguage() {
		this(ServiceLocator.getInstance(FileToMarkupLanguage.class.getClassLoader()).getAllMarkupLanguages());
	}

	public FileToMarkupLanguage(Set<MarkupLanguage> markupLanguages) {
		extensionToMarkupLanguage = computeExtensionToMarkupLanguage(requireNonNull(markupLanguages));
	}

	public MarkupLanguage get(File file) {
		requireNonNull(file);
		String extension = computeFileExtension(file);
		return extensionToMarkupLanguage.get(extension.toLowerCase());
	}

	Map<String, MarkupLanguage> computeExtensionToMarkupLanguage(Set<MarkupLanguage> markupLanguages) {
		Map<String, MarkupLanguage> extensionToMarkupLanguage = new HashMap<>();
		for (MarkupLanguage language : markupLanguages) {
			for (String extension : language.getFileExtensions()) {
				extensionToMarkupLanguage.put(extension.toLowerCase(), language);
			}
		}
		return extensionToMarkupLanguage;
	}

	String computeFileExtension(File file) {
		String name = file.getName();
		int indexOfDot = name.lastIndexOf('.');
		if (indexOfDot >= 0 && indexOfDot < name.length() - 1) {
			return name.substring(indexOfDot + 1);
		}
		return ""; //$NON-NLS-1$
	}
}
