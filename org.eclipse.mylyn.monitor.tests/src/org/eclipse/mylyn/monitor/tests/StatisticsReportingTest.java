/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.monitor.tests;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.mylar.context.core.InteractionEvent;
import org.eclipse.mylar.context.core.MylarPlugin;
import org.eclipse.mylar.context.ui.MylarUiPlugin;
import org.eclipse.mylar.internal.java.MylarJavaPlugin;
import org.eclipse.mylar.internal.java.ui.actions.ApplyMylarToPackageExplorerAction;
import org.eclipse.mylar.internal.monitor.reports.ReportGenerator;
import org.eclipse.mylar.internal.monitor.reports.collectors.MylarUsageAnalysisCollector;
import org.eclipse.mylar.internal.monitor.reports.collectors.MylarViewUsageCollector;
import org.eclipse.mylar.internal.monitor.reports.ui.views.UsageStatisticsSummary;
import org.eclipse.mylar.internal.monitor.usage.InteractionEventLogger;
import org.eclipse.mylar.internal.tasklist.ui.actions.TaskActivateAction;
import org.eclipse.mylar.monitor.MylarMonitorPlugin;
import org.eclipse.mylar.monitor.reports.IUsageCollector;
import org.eclipse.mylar.monitor.usage.MylarUsageMonitorPlugin;

/**
 * @author Mik Kersten
 */
public class StatisticsReportingTest extends TestCase {

	private InteractionEventLogger logger;

	private MylarViewUsageCollector viewCollector = new MylarViewUsageCollector();

	private MylarUsageAnalysisCollector editRatioCollector = new MylarUsageAnalysisCollector();;

	private ReportGenerator report;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		assertNotNull(MylarPlugin.getDefault());
		assertNotNull(MylarJavaPlugin.getDefault());
		assertNotNull(PackageExplorerPart.openInActivePerspective());

		MylarUsageMonitorPlugin.getDefault().startMonitoring();
		assertTrue(MylarUsageMonitorPlugin.getDefault().isMonitoringEnabled());
		logger = MylarUsageMonitorPlugin.getDefault().getInteractionLogger();
		logger.clearInteractionHistory();

		List<IUsageCollector> collectors = new ArrayList<IUsageCollector>();
		collectors.add(viewCollector);
		collectors.add(editRatioCollector);
		report = new ReportGenerator(logger, collectors);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	protected InteractionEvent mockExplorerSelection(String handle) {
		InteractionEvent event = new InteractionEvent(InteractionEvent.Kind.SELECTION, "java", handle,
				JavaUI.ID_PACKAGES);
		MylarMonitorPlugin.getDefault().notifyInteractionObserved(event);
		return event;
	}

	protected void mockEdit(String handle) {
		MylarMonitorPlugin.getDefault().notifyInteractionObserved(
				new InteractionEvent(InteractionEvent.Kind.EDIT, "java", handle, JavaUI.ID_PACKAGES));
	}

	protected void mockTypesSelection(String handle) {
		MylarMonitorPlugin.getDefault().notifyInteractionObserved(
				new InteractionEvent(InteractionEvent.Kind.SELECTION, "java", handle, JavaUI.ID_TYPES_VIEW));
	}

	public void testEditRatio() throws InvocationTargetException, InterruptedException {
		logger.stopObserving();
		PackageExplorerPart part = PackageExplorerPart.openInActivePerspective();
		assertNotNull(part.getTreeViewer());
		part.setFocus();

		logger.startObserving();
		final InteractionEvent first = mockExplorerSelection("A.java");
		mockUserDelay();
		mockUserDelay();
		final InteractionEvent second = mockExplorerSelection("A.java");

		assertTrue(!first.getDate().equals(second.getDelta()));

		try {
			// XXX: this is a hack and sensitive to CPU speeds
			Thread.sleep(3000);
		} catch (InterruptedException ie) {
			fail();
		}

		mockEdit("A.java");

		MylarMonitorPlugin.getDefault().notifyInteractionObserved(InteractionEvent.makeCommand(TaskActivateAction.ID, ""));

		mockExplorerSelection("A.java");
		mockEdit("A.java");
		mockUserDelay();
		mockEdit("A.java");

		logger.stopObserving();
		report.getStatisticsFromInteractionHistory(logger.getOutputFile());

		// TODO: these are off from expected when test run alone, due to unknown
		// element selections
		assertEquals(0.5f, editRatioCollector.getBaselineRatio(-1));
		assertEquals(2f, editRatioCollector.getMylarRatio(-1));
	}

	public void testSimpleSelection() {
		mockExplorerSelection("A.java");
		UsageStatisticsSummary summary = report.getStatisticsFromInteractionHistory(logger.getOutputFile());
		assertTrue(summary.getSingleSummaries().size() > 0);
	}

	public void testFilteredModeDetection() throws IOException {
		MylarUsageMonitorPlugin.getDefault().getInteractionLogger().clearInteractionHistory();
		mockExplorerSelection("A.java");
		mockUserDelay();
		mockExplorerSelection("A.java");
		mockUserDelay();
		mockTypesSelection("A.java");

		assertNotNull(MylarUiPlugin.getDefault().getPreferenceStore());
		String prefId = ApplyMylarToPackageExplorerAction.PREF_ID_PREFIX + PackageExplorerPart.VIEW_ID;
		assertNotNull(prefId);
		MylarUiPlugin.getDefault().getPreferenceStore().setValue(prefId, true);

		mockExplorerSelection("A.java");
		mockUserDelay();
		mockExplorerSelection("A.java");
		mockUserDelay();
		mockTypesSelection("A.java");

		MylarUiPlugin.getDefault().getPreferenceStore().setValue(prefId, false);

		mockExplorerSelection("A.java");

		logger.stopObserving();
		report.getStatisticsFromInteractionHistory(logger.getOutputFile());

		int normal = viewCollector.getNormalViewSelections().get(JavaUI.ID_PACKAGES);
		int filtered = viewCollector.getFilteredViewSelections().get(JavaUI.ID_PACKAGES);

		assertEquals(5, normal);
		assertEquals(2, filtered);
	}

	/**
	 * Delay enough to make replicated events different
	 */
	private void mockUserDelay() {
		// TODO: Refactor into mylar.core.tests
		try {
			// XXX: this could be sensitive to CPU speeds
			Thread.sleep(100);
		} catch (InterruptedException ie) {
			fail();
		}
	}
}
