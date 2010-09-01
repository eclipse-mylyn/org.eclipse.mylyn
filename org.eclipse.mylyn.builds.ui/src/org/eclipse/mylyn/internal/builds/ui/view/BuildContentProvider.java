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

import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.builds.core.IBuildModel;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.builds.internal.core.BuildModel;
import org.eclipse.mylyn.internal.builds.ui.BuildsUiInternal;

/**
 * @author Steffen Pingel
 */
public class BuildContentProvider implements ITreeContentProvider {

	public enum Presentation {
		BY_SERVER, BY_PLAN;

		@Override
		public String toString() {
			switch (this) {
			case BY_SERVER:
				return "Servers";
			case BY_PLAN:
				return "Plans";
			}
			throw new IllegalStateException();
		};
	};

	private static final Object[] EMPTY_ARRAY = new Object[0];

	private Object input;

	private BuildModel model;

	private final Adapter modelListener = new EContentAdapter() {

		@Override
		public void notifyChanged(Notification msg) {
			super.notifyChanged(msg);
			if (msg.getOldValue() != msg.getNewValue()) {
				refresh();
			}
		}

		@Override
		protected void addAdapter(Notifier notifier) {
			if (observing(notifier)) {
				super.addAdapter(notifier);
			}
		}

		protected boolean observing(Notifier notifier) {
			return notifier instanceof IBuildServer || notifier instanceof IBuildPlan
					|| notifier instanceof IBuildModel;
		}

		@Override
		protected void removeAdapter(Notifier notifier) {
			if (observing(notifier)) {
				notifier.eAdapters().remove(this);
			}
		}

	};

	private boolean nestPlansEnabled;

	private Presentation presentation;

	private boolean selectedOnly;

	private Viewer viewer;

	public BuildContentProvider() {
		setNestPlansEnabled(true);
		setPresentation(Presentation.BY_SERVER);
	}

	public void dispose() {
		// ignore
	}

	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof IBuildServer) {
			return getPlans(parentElement, model.getPlans(((IBuildServer) parentElement))).toArray();
		} else if (parentElement instanceof IBuildPlan) {
			return getPlans(parentElement, ((IBuildPlan) parentElement).getChildren()).toArray();
		}
		return EMPTY_ARRAY;
	}

	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof IBuildServer) {
			return getPlans(inputElement, model.getPlans(((IBuildServer) inputElement))).toArray();
		} else if (inputElement instanceof List<?>) {
			return ((List<?>) inputElement).toArray();
		} else {
			IBuildModel model = getModel(inputElement);
			if (model != null) {
				switch (presentation) {
				case BY_SERVER:
					return model.getServers().toArray();
				case BY_PLAN:
					return model.getPlans().toArray();
				}
			}
		}
		return EMPTY_ARRAY;
	}

	private IBuildModel getModel(Object inputElement) {
		if (inputElement instanceof IBuildModel) {
			return ((IBuildModel) inputElement);
		} else if (inputElement == input) {
			return model;
		}
		return null;
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

	public Presentation getPresentation() {
		return presentation;
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

	public void setPresentation(Presentation presentation) {
		Assert.isNotNull(presentation);
		this.presentation = presentation;
		refresh();
	}

	public final void setSelectedOnly(boolean selectedOnly) {
		this.selectedOnly = selectedOnly;
	}

	protected void refresh() {
		if (viewer != null && !viewer.getControl().isDisposed()) {
			viewer.refresh();
		}
	}

}
