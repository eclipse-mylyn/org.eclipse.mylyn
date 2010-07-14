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

package org.eclipse.mylyn.internal.builds.ui.view;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.builds.core.IBuildModel;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.internal.builds.core.BuildModel;
import org.eclipse.mylyn.internal.builds.ui.BuildsUiInternal;

/**
 * @author Steffen Pingel
 */
public class BuildContentProvider implements ITreeContentProvider {

	private static final Object[] EMPTY_ARRAY = new Object[0];

	private Object input;

	private boolean nestPlansEnabled;

	private boolean selectedOnly;

	private BuildModel model;

	private Viewer viewer;

	private final Adapter modelListener = new EContentAdapter() {
		@Override
		public void notifyChanged(Notification msg) {
//			Display.getDefault().asyncExec(new Runnable() {
//				public void run() {
			System.err.println(msg);
			if (viewer != null && !viewer.getControl().isDisposed()) {
				viewer.refresh();
			}
//				}
//			});
		}
	};

	public BuildContentProvider() {
		setNestPlansEnabled(true);
	}

	public void dispose() {
		// ignore
	}

	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof IBuildServer) {
			return getPlans(parentElement, ((IBuildServer) parentElement).getPlans()).toArray();
		} else if (parentElement instanceof IBuildPlan) {
			return getPlans(parentElement, ((IBuildPlan) parentElement).getChildren()).toArray();
		}
		return EMPTY_ARRAY;
	}

	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof IBuildModel) {
			return ((IBuildModel) inputElement).getServers().toArray();
		} else if (inputElement instanceof IBuildServer) {
			return getPlans(inputElement, ((IBuildServer) inputElement).getPlans()).toArray();
		} else if (inputElement == input) {
			return model.getServers().toArray();
		}
		if (inputElement instanceof List<?>) {
			return ((List<?>) inputElement).toArray();
		}
		return EMPTY_ARRAY;
	}

	public Object getParent(Object element) {
		if (element instanceof IBuildServer) {
			return input;
		} else if (element instanceof IBuildPlan) {
			IBuildPlan plan = (IBuildPlan) element;
			if (plan.getParent() != null) {
				return plan.getParent();
			} else {
				return plan.getServer();
			}
		}
		return null;
	}

	private List<IBuildPlan> getPlans(Object parent, List<IBuildPlan> plans) {
		List<IBuildPlan> children = new ArrayList<IBuildPlan>(plans.size());
		for (IBuildPlan plan : plans) {
			if (isSelectedOnly() && !plan.isSelected()) {
				continue;
			}
			if (isNestPlansEnabled() && plan.getParent() != null && plan.getParent() != parent) {
				continue;
			}
			children.add(plan);
		}
		return children;
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
		this.input = newInput;
	}

	public final boolean isNestPlansEnabled() {
		return nestPlansEnabled;
	}

	public final boolean isSelectedOnly() {
		return selectedOnly;
	}

	public final void setNestPlansEnabled(boolean nestPlansEnabled) {
		this.nestPlansEnabled = nestPlansEnabled;
	}

	public final void setSelectedOnly(boolean selectedOnly) {
		this.selectedOnly = selectedOnly;
	}

}
