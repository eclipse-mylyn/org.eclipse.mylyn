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

package org.eclipse.mylyn.internal.builds.ui.navigator;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.builds.internal.core.BuildModel;
import org.eclipse.mylyn.commons.repositories.RepositoryCategory;
import org.eclipse.mylyn.internal.builds.ui.BuildsUiInternal;
import org.eclipse.mylyn.internal.builds.ui.view.BuildModelContentAdapter;

/**
 * @author Steffen Pingel
 */
public class BuildNavigatorContentProvider implements ITreeContentProvider {

	private static final Object[] EMPTY_ARRAY = new Object[0];

	private BuildModel model;

	private Viewer viewer;

	private final Adapter modelListener = new BuildModelContentAdapter() {
		@Override
		public void doNotifyChanged(Notification msg) {
			if (viewer != null && !viewer.getControl().isDisposed()) {
				viewer.refresh();
			}
		}
	};

	public BuildNavigatorContentProvider() {
	}

	public void dispose() {
		if (model != null) {
			model.eAdapters().remove(modelListener);
		}
	}

	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof RepositoryCategory) {
			RepositoryCategory category = (RepositoryCategory) parentElement;
			if (RepositoryCategory.ID_CATEGORY_BUILDS.equals(category.getId())
					|| RepositoryCategory.ID_CATEGORY_ALL.equals(category.getId())) {
				return model.getServers().toArray();
			}
		}
		return EMPTY_ARRAY;
	}

	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	public Object getParent(Object element) {
		return null;
	}

	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = viewer;
		if (model != null) {
			model.eAdapters().remove(modelListener);
		}
		if (newInput instanceof BuildModel) {
			model = (BuildModel) newInput;
		} else {
			model = BuildsUiInternal.getModel();
		}
		model.eAdapters().add(modelListener);
		//this.input = newInput;
	}

}
