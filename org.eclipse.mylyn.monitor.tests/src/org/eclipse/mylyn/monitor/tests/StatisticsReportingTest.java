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

import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.mylar.context.core.ContextCorePlugin;
import org.eclipse.mylar.context.ui.ContextUiPlugin;
import org.eclipse.mylar.internal.java.MylarJavaPlugin;
import org.eclipse.mylar.internal.java.ui.actions.FocusPackageExplorerAction;
import org.eclipse.mylar.internal.monitor.core.collection.IUsageCollector;
import org.eclipse.mylar.internal.monitor.reports.collectors.MylarUsageAnalysisCollector;
import org.eclipse.mylar.internal.monitor.reports.collectors.MylarViewUsageCollector;
import org.eclipse.mylar.internal.monitor.usage.InteractionEventLogger;
import org.eclipse.mylar.internal.monitor.usage.MylarUsageMonitorPlugin;
import org.eclipse.mylar.internal.tasks.ui.actions.TaskActivateAction;
import org.eclipse.mylar.monitor.core.InteractionEvent;
import org.eclipse.mylar.monitor.ui.MonitorUiPlugin;
import org.eclipse.mylar.monitor.usage.ReportGenerator;
import org.eclipse.mylar.monitor.usage.UsageStatisticsSummary;

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
		assertNotNull(ContextCorePlugin.getDefault());
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
		report.forceSyncForTesting(true);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	protected InteractionEvent mockExplorerSelection(String handle) {
		InteractionEvent event = new InteractionEvent(InteractionEvent.Kind.SELECTION, "java", handle,
				JavaUI.ID_PACKAGES);
		MonitorUiPlugin.getDefault().notifyInteractionObserved(event);
		return event;
	}

	protected void mockEdit(String handle) {
		MonitorUiPlugin.getDefault().notifyInteractionObserved(
				new InteractionEvent(InteractionEvent.Kind.EDIT, "java", handle, JavaUI.ID_PACKAGES));
	}

	protected void mockTypesSelection(String handle) {
		MonitorUiPlugin.getDefault().notifyInteractionObserved(
				new InteractionEvent(InteractionEvent.Kind.SELECTION, "java", handle, JavaUI.ID_TYPES_VIEW));
	}

	public void testEditRatio() throws InvocationTargetException, InterruptedException {
		logger.stopMonitoring();
		PackageExplorerPart part = PackageExplorerPart.openInActivePerspective();
		assertNotNull(part.getTreeViewer());
		part.setFocus();

		logger.startMonitoring();
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

		MonitorUiPlugin.getDefault().notifyInteractionObserved(
				InteractionEvent.makeCommand(TaskActivateAction.ID, ""));

		mockExplorerSelection("A.java");
		mockEdit("A.java");
		mockUserDelay();
		mockEdit("A.java");

		logger.stopMonitoring();
		report.getStatisticsFromInteractionHistory(logger.getOutputFile(), null);

		// TODO: these are off from expected when test run alone, due to unknown
		// element selections
		assertEquals(0.5f, editRatioCollector.getBaselineRatio(-1));
		assertEquals(2f, editRatioCollector.getMylarRatio(-1));
	}

	@SuppressWarnings("unused")
	public void testSimpleSelection() {
		mockExplorerSelection("A.java");
		report.getStatisticsFromInteractionHistory(logger.getOutputFile(), new JobChangeAdapter() {
			public void done() {
				UsageStatisticsSummary summary = report.getLastParsedSummary();
				assertTrue(summary.getSingleSummaries().size() > 0);
			}
		});

	}

	@SuppressWarnings("unused")
	public void testFilteredModeDetection() throws IOException {
		MylarUsageMonitorPlugin.getDefault().addMonitoredPreferences(
				ContextUiPlugin.getDefault().getPluginPreferences());

		MylarUsageMonitorPlugin.getDefault().getInteractionLogger().clearInteractionHistory();
		mockExplorerSelection("A.java");
		mockUserDelay();
		mockExplorerSelection("A.java");
		mockUserDelay();
		mockTypesSelection("A.java");

		assertNotNull(ContextUiPlugin.getDefault().getPreferenceStore());
		String prefId = FocusPackageExplorerAction.PREF_ID_PREFIX + JavaUI.ID_PACKAGES;
		assertNotNull(prefId);

		PackageExplorerPart part = PackageExplorerPart.openInActivePerspective();
		assertNotNull(part);
		// AbstractFocusViewAction action =
		// FocusPackageExplorerAction.getActionForPart(part);
		// assertNotNull(action);

		ContextUiPlugin.getDefault().getPreferenceStore().setValue(prefId, true);

		mockExplorerSelection("A.java");
		mockUserDelay();
		mockExplorerSelection("A.java");
		mockUserDelay();
		mockTypesSelection("A.java");

		ContextUiPlugin.getDefault().getPreferenceStore().setValue(prefId, false);

		mockExplorerSelection("A.java");

		logger.stopMonitoring();
		report.getStatisticsFromInteractionHistory(logger.getOutputFile(), new JobChangeAdapter() {

			public void done() {
				int normal = viewCollector.getNormalViewSelections().get(JavaUI.ID_PACKAGES);
				assertEquals(5, normal);

				int filtered = viewCollector.getFilteredViewSelections().get(JavaUI.ID_PACKAGES);
				assertEquals(2, filtered);

				MylarUsageMonitorPlugin.getDefault().removeMonitoredPreferences(
						ContextUiPlugin.getDefault().getPluginPreferences());
			}
		});

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
