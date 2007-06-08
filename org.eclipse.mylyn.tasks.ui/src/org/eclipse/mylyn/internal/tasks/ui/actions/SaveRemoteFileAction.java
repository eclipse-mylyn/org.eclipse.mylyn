/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.jface.action.Action;

/**
 * An <code>Action</code> which saves the given file contents to the given
 * file location on disk.
 * 
 * @author Jeff Pound
 */
public class SaveRemoteFileAction extends Action {

	public static final String TITLE = "Save...";

	private String destinationFilePath;

	private InputStream inStream;

	public SaveRemoteFileAction() {
		super(TITLE);
	}

	@Override
	public void run() {
		if (destinationFilePath == null || inStream == null) {
			return;
		}

		File outFile = new File(destinationFilePath);
		try {
			/* TODO jpound - Use FileWriter iff text? */
			FileOutputStream writer = new FileOutputStream(outFile);
			BufferedInputStream reader = new BufferedInputStream(inStream);

			int c;
			while ((c = reader.read()) != -1) {
				writer.write(c);
			}

			writer.close();
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setDestinationFilePath(String destinationFilePath) {
		this.destinationFilePath = destinationFilePath;
	}

	public void setInputStream(InputStream inStream) {
		this.inStream = inStream;
	}
}
