/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
/*
 * Created on May 22, 2005
 */
package org.eclipse.mylar.core.internal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.eclipse.mylar.core.util.ContextReader;
import org.eclipse.mylar.core.util.ContextWriter;
import org.eclipse.mylar.core.util.ErrorLogger;

/**
 * @author Mik Kersten
 */
public class MylarContextExternalizer {

	public static final String INTERACTION_EVENT_ID = "interactionEvent";

	private ContextReader reader = new ContextReader();

	private ContextWriter writer = new ContextWriter();
	
//	private SaxContextReader reader = new SaxContextReader();
//
//	private SaxContextWriter writer = new SaxContextWriter();

	public void writeContextToXML(MylarContext context, File file) {
		if (context.getInteractionHistory().isEmpty()) return;
		try {
			if (!file.exists()) file.createNewFile();
			OutputStream stream = new FileOutputStream(file);
			writer.setOutputStream(stream);
			writer.writeContextToStream(context);
			stream.close();
		} catch (IOException e) {
			ErrorLogger.fail(e, "Could not write: " + file.getAbsolutePath(), true);
		}
	}

	public MylarContext readContextFromXML(File file) {
		try {
			if (!file.exists())
				return null;
			return reader.readContext(file);
		} catch (Exception e) {
			ErrorLogger.fail(e, "Could not read: " + file.getAbsolutePath(), true);
		}
		return null;
	}
}
