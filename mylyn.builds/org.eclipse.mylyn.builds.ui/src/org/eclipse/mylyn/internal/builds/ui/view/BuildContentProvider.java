/*******************************************************************************
 * Copyright (c) 2010, 2012 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui.view;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
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
			return switch (this) {
				case BY_SERVER -> Messages.BuildContentProvider_servers;
				case BY_PLAN -> Messages.BuildContentProvider_plans;
				default -> throw new IllegalStateException();
			};
		}
	}

	private static final Object[] EMPTY_ARRAY = {};

	private Object input;

	private BuildModel model;

	private final Adapter modelListener = new BuildModelContentAdapter() {
		@Override
		protected void doNotifyChanged(Notification msg) {
			refresh();
		}

		@Override
		protected boolean observing(Notifier notifier) {
			// reduce the number of refreshes by limiting the number of monitored objects: refresh jobs trigger notifications
			// on the server and model objects which is sufficient to monitor plan updates
			return notifier instanceof IBuildServer || notifier instanceof IBuildModel;
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

	@Override
	public void dispose() {
		// ignore
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof IBuildServer) {
			return getPlans(parentElement, model.getPlans((IBuildServer) parentElement)).toArray();
		} else if (parentElement instanceof IBuildPlan) {
			return getPlans(parentElement, ((IBuildPlan) parentElement).getChildren()).toArray();
		}
		return EMPTY_ARRAY;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof IBuildServer) {
			return getPlans(inputElement, model.getPlans((IBuildServer) inputElement)).toArray();
		} else if (inputElement instanceof List<?>) {
			return ((List<?>) inputElement).toArray();
		} else {
			IBuildModel model = getModel(inputElement);
			if (model != null) {
				return switch (presentation) {
					case BY_SERVER -> model.getServers().toArray();
					case BY_PLAN -> model.getPlans().toArray();
				};
			}
		}
		return EMPTY_ARRAY;
	}

	private IBuildModel getModel(Object inputElement) {
		if (inputElement instanceof IBuildModel) {
			return (IBuildModel) inputElement;
		} else if (inputElement == input) {
			return model;
		}
		return null;
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof IBuildServer) {
			return input;
		} else if (element instanceof IBuildPlan plan) {
			if (plan.getParent() != null) {
				return plan.getParent();
			} else {
				return plan.getServer();
			}
		}
		return null;
	}

	private List<IBuildPlan> getPlans(Object parent, List<IBuildPlan> plans) {
		List<IBuildPlan> children = new ArrayList<>(plans.size());
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

	@Override
	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}

	@Override
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
		input = newInput;
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
