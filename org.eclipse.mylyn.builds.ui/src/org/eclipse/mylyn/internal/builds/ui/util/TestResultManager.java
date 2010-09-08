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

package org.eclipse.mylyn.internal.builds.ui.util;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.TypeNameMatch;
import org.eclipse.jdt.core.search.TypeNameMatchRequestor;
import org.eclipse.jdt.internal.junit.model.JUnitModel;
import org.eclipse.jdt.internal.junit.model.TestRunHandler;
import org.eclipse.jdt.internal.junit.model.TestRunSession;
import org.eclipse.jdt.internal.junit.ui.TestRunnerViewPart;
import org.eclipse.jdt.junit.launcher.JUnitLaunchShortcut;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.builds.core.IBuild;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.internal.core.operations.OperationChangeEvent;
import org.eclipse.mylyn.builds.internal.core.operations.OperationChangeListener;
import org.eclipse.mylyn.builds.internal.core.operations.RefreshOperation;
import org.eclipse.mylyn.builds.internal.core.util.JUnitResultGenerator;
import org.eclipse.mylyn.internal.builds.ui.BuildsUiInternal;
import org.eclipse.mylyn.internal.builds.ui.BuildsUiPlugin;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonUiUtil;
import org.eclipse.mylyn.internal.provisional.commons.ui.ICoreRunnable;
import org.eclipse.mylyn.internal.provisional.commons.ui.WorkbenchUtil;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.statushandlers.StatusManager;
import org.xml.sax.SAXException;

/**
 * @author Steffen Pingel
 */
public class TestResultManager {

	private static class Session extends TestRunSession {

		private Session(IBuild build) {
			super(NLS.bind("Test Results for Build {0}", build.getLabel()), null);
		}

		// Eclipse 3.5 and earlier
		public boolean rerunTest(String testId, String className, String testName, String launchMode)
				throws CoreException {
			return rerunTest(testId, className, testName, launchMode, false);
		}

		// Eclipse 3.6 and later
		public boolean rerunTest(String testId, final String className, final String testName, String launchMode,
				boolean buildBeforeLaunch) throws CoreException {
			final AtomicReference<IJavaElement> result = new AtomicReference<IJavaElement>();
			CommonUiUtil.busyCursorWhile(new ICoreRunnable() {
				public void run(IProgressMonitor monitor) throws CoreException {
					IType type = findType(className, monitor);
					if (type == null) {
						return;
					}
					if (testName != null) {
						IMethod method = type.getMethod(testName, new String[0]);
						if (method != null && method.exists()) {
							result.set(method);
						} else {
							result.set(type);
						}
					}
				}
			});
			if (result.get() == null) {
				String typeName = className;
				if (testName != null) {
					typeName += "." + testName + "()"; //$NON-NLS-1$ //$NON-NLS-2$
				}
				throw new CoreException(new Status(IStatus.ERROR, BuildsUiPlugin.ID_PLUGIN, NLS.bind(
						"Launch failed: Test ''{0}'' not found in workspace.", typeName)));
			}
			JUnitLaunchShortcut shortcut = new JUnitLaunchShortcut();
			shortcut.launch(new StructuredSelection(result.get()), launchMode);
			return true;
		}

		/**
		 * @see {@link org.eclipse.jdt.internal.junit.ui.OpenTestAction}
		 */
		private IType findType(String className, IProgressMonitor monitor) throws CoreException {
			final IType[] result = { null };
			TypeNameMatchRequestor nameMatchRequestor = new TypeNameMatchRequestor() {
				@Override
				public void acceptTypeNameMatch(TypeNameMatch match) {
					result[0] = match.getType();
				}
			};
			int lastDot = className.lastIndexOf('.');
			char[] packageName = lastDot >= 0 ? className.substring(0, lastDot).toCharArray() : null;
			char[] typeName = (lastDot >= 0 ? className.substring(lastDot + 1) : className).toCharArray();
			SearchEngine engine = new SearchEngine();
			engine.searchAllTypeNames(packageName, SearchPattern.R_EXACT_MATCH | SearchPattern.R_CASE_SENSITIVE,
					typeName, SearchPattern.R_EXACT_MATCH | SearchPattern.R_CASE_SENSITIVE, IJavaSearchConstants.TYPE,
					SearchEngine.createWorkspaceScope(), nameMatchRequestor,
					IJavaSearchConstants.WAIT_UNTIL_READY_TO_SEARCH, monitor);
			return result[0];
		}

	}

	public static void showInJUnitView(final IBuild build) {
		Assert.isNotNull(build);

		if (build.getTestResult() == null) {
			StatusManager.getManager().handle(
					new Status(IStatus.ERROR, BuildsUiPlugin.ID_PLUGIN, "The build did not produce test results."),
					StatusManager.SHOW | StatusManager.BLOCK);
			return;
		}

		final TestRunSession testRunSession = new Session(build);
		try {
			CommonUiUtil.busyCursorWhile(new ICoreRunnable() {
				public void run(IProgressMonitor monitor) throws CoreException {
					JUnitResultGenerator generator = new JUnitResultGenerator(build.getTestResult());
					TestRunHandler handler = new TestRunHandler(testRunSession);
					try {
						generator.write(handler);
					} catch (SAXException e) {
						throw new CoreException(new Status(IStatus.ERROR, BuildsUiPlugin.ID_PLUGIN,
								"Unexpected parsing error while preparing test results", e));
					}
				}
			});
		} catch (OperationCanceledException e) {
			return;
		} catch (CoreException e) {
			StatusManager.getManager().handle(
					new Status(IStatus.ERROR, BuildsUiPlugin.ID_PLUGIN,
							"Unexpected error while processing test results", e),
					StatusManager.SHOW | StatusManager.LOG);
			return;
		}

		// show results in view
		WorkbenchUtil.showViewInActiveWindow(TestRunnerViewPart.NAME);
		getJUnitModel().addTestRunSession(testRunSession);
	}

	public static void showInJUnitView(final IBuildPlan plan) {
		if (plan.getLastBuild() != null) {
			showInJUnitView(plan.getLastBuild());
		} else {
			RefreshOperation operation = BuildsUiInternal.getFactory().getRefreshOperation(plan);
			operation.addOperationChangeListener(new OperationChangeListener() {
				@Override
				public void done(OperationChangeEvent event) {
					event.getOperation().getService().getRealm().asyncExec(new Runnable() {
						public void run() {
							if (plan.getLastBuild() != null) {
								showInJUnitView(plan.getLastBuild());
							}
						}
					});
				}
			});
			operation.execute();
		}
	}

	private static JUnitModel junitModel;

	static JUnitModel getJUnitModel() {
		if (junitModel == null) {
			Exception cause = null;
			try {
				// Eclipse 3.6 or later
				Class<?> clazz;
				try {
					clazz = Class.forName("org.eclipse.jdt.internal.junit.JUnitCorePlugin");
				} catch (ClassNotFoundException e) {
					// Eclipse 3.5 and earlier
					clazz = Class.forName("org.eclipse.jdt.internal.junit.ui.JUnitPlugin");
				}

				Method method = clazz.getDeclaredMethod("getModel");
				junitModel = (JUnitModel) method.invoke(null);
			} catch (Exception e) {
				NoClassDefFoundError error = new NoClassDefFoundError("Unable to locate container for JUnitModel");
				error.initCause(cause);
				throw error;
			}
		}
		return junitModel;
	}

}
