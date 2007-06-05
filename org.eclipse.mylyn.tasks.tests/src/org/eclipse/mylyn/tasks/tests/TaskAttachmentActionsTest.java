/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.tasks.tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.net.URLConnection;

import junit.framework.TestCase;

import org.eclipse.mylar.internal.tasks.ui.actions.CopyAttachmentToClipboardJob;
import org.eclipse.mylar.internal.tasks.ui.actions.SaveRemoteFileAction;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.ui.PlatformUI;

/**
 * Test task attachment actions.
 * 
 * @author Jeff Pound
 */
public class TaskAttachmentActionsTest extends TestCase {

	/**
	 * Copy some text to the clipboard using the CopyToClipboardAction and
	 * ensure the contents of the clipboard match.
	 * 
	 * @throws Exception
	 */
	public void testCopyToClipboardAction() throws Exception {
		String contents = "Sample clipboard text";
		CopyAttachmentToClipboardJob job = new CopyAttachmentToClipboardJob(null);
		job.copyToClipboard(contents);

		// wait for job to finish
		while (PlatformUI.getWorkbench().getDisplay().readAndDispatch()) {
		}
		
		Clipboard clipboard = new Clipboard(PlatformUI.getWorkbench().getDisplay());
		assertEquals(contents, clipboard.getContents(TextTransfer.getInstance()));
	}

	/**
	 * Save a file using the SaveRemoteFileAction and ensure it exists and the
	 * contents are correct
	 * 
	 * @throws Exception
	 */
	public void testSaveRemoteFileAction() throws Exception {
		String localFile = "TaskAttachmentActionsTest.testfile";
		String url = "http://mylar.eclipse.org/bugs222/attachment.cgi?id=189";
		int lineCount = 11;

		SaveRemoteFileAction action = new SaveRemoteFileAction();

		URLConnection urlConnect;
		urlConnect = (new URL(url)).openConnection();
		urlConnect.connect();

		action.setInputStream(urlConnect.getInputStream());
		action.setDestinationFilePath(localFile);
		action.run();

		File file = new File(localFile);		
		assertTrue(file.exists());

		BufferedReader reader = new BufferedReader(new FileReader(file));
		int lines = 0;
		while (reader.readLine() != null) {
			lines++;
		}

		assertEquals(lineCount, lines);
		file.delete();
	}

}
