/*******************************************************************************
 * Copyright (c) 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Emerson Murphy-Hill
 *******************************************************************************/

package org.eclipse.mylyn.monitor.tests.usage.tests;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Random;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.mylyn.context.tests.support.FileTool;
import org.eclipse.mylyn.internal.monitor.usage.InteractionEventLogger;
import org.eclipse.mylyn.monitor.tests.MonitorTestsPlugin;
import org.eclipse.swt.widgets.Display;

public class InteractionEventLoggerTest extends TestCase {

	public void testLoggerProgress() throws Exception {
		runWith(FileTool.getFileInPlugin(MonitorTestsPlugin.getDefault(), new Path("testdata/monitor-log.xml")));
	}

	public void testLoggerProgressZip() throws Exception {
		runWith(FileTool.getFileInPlugin(MonitorTestsPlugin.getDefault(), new Path("testdata/usage-parsing.zip")));
	}

	public void testLoggerProgressBig() throws Exception {
		File file = createBigDummyFile();
		runWith(file);
		file.delete();
	}

	private void runWith(final File monitorFile) throws InvocationTargetException, InterruptedException {
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
		dialog.run(true, true, new IRunnableWithProgress() {

			public void run(IProgressMonitor monitor) {
				monitor.beginTask("Running test job", 1);
				InteractionEventLogger l = new InteractionEventLogger(null);
				l.getHistoryFromFile(monitorFile, new SubProgressMonitor(monitor, 1));
				monitor.done();
			}
		});
	}

	private File createBigDummyFile() {
		File monitorFile = new File("interaction_history_temp.xml");

		if (monitorFile.exists()) {
			monitorFile.delete();
		}
		try {
			monitorFile.createNewFile();

			BufferedWriter out = new BufferedWriter(new FileWriter(monitorFile));
			for (int i = 0; i < 20000; i++) {
				out.write("<interactionEvent>");
				out.newLine();
				out.write("<kind>selection</kind>");
				out.newLine();
				out.write("<date>");
				writeRandomDate(out);
				out.write("</date>");
				out.newLine();
				out.write("<endDate>");
				writeRandomDate(out);
				out.write("</endDate>");
				out.newLine();
				out.write("<originId>");
				writeRandomString(out);
				out.write("</originId>");
				out.newLine();
				out.write("<structureKind>");
				writeRandomString(out);
				out.write("</structureKind>");
				out.newLine();
				out.write("<structureHandle>");
				writeRandomString(out);
				out.write("</structureHandle>");
				out.newLine();
				out.write("<navigation>null</navigation>");
				out.newLine();
				out.write("<delta>false</delta>");
				out.newLine();
				out.write("</interactionEvent>");
				out.newLine();
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return monitorFile;
	}

	private void writeRandomString(BufferedWriter out) throws IOException {
		for (int j = 0; j < 3; j++) {
			out.write(Math.random() + "");
		}
	}

	Random random = new Random();

	DateFormat dateFormat = InteractionEventLogger.dateFormat();

	Calendar c = Calendar.getInstance();

	private void writeRandomDate(BufferedWriter out) throws IOException {
		c.setTimeInMillis(random.nextLong());
		out.write(dateFormat.format(c.getTime()));
	}
}
