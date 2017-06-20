/*******************************************************************************
 * Copyright (c) 2012 Research Group for Industrial Software (INSO), Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Research Group for Industrial Software (INSO), Vienna University of Technology - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.versions.tasks.context;

import org.eclipse.jface.action.Action;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.internal.monitor.ui.MonitorUiPlugin;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.eclipse.mylyn.monitor.core.InteractionEvent.Kind;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.versions.core.Change;
import org.eclipse.mylyn.versions.core.ChangeType;
import org.eclipse.mylyn.versions.tasks.core.TaskChangeSet;
import org.eclipse.mylyn.versions.tasks.ui.spi.ITaskVersionsContributionAction;
import org.eclipse.mylyn.versions.tasks.ui.spi.ITaskVersionsModel;
import org.eclipse.swt.widgets.Event;

/**
 *
 * @author Kilian Matt
 *
 */
public class ImportAsContextAction extends Action implements
		ITaskVersionsContributionAction {

	private static final String ORIGIN = "org.eclipse.mylyn.versions.tasks.contextImporter.";

	public ImportAsContextAction() {
		super("Convert to Context", AS_PUSH_BUTTON);
		setDescription("Convert to context");
		setImageDescriptor(TasksUiImages.CONTEXT_ATTACH);
		setToolTipText("Convert to context");
	}

	public void run() {
		throw new java.lang.UnsupportedOperationException();
	}

	private String formatHandleString(Change c) {
		return "/" + c.getTarget().getPath();
	}

	private boolean elementNotDeleted(Change c) {
		return c.getChangeType() == ChangeType.MODIFIED
				|| c.getChangeType() == ChangeType.ADDED
				|| c.getChangeType() == ChangeType.REPLACED;
	}

	public void runWithEvent(Event event) {
		run();
	}

	@Override
	public void run(ITaskVersionsModel model) {
		for (TaskChangeSet cs : model.getInput()) {
			for (Change c : cs.getChangeset().getChanges()) {

				if (elementNotDeleted(c)) {
					InteractionEvent interactionEvent = new InteractionEvent(
							Kind.SELECTION, null, formatHandleString(c), ORIGIN);
					ContextCore.getContextManager().processInteractionEvent(interactionEvent);

					MonitorUiPlugin.getDefault().notifyInteractionObserved(interactionEvent);
				}
			}
		}
	}

}
