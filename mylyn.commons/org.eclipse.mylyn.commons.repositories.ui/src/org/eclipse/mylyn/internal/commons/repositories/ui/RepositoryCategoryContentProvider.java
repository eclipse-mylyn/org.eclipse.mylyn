/*******************************************************************************
 * Copyright (c) 2010, 2013 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.repositories.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.commons.core.ExtensionPointReader;
import org.eclipse.mylyn.commons.repositories.core.RepositoryCategory;

public class RepositoryCategoryContentProvider implements ITreeContentProvider {

	private static final Map<String, RepositoryCategory> repositoryCategories = new HashMap<>();

	public RepositoryCategoryContentProvider() {
		InternalExtensionPointReader.initExtensions();
	}

	@Override
	public void dispose() {
		// ignore
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// ignore
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return repositoryCategories.values().toArray();
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		// ignore
		return null;
	}

	@Override
	public Object getParent(Object element) {
		// ignore
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		// ignore
		return false;
	}

	private static class InternalExtensionPointReader {
		public static final String EXTENSION_CUSTOM_CATEGORY = "categories"; //$NON-NLS-1$

		public static final String EXTENSION_TMPL_REPOSITORY_CATEGORY = "category"; //$NON-NLS-1$

		private static final String ELEMENT_REPOSITORY_CATEGORY_ID = "id"; //$NON-NLS-1$

		private static final String ELEMENT_REPOSITORY_CATEGORY_LABEL = "label"; //$NON-NLS-1$

		private static final String ELEMENT_REPOSITORY_CATEGORY_RANK = "rank"; //$NON-NLS-1$

		private static void initExtensions() {
			ExtensionPointReader<RepositoryCategory> reader = new ExtensionPointReader<>(
					RepositoriesUiPlugin.ID_PLUGIN, EXTENSION_CUSTOM_CATEGORY, EXTENSION_TMPL_REPOSITORY_CATEGORY,
					RepositoryCategory.class) {
				@Override
				protected RepositoryCategory readElement(IConfigurationElement element,
						org.eclipse.core.runtime.MultiStatus result) {
					return readRepositoryCategory(element);
				}
			};
			reader.read();
			List<RepositoryCategory> categories = reader.getItems();
			for (RepositoryCategory cat : categories) {
				repositoryCategories.put(cat.getId(), cat);
			}
		}

		private static RepositoryCategory readRepositoryCategory(IConfigurationElement element) {
			String id = element.getAttribute(ELEMENT_REPOSITORY_CATEGORY_ID);
			String label = element.getAttribute(ELEMENT_REPOSITORY_CATEGORY_LABEL);
			String rank = element.getAttribute(ELEMENT_REPOSITORY_CATEGORY_RANK);
			int rankInt;
			try {
				rankInt = Integer.parseInt(rank);
			} catch (NumberFormatException e) {
				rankInt = 0;
			}
			return new RepositoryCategory(id, label, rankInt);
		}
	}
}
