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
package org.eclipse.mylyn.internal.builds.ui.console;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.builds.core.IBuild;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.internal.core.operations.OperationChangeEvent;
import org.eclipse.mylyn.builds.internal.core.operations.OperationChangeListener;
import org.eclipse.mylyn.builds.internal.core.operations.RefreshOperation;
import org.eclipse.mylyn.internal.builds.ui.BuildsUiInternal;
import org.eclipse.ui.console.ConsolePlugin;

/**
 * @author Steffen Pingel
 */
public class BuildConsoleManager {

	private static boolean consoleAvailable = false;

	static {
		try {
			if (ConsolePlugin.getDefault() != null) {
				consoleAvailable = true;
			}
		} catch (Throwable e) {
			// ignore
		}
	}

	public static boolean isConsoleAvailable() {
		return consoleAvailable;
	}

	private final Map<IBuild, BuildConsole> consoleByBuild;

	public BuildConsoleManager() {
		consoleByBuild = new HashMap<IBuild, BuildConsole>();
	}

	public BuildConsole showConsole(IBuild build) {
		Assert.isNotNull(build);
		BuildConsole console = consoleByBuild.get(build);
		if (console == null) {
			console = new BuildConsole(ConsolePlugin.getDefault().getConsoleManager(), BuildsUiInternal.getModel(),
					build);
			consoleByBuild.put(build, console);

		}
		console.show();
		return console;
	}

	public void showConsole(final IBuildPlan plan) {
		if (plan.getLastBuild() != null) {
			showConsole(plan.getLastBuild());
		} else {
			RefreshOperation operation = BuildsUiInternal.getFactory().getRefreshOperation(plan);
			operation.addOperationChangeListener(new OperationChangeListener() {
				@Override
				public void done(OperationChangeEvent event) {
					event.getOperation().getService().getRealm().asyncExec(new Runnable() {
						public void run() {
							if (plan.getLastBuild() != null) {
								showConsole(plan.getLastBuild());
							}
						}
					});
				}
			});
			operation.execute();
		}
	}
}
