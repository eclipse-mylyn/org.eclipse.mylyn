/*******************************************************************************
 * Copyright (c) 2007, 2013 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.ui.commands;

import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.mylyn.internal.wikitext.ui.util.IOUtil;
import org.eclipse.mylyn.wikitext.parser.util.MarkupToEclipseToc;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.PlatformUI;

/**
 * @author David Green
 * @since 1.0
 */
public class ConvertMarkupToEclipseHelp extends ConvertMarkupToHtml {

	@Override
	protected void handleFile(final IFile file, String name) {
		super.handleFile(file, name);
		final IFile newFile = file.getParent().getFile(new Path(name + "-toc.xml")); //$NON-NLS-1$
		if (newFile.exists()) {
			if (!MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					Messages.ConvertMarkupToEclipseHelp_overwrite,
					NLS.bind(Messages.ConvertMarkupToEclipseHelp_fileExistsOverwrite,
							new Object[] { newFile.getFullPath() }))) {
				return;
			}
		}

		IPath parentFullPath = file.getParent().getFullPath();
		IPath pluginPathToHelp = parentFullPath.removeFirstSegments(1);

		final MarkupToEclipseToc markupToEclipseToc = new MarkupToEclipseToc();
		markupToEclipseToc.setMarkupLanguage(markupLanguage);
		markupToEclipseToc.setBookTitle(name);

		String htmlFilePath = name + ".html"; //$NON-NLS-1$
		if (pluginPathToHelp.segmentCount() > 0) {
			String pathPart = pluginPathToHelp.toString();
			if (!pathPart.endsWith("/")) { //$NON-NLS-1$
				pathPart = pathPart + "/"; //$NON-NLS-1$
			}
			htmlFilePath = pathPart + htmlFilePath;
		}
		markupToEclipseToc.setHtmlFile(htmlFilePath);

		try {

			IRunnableWithProgress runnable = monitor -> {

				try {
					String content = IOUtil.readFully(file);
					final String tocXml = markupToEclipseToc.parse(content);
					if (newFile.exists()) {
						newFile.setContents(new ByteArrayInputStream(tocXml.getBytes(StandardCharsets.UTF_8)), false,
								true, monitor);
					} else {
						newFile.create(new ByteArrayInputStream(tocXml.getBytes(StandardCharsets.UTF_8)), false,
								monitor);
					}
					newFile.setCharset("utf-8", monitor); //$NON-NLS-1$
				} catch (Exception e) {
					throw new InvocationTargetException(e);
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
			out.println(Messages.ConvertMarkupToEclipseHelp_cannotConvert + e.getMessage());
			out.println(Messages.ConvertMarkupToEclipseHelp_detailsFollow);
			e.printStackTrace(out);
			out.close();

			MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					Messages.ConvertMarkupToEclipseHelp_cannotCompleteOperation, message.toString());
		}
	}

}
