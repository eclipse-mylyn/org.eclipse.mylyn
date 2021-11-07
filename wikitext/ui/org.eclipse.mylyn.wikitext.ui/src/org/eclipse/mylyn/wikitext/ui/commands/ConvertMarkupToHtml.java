/*******************************************************************************
 * Copyright (c) 2007, 2021 David Green and others.
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
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.mylyn.internal.wikitext.ui.util.IOUtil;
import org.eclipse.mylyn.wikitext.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.parser.builder.HtmlDocumentBuilder;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.PlatformUI;

/**
 * @author David Green
 * @since 1.0
 */
public class ConvertMarkupToHtml extends AbstractMarkupResourceHandler {

	@Override
	protected void handleFile(final IFile file, String name) {
		final IFile newFile = file.getParent().getFile(new Path(name + ".html")); //$NON-NLS-1$
		if (newFile.exists()) {
			if (!MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					Messages.ConvertMarkupToHtml_overwrite, NLS.bind(Messages.ConvertMarkupToHtml_fileExistsOverwrite,
							new Object[] { newFile.getFullPath() }))) {
				return;
			}
		}

		final StringWriter writer = new StringWriter();
		HtmlDocumentBuilder builder = new HtmlDocumentBuilder(writer, true);
		final MarkupParser parser = new MarkupParser();
		parser.setMarkupLanguage(markupLanguage);
		parser.setBuilder(builder);
		builder.setEmitDtd(true);

		try {

			IRunnableWithProgress runnable = monitor -> {
				try {
					String inputContent = IOUtil.readFully(file);
					parser.parse(inputContent);
					String xhtmlContent = writer.toString();

					if (newFile.exists()) {
						newFile.setContents(new ByteArrayInputStream(xhtmlContent.getBytes(StandardCharsets.UTF_8)),
								false, true, monitor);
					} else {
						newFile.create(new ByteArrayInputStream(xhtmlContent.getBytes(StandardCharsets.UTF_8)), false,
								monitor);
					}
					newFile.setCharset(StandardCharsets.UTF_8.name(), monitor);
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
			try (PrintWriter out = new PrintWriter(message)) {
				out.println(Messages.ConvertMarkupToHtml_cannotConvert + e.getMessage());
				out.println(Messages.ConvertMarkupToHtml_detailsFollow);
				e.printStackTrace(out);
			}

			MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					Messages.ConvertMarkupToHtml_cannotCompleteOperation, message.toString());
		}
	}

}
