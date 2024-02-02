/*******************************************************************************
 * Copyright (c) 2010, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
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
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleListener;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;

/**
 * @author Steffen Pingel
 */
public class BuildConsoleManager {

	private final Map<IBuild, BuildConsole> consoleByBuild;

	private final IConsoleManager consoleManager;

	private final IConsoleListener listener = new IConsoleListener() {

		@Override
		public void consolesAdded(IConsole[] consoles) {
			// ignore

		}

		@Override
		public void consolesRemoved(IConsole[] consoles) {
			for (IConsole console : consoles) {
				if (BuildConsole.CONSOLE_TYPE.equals(console.getType())) {
					Object build = ((MessageConsole) console).getAttribute(BuildConsole.ATTRIBUTE_BUILD);
					if (build instanceof IBuild) {
						disposeConsole((IBuild) build);
					}
				}
			}
		}
	};

	public BuildConsoleManager() {
		consoleByBuild = new HashMap<>();
		consoleManager = ConsolePlugin.getDefault().getConsoleManager();
		consoleManager.addConsoleListener(listener);
	}

	protected void disposeConsole(IBuild build) {
		BuildConsole console = consoleByBuild.get(build);
		if (console != null) {
			console.close();
			consoleByBuild.remove(build);
		}
	}

	public BuildConsole showConsole(IBuild build) {
		Assert.isNotNull(build);
		BuildConsole console = consoleByBuild.get(build);
		if (console == null) {
			console = new BuildConsole(consoleManager, BuildsUiInternal.getModel(), build);
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
					event.getOperation().getService().getRealm().asyncExec(() -> {
						if (plan.getLastBuild() != null) {
							showConsole(plan.getLastBuild());
						}
					});
				}
			});
			operation.execute();
		}
	}

	public void stop() {
		consoleManager.removeConsoleListener(listener);
	}

}
