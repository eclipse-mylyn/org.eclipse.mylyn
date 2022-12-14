/*******************************************************************************
 * Copyright (c) 2010, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui.commands;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.builds.core.IBuild;
import org.eclipse.mylyn.builds.core.IBuildElement;
import org.eclipse.mylyn.commons.ui.ClipboardCopier;
import org.eclipse.mylyn.internal.builds.ui.BuildsUiInternal;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author Steffen Pingel
 */
public class CopyDetailsHandler extends AbstractHandler {

	public enum Mode {
		KEY, SUMMARY, SUMMARY_URL, URL;

		public String toString() {
			switch (this) {
			case KEY:
				return "ID";
			case URL:
				return "URL";
			case SUMMARY:
				return "Summary";
			case SUMMARY_URL:
				return "Summary and URL";
			}
			return null;
		}

	}

	public static void copyDetails(List<IBuildElement> elements, final Mode mode) {
		ClipboardCopier.getDefault().copy(elements, new ClipboardCopier.TextProvider() {
			public String getTextForElement(Object element) {
				return getTextFor(element, mode);
			}
		});
	}

	public static String getTextFor(Object object) {
		return getTextFor(object, Mode.SUMMARY_URL);
	}

	public static String getTextFor(Object object, Mode mode) {
		StringBuffer sb = new StringBuffer();
		switch (mode) {
		case KEY:
			if (object instanceof IBuild) {
				IBuild build = (IBuild) object;
				if (build.getId() != null) {
					sb.append(build.getId());
				}
			} else if (object instanceof IBuildElement) {
				IBuildElement element = (IBuildElement) object;
				sb.append(element.getLabel());
			}
			break;
		case URL:
			if (object instanceof IBuildElement) {
				IBuildElement element = (IBuildElement) object;
				if (element.getUrl() != null) {
					sb.append(element.getUrl());
				}
			}
			break;
		case SUMMARY:
			if (object instanceof IBuild) {
				IBuild build = (IBuild) object;
				if (build.getLabel() != null) {
					sb.append(NLS.bind("Build {0}", build.getLabel()));
				}
			} else if (object instanceof IBuildElement) {
				IBuildElement element = (IBuildElement) object;
				sb.append(element.getLabel());
			}
			break;
		case SUMMARY_URL:
			if (object instanceof IBuildElement) {
				IBuildElement element = (IBuildElement) object;
				if (object instanceof IBuild) {
					IBuild build = (IBuild) object;
					if (build.getLabel() != null) {
						sb.append(NLS.bind("Build {0}", build.getLabel()));
					}
				}
				sb.append(element.getLabel());
				if (TasksUiInternal.isValidUrl(element.getUrl())) {
					sb.append(ClipboardCopier.LINE_SEPARATOR);
					sb.append(element.getUrl());
				}
			}
			break;
		}
		return sb.toString();
	}

	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection instanceof IStructuredSelection) {
			Mode mode = Mode.SUMMARY;
			String kind = event.getParameter("kind");
			if (kind != null) {
				try {
					mode = Mode.valueOf(kind);
				} catch (IllegalArgumentException e) {
					throw new ExecutionException(NLS.bind("Invalid kind ''{0}'' specified", kind));
				}
			}
			copyDetails(BuildsUiInternal.getElements(event), mode);
		}
		return null;
	}

}
