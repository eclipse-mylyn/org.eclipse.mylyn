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

import java.io.BufferedReader;
import java.io.IOException;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.mylyn.builds.core.IBuild;
import org.eclipse.mylyn.builds.internal.core.BuildModel;
import org.eclipse.mylyn.builds.internal.core.operations.GetBuildOutputOperation;
import org.eclipse.mylyn.builds.internal.core.operations.GetBuildOutputOperation.BuildOutputEvent;
import org.eclipse.mylyn.builds.internal.core.operations.GetBuildOutputOperation.BuildOutputReader;
import org.eclipse.mylyn.commons.core.IOperationMonitor;
import org.eclipse.mylyn.internal.builds.ui.BuildImages;
import org.eclipse.mylyn.internal.builds.ui.BuildsUiInternal;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

/**
 * @author Steffen Pingel
 */
public class BuildConsole {

	private final IBuild build;

	private final IConsoleManager consoleManager;

	private MessageConsole console;

	private GetBuildOutputOperation operation;

	private MessageConsoleStream stream;

	private final BuildModel model;

	final static String CONSOLE_TYPE = "org.eclipse.mylyn.builds.ui.console.BuildConsole";

	final static String ATTRIBUTE_BUILD = "org.eclipse.mylyn.builds.ui.console.build";

	public BuildConsole(IConsoleManager consoleManager, BuildModel model, IBuild build) {
		Assert.isNotNull(consoleManager);
		Assert.isNotNull(model);
		Assert.isNotNull(build);
		this.consoleManager = consoleManager;
		this.model = model;
		this.build = build;
	}

	public MessageConsole show() {
		if (console == null) {
			console = new MessageConsole(NLS.bind("Output for Build {0}", build.getLabel()), CONSOLE_TYPE,
					BuildImages.CONSOLE, true);
			consoleManager.addConsoles(new IConsole[] { console });
			console.setAttribute(ATTRIBUTE_BUILD, build);

			stream = console.newMessageStream();
		}

		doGetOutput();

		consoleManager.showConsoleView(console);
		return console;
	}

	public void close() {
		if (operation != null) {
			operation.cancel();
		}
	}

	private void doGetOutput() {
		if (operation == null) {
			operation = new GetBuildOutputOperation(BuildsUiInternal.getOperationService(), build,
					new BuildOutputReader() {
						@Override
						public void handle(BuildOutputEvent event, IOperationMonitor monitor) throws IOException,
								CoreException {
							BufferedReader reader = event.getInput();
							String line;
							while ((line = reader.readLine()) != null) {
								if (stream.isClosed()) {
									throw new OperationCanceledException();
								}
								stream.println(line);
							}
						}

						@Override
						public void done() {
							operation = null;
						}
					});
			operation.execute();
		}
	}

}
