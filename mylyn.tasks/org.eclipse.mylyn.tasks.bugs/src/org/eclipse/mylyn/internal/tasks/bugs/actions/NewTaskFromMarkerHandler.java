/*******************************************************************************
 * Copyright (c) 2004, 2011 Frank Becker and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.bugs.actions;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylyn.commons.workbench.WorkbenchUtil;
import org.eclipse.mylyn.tasks.core.TaskMapping;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
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

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IMarker[] markers = getSelectedMarkers(event);
		if (markers.length == 0 || markers[0] == null) {
			MessageDialog.openInformation(null, Messages.NewTaskFromMarkerHandler_New_Task_from_Marker,
					Messages.NewTaskFromMarkerHandler_No_marker_selected);
			return null;
		}
		final IMarker marker = markers[0];
		TaskMapping mapping = new TaskMapping() {

			@Override
			public String getSummary() {
				StringBuilder sb = new StringBuilder();
				try {
					MarkerType type = MarkerTypesModel.getInstance().getType(marker.getType());
					sb.append(type.getLabel() + ": "); //$NON-NLS-1$
				} catch (CoreException e) {
					// ignore
				}

				return sb.toString() + marker.getAttribute("message", ""); //$NON-NLS-1$ //$NON-NLS-2$
			}

			@Override
			public String getDescription() {
				return buildDescriptionFromMarkerItem(marker);
			}

		};

		TasksUiUtil.openNewTaskEditor(WorkbenchUtil.getShell(), mapping, null);
		return null;
	}

	private String buildDescriptionFromMarkerItem(IMarker marker) {
		StringBuilder sb = new StringBuilder();
		sb.append(Messages.NewTaskFromMarkerHandler_Resource_
				+ marker.getResource().getFullPath().removeLastSegments(1).toString().substring(1) + "/" //$NON-NLS-1$
				+ marker.getResource().getName());
		int lineNumber = marker.getAttribute(IMarker.LINE_NUMBER, -1);
		if (lineNumber != -1) {
			sb.append(Messages.NewTaskFromMarkerHandler_LOCATION_LINE + lineNumber);
		}
		return sb.toString();
	}

}
