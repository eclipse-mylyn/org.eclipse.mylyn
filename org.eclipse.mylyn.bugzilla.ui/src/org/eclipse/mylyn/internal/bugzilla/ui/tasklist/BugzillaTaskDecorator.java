/*******************************************************************************
 * Copyright (c) 2006 - 2006 Mylar eclipse.org project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Mylar project committers - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.bugzilla.ui.tasklist;

import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaQueryHit;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaReportElement;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaTask;
import org.eclipse.mylar.internal.bugzilla.ui.BugzillaImages;
import org.eclipse.mylar.tasks.core.RepositoryTaskAttribute;

/**
 * @author Mik Kersten
 */
public class BugzillaTaskDecorator implements ILightweightLabelDecorator {

	public void decorate(Object element, IDecoration decoration) {
		if (element instanceof BugzillaTask) {			
			BugzillaTask task = (BugzillaTask) element;
			if (task.getTaskData() != null) {
				RepositoryTaskAttribute attribute = task.getTaskData().getAttribute(
						BugzillaReportElement.BUG_SEVERITY.getKeyString());
				if (attribute != null) {
					String severity = attribute.getValue();
					// XXX: refactor to use configuration
					if ("major".equals(severity) || "blocker".equals(severity) || "critical".equals(severity)) {
						decoration.addOverlay(BugzillaImages.OVERLAY_MAJOR, IDecoration.BOTTOM_RIGHT);
					} else if ("enhancement".equals(severity)) {
						decoration.addOverlay(BugzillaImages.OVERLAY_ENHANCEMENT, IDecoration.BOTTOM_RIGHT);
					}
				}
			}
		} else if (element instanceof BugzillaQueryHit) {
			BugzillaQueryHit hit = (BugzillaQueryHit) element;
			if (hit.getCorrespondingTask() != null) {
				decorate(hit.getCorrespondingTask(), decoration);
			}
		}
	}

	public void addListener(ILabelProviderListener listener) {
		// ignore
	}

	public void dispose() {
		// ignore
	}

	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
		// ignore
	}
}
