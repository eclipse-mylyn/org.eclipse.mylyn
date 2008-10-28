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
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.mylyn.wikitext.core.parser.util.MarkupToDocbook;
import org.eclipse.ui.PlatformUI;

/**
 * 
 * 
 * @author David Green
 */
public class ConvertMarkupToDocbook extends AbstractMarkupResourceHandler {

	@Override
	protected void handleFile(IFile file, String name) {
		final IFile newFile = file.getParent().getFile(new Path(name + ".xml")); //$NON-NLS-1$
		if (newFile.exists()) {
			if (!MessageDialog.openQuestion(
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					Messages.getString("ConvertMarkupToDocbook.1"), MessageFormat.format(Messages.getString("ConvertMarkupToDocbook.2"), newFile.getFullPath()))) { //$NON-NLS-1$ //$NON-NLS-2$
				return;
			}
		}

		MarkupToDocbook markupToDocbook = new MarkupToDocbook();
		markupToDocbook.setMarkupLanguage(markupLanguage);
		markupToDocbook.setBookTitle(name);

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

			final String docbook = markupToDocbook.parse(w.toString());

			IRunnableWithProgress runnable = new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					try {
						if (newFile.exists()) {
							newFile.setContents(new ByteArrayInputStream(docbook.getBytes("utf-8")), false, true, //$NON-NLS-1$
									monitor);
						} else {
							newFile.create(new ByteArrayInputStream(docbook.getBytes("utf-8")), false, monitor); //$NON-NLS-1$
						}
						newFile.setCharset("utf-8", monitor); //$NON-NLS-1$
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
			out.println(Messages.getString("ConvertMarkupToDocbook.6") + e.getMessage()); //$NON-NLS-1$
			out.println(Messages.getString("ConvertMarkupToDocbook.7")); //$NON-NLS-1$
			e.printStackTrace(out);
			out.close();

			MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					Messages.getString("ConvertMarkupToDocbook.8"), message.toString()); //$NON-NLS-1$
		}
	}

}
