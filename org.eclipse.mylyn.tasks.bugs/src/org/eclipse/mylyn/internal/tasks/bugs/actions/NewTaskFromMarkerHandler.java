/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.bugs.actions;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.tasks.core.TaskMapping;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.markers.MarkerViewHandler;
import org.eclipse.ui.views.markers.internal.MarkerType;
import org.eclipse.ui.views.markers.internal.MarkerTypesModel;

/**
 * Creates a new task from the selected marker entry.
 * 
 * @author Frank Becker
 * @since 3.0
 */
@SuppressWarnings("restriction")
public class NewTaskFromMarkerHandler extends MarkerViewHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IMarker[] markers = getSelectedMarkers(event);
		if (markers.length == 0 || markers[0] == null) {
			return null;
		}

		final IMarker marker = markers[0];
		TaskMapping mapping = new TaskMapping() {

			@Override
			public String getSummary() {
				StringBuilder sb = new StringBuilder();
				try {
					MarkerType type = MarkerTypesModel.getInstance().getType(marker.getType());
					sb.append(type.getLabel() + ": ");
				} catch (CoreException e) {
					// ignore
				}

				return sb.toString() + marker.getAttribute("message", "");
			}

			@Override
			public String getDescription() {
				return buildDescriptionFromMarkerItem(marker);
			}

		};

		TasksUiUtil.openNewTaskEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), mapping, null);
		return null;
	}

	private String buildDescriptionFromMarkerItem(IMarker marker) {

		StringBuilder sb = new StringBuilder();
		try {
			sb.append("Resource: " + marker.getResource().getFullPath().removeLastSegments(1).toString().substring(1)
					+ "/" + marker.getResource().getName());
			int lineNumber = marker.getAttribute(IMarker.LINE_NUMBER, -1);
			if (lineNumber != -1) {
				sb.append("\nLocation: line " + lineNumber);
			}
		} catch (Exception e) {
			// ignore
		}

		return sb.toString();
	}

}
