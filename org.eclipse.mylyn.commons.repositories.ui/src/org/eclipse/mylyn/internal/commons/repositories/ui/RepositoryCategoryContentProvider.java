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

package org.eclipse.mylyn.internal.commons.repositories.ui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.commons.repositories.core.RepositoryCategory;

public class RepositoryCategoryContentProvider implements ITreeContentProvider {

	private static final Map<String, RepositoryCategory> repositoryCategories = new HashMap<String, RepositoryCategory>();

	public RepositoryCategoryContentProvider() {
		ExtensionPointReader.initExtensions();
	}

	public void dispose() {
		// ignore

	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// ignore

	}

	public Object[] getElements(Object inputElement) {
		return repositoryCategories.values().toArray();
	}

	public Object[] getChildren(Object parentElement) {
		// ignore
		return null;
	}

	public Object getParent(Object element) {
		// ignore
		return null;
	}

	public boolean hasChildren(Object element) {
		// ignore
		return false;
	}

	static class ExtensionPointReader {
		public static final String EXTENSION_CUSTOM_CATEGORY = "org.eclipse.mylyn.commons.repositories.ui.categories"; //$NON-NLS-1$

		public static final String EXTENSION_TMPL_REPOSITORY_CATEGORY = "category"; //$NON-NLS-1$

		private static final String ELEMENT_REPOSITORY_CATEGORY_ID = "id"; //$NON-NLS-1$

		private static final String ELEMENT_REPOSITORY_CATEGORY_LABEL = "label"; //$NON-NLS-1$

		private static final String ELEMENT_REPOSITORY_CATEGORY_RANK = "rank"; //$NON-NLS-1$

		public static void initExtensions() {
			IExtensionRegistry registry = Platform.getExtensionRegistry();

			IExtensionPoint templatesExtensionPoint = registry.getExtensionPoint(EXTENSION_CUSTOM_CATEGORY);
			IExtension[] templateExtensions = templatesExtensionPoint.getExtensions();
			for (IExtension templateExtension : templateExtensions) {
				IConfigurationElement[] elements = templateExtension.getConfigurationElements();
				for (IConfigurationElement element : elements) {
					if (element.getName().equals(EXTENSION_TMPL_REPOSITORY_CATEGORY)) {
						readRepositoryCategory(element);
					}
				}
			}

		}

		private static void readRepositoryCategory(IConfigurationElement element) {
			String id = element.getAttribute(ELEMENT_REPOSITORY_CATEGORY_ID);
			String label = element.getAttribute(ELEMENT_REPOSITORY_CATEGORY_LABEL);
			String rank = element.getAttribute(ELEMENT_REPOSITORY_CATEGORY_RANK);
			int rankInt;
			try {
				rankInt = Integer.parseInt(rank);

			} catch (NumberFormatException e) {
				rankInt = 0;
			}
			RepositoryCategory cat = new RepositoryCategory(id, label, rankInt);
			repositoryCategories.put(cat.getId(), cat);
		}
	}
}
