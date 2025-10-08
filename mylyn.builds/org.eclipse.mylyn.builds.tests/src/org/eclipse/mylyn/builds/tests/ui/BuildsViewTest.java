/*******************************************************************************
 * Copyright (c) 2014 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.builds.tests.ui;

import static org.mockito.Mockito.when;

import org.eclipse.mylyn.builds.core.BuildStatus;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.internal.core.BuildFactory;
import org.eclipse.mylyn.builds.internal.core.BuildModel;
import org.eclipse.mylyn.internal.builds.ui.BuildsUiInternal;
import org.eclipse.mylyn.internal.builds.ui.view.BuildContentProvider;
import org.eclipse.mylyn.internal.builds.ui.view.BuildsView;
import org.eclipse.mylyn.internal.builds.ui.view.BuildsView.BuildsSummary;
import org.mockito.Mockito;

import junit.framework.TestCase;

@SuppressWarnings("nls")
public class BuildsViewTest extends TestCase {
	public class TestBuildsView extends BuildsView {
		@Override
		protected BuildsSummary getBuildsSummary() {
			return super.getBuildsSummary();
		}

		@Override
		protected BuildModel getModel() {
			return super.getModel();
		}
	}

	public void testGetBuildsSummary() throws Exception {
		TestBuildsView view = Mockito.spy(new TestBuildsView());
		when(view.getContentProvider()).thenReturn(new BuildContentProvider());
		when(view.getModel()).thenReturn(BuildsUiInternal.getModel());

		assertBuildSummary(view.getBuildsSummary(), false, false, false);
		assertTrue(view.getBuildsSummary().isEmpty());

		view.getModel().getPlans().add(createBuildPlan(BuildStatus.SUCCESS));
		view.getModel().getPlans().add(createBuildPlan(BuildStatus.SUCCESS));
		view.getModel().getPlans().add(createBuildPlan(BuildStatus.SUCCESS));
		assertBuildSummary(view.getBuildsSummary(), true, false, false);

		view.getModel().getPlans().add(createBuildPlan(BuildStatus.UNSTABLE));
		view.getModel().getPlans().add(createBuildPlan(BuildStatus.UNSTABLE));
		assertBuildSummary(view.getBuildsSummary(), false, true, false);

		view.getModel().getPlans().add(createBuildPlan(BuildStatus.FAILED));
		assertBuildSummary(view.getBuildsSummary(), false, false, true);

		assertFalse(view.getBuildsSummary().isEmpty());
		assertEquals("3 Succeeded, 2 Unstable, 1 Failed", view.getBuildsSummary().toString());
	}

	private void assertBuildSummary(BuildsSummary buildsSummary, boolean isSuccess, boolean isUnstable,
			boolean isFailed) {
		assertEquals(isSuccess, buildsSummary.isSuccess());
		assertEquals(isUnstable, buildsSummary.isUnstable());
		assertEquals(isFailed, buildsSummary.isFailed());
	}

	private IBuildPlan createBuildPlan(BuildStatus status) {
		IBuildPlan plan = BuildFactory.eINSTANCE.createBuildPlan();
		plan.setStatus(status);
		return plan;
	}
}
