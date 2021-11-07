/*******************************************************************************
 * Copyright (c) 2013, 2021 David Green and others.
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

package org.eclipse.mylyn.internal.wikitext.ui.commands;

import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.mylyn.internal.wikitext.ui.util.IOUtil;
import org.eclipse.mylyn.wikitext.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.ui.commands.AbstractMarkupResourceHandler;
import org.eclipse.mylyn.wikitext.util.ServiceLocator;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.PlatformUI;

/**
 * Command that parses an input file and generates a corresponding output file in the specified {@link MarkupLanguage}.
 */
public class ConvertMarkupToMarkup extends AbstractMarkupResourceHandler {

	static final String PARAM_MARKUP_LANGUAGE = "org.eclipse.mylyn.wikitext.ui.targetLanguage"; //$NON-NLS-1$

	static final String COMMAND_ID = "org.eclipse.mylyn.wikitext.ui.convertToMarkupCommand"; //$NON-NLS-1$

	@Override
	protected void handleFile(ExecutionEvent event, IFile file, String name) throws ExecutionException {
		MarkupLanguage targetMmarkupLanguage = ServiceLocator.getInstance()
				.getMarkupLanguage(event.getParameter(PARAM_MARKUP_LANGUAGE));

		// TODO: better way to get the file extension
		String extension = targetMmarkupLanguage.getName().toLowerCase().replaceAll("\\W", ""); //$NON-NLS-1$ //$NON-NLS-2$

		final IFile newFile = file.getParent().getFile(new Path(name + "." + extension)); //$NON-NLS-1$
		if (newFile.exists()) {
			if (!MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					NLS.bind(Messages.ConvertMarkupToMarkup_overwrite_file, targetMmarkupLanguage.getName()),
					NLS.bind(Messages.ConvertMarkupToMarkup_overwrite_file_detail,
							new Object[] { newFile.getFullPath() }))) {
				return;
			}
		}

		StringWriter writer = new StringWriter();

		MarkupParser parser = new MarkupParser();
		parser.setMarkupLanguage(markupLanguage);
		parser.setBuilder(targetMmarkupLanguage.createDocumentBuilder(writer));

		try {
			String inputContent = IOUtil.readFully(file);
			parser.parse(inputContent);
			final String targetConent = writer.toString();

			IRunnableWithProgress runnable = monitor -> {
				try {
					if (newFile.exists()) {
						newFile.setContents(new ByteArrayInputStream(targetConent.getBytes(StandardCharsets.UTF_8)),
								false, true, monitor);
					} else {
						newFile.create(new ByteArrayInputStream(targetConent.getBytes(StandardCharsets.UTF_8)), false,
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
			try (PrintWriter out = new PrintWriter(message)) {
				out.println(NLS.bind(Messages.ConvertMarkupToMarkup_cannot_generate_detail,
						targetMmarkupLanguage.getName(), e.getMessage()));
				out.println(Messages.ConvertMarkupToMarkup_details_follow);
				e.printStackTrace(out);
			}

			MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					NLS.bind(Messages.ConvertMarkupToMarkup_cannot_generate_title, targetMmarkupLanguage.getName()),
					message.toString());
		}
	}

	@Override
	protected void handleFile(IFile file, String name) throws ExecutionException {
		throw new UnsupportedOperationException();
	}

}
