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

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.core.util.ServiceLocator;

public class FileToMarkupLanguage {

	private final Map<String, MarkupLanguage> extensionToMarkupLanguage;

	public FileToMarkupLanguage() {
		this(ServiceLocator.getInstance(FileToMarkupLanguage.class.getClassLoader()).getAllMarkupLanguages());
	}

	public FileToMarkupLanguage(Set<MarkupLanguage> markupLanguages) {
		this.extensionToMarkupLanguage = computeExtensionToMarkupLanguage(checkNotNull(markupLanguages));
	}

	public MarkupLanguage get(File file) {
		checkNotNull(file);
		String extension = computeFileExtension(file);
		return extensionToMarkupLanguage.get(extension.toLowerCase());
	}

	Map<String, MarkupLanguage> computeExtensionToMarkupLanguage(Set<MarkupLanguage> markupLanguages) {
		Map<String, MarkupLanguage> extensionToMarkupLanguage = new HashMap<String, MarkupLanguage>();
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
		if (indexOfDot >= 0 && indexOfDot < (name.length() - 1)) {
			return name.substring(indexOfDot + 1);
		}
		return "";
	}
}
