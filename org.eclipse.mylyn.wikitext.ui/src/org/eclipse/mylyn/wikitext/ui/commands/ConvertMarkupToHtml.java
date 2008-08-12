/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.ui.commands;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.core.util.XmlStreamWriter;
import org.eclipse.ui.PlatformUI;

/**
 * 
 * 
 * @author David Green
 */
public class ConvertMarkupToHtml extends AbstractMarkupResourceHandler {

	@Override
	protected void handleFile(IFile file, String name) {
		final IFile newFile = file.getParent().getFile(new Path(name + ".html"));
		if (newFile.exists()) {
			if (!MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					"Overwrite?", String.format("File '%s' exists: overwrite?", newFile.getFullPath()))) {
				return;
			}
		}

		StringWriter writer = new StringWriter();
		HtmlDocumentBuilder builder = new HtmlDocumentBuilder(writer) {
			@Override
			protected XmlStreamWriter createXmlStreamWriter(Writer out) {
				return super.createFormattingXmlStreamWriter(out);
			}
		};
		MarkupParser parser = new MarkupParser();
		parser.setMarkupLanaguage(markupLanguage);
		parser.setBuilder(builder);
		builder.setEmitDtd(true);

		try {
			StringWriter w = new StringWriter();
			Reader r = new InputStreamReader(new BufferedInputStream(file.getContents()), file.getCharset());
			try {
				int i;
				while ((i = r.read()) != -1) {
					w.write((char) i);
				}
			} finally {
				r.close();
			}

			parser.parse(w.toString());
			final String xhtmlContent = writer.toString();

			IRunnableWithProgress runnable = new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					try {
						if (newFile.exists()) {
							newFile.setContents(new ByteArrayInputStream(xhtmlContent.getBytes("utf-8")), false, true,
									monitor);
						} else {
							newFile.create(new ByteArrayInputStream(xhtmlContent.getBytes("utf-8")), false, monitor);
						}
						newFile.setCharset("utf-8", monitor);
					} catch (Exception e) {
						throw new InvocationTargetException(e);
					}
				}
			};
			try {
				PlatformUI.getWorkbench().getProgressService().busyCursorWhile(runnable);
			} catch (InterruptedException e) {
				return;
			} catch (InvocationTargetException e) {
				throw e.getCause();
			}
		} catch (Throwable e) {
			StringWriter message = new StringWriter();
			PrintWriter out = new PrintWriter(message);
			out.println("Cannot convert to HTML: " + e.getMessage());
			out.println("Details follow:");
			e.printStackTrace(out);
			out.close();

			MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					"Cannot complete operation", message.toString());
		}
	}

}
