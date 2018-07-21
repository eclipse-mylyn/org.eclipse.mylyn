/*******************************************************************************
 * Copyright (c) 2012 Frank Becker and others.
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

package org.eclipse.mylyn.internal.bugzilla.ui.editor;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorDescriptionPart;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;

public class BugzillaTaskEditorDescriptionPart extends TaskEditorDescriptionPart {
	private class LockAction extends Action {

		public LockAction() {
			super();
			updateActionState();
		}

		private void updateActionState() {
			TaskAttribute isPrivate = getAttribute().getAttribute(IBugzillaConstants.BUGZILLA_DESCRIPTION_IS_PRIVATE);
			if ("1".compareTo(isPrivate.getValue()) == 0) { //$NON-NLS-1$
				this.setImageDescriptor(TasksUiImages.LOCK_CLOSE);
			} else {
				this.setImageDescriptor(TasksUiImages.LOCK_OPEN);
			}
		}

		@Override
		public void run() {
			TaskAttribute isPrivateAttribute = getAttribute().getAttribute(
					IBugzillaConstants.BUGZILLA_DESCRIPTION_IS_PRIVATE);
			// isPrivateAttribute can not be null because we only add the Action when the Attribute exists
			TaskAttribute idAttribute = getAttribute().getAttribute(IBugzillaConstants.BUGZILLA_DESCRIPTION_ID);
			boolean oldValue = "1".compareTo(isPrivateAttribute.getValue()) == 0; //$NON-NLS-1$
			isPrivateAttribute.setValue(!oldValue ? "1" : "0"); //$NON-NLS-1$ //$NON-NLS-2$
			String value = idAttribute.getValue();
			TaskAttribute definedIsPrivate = getAttribute().getAttribute(
					IBugzillaConstants.BUGZILLA_PREFIX_DEFINED_ISPRIVATE + value);
			if (definedIsPrivate == null) {
				definedIsPrivate = getAttribute().createAttribute(
						IBugzillaConstants.BUGZILLA_PREFIX_DEFINED_ISPRIVATE + value);
			}
			TaskAttribute isPrivate = getAttribute().getAttribute(IBugzillaConstants.BUGZILLA_PREFIX_ISPRIVATE + value);
			if (isPrivate == null) {
				isPrivate = getAttribute().createAttribute(IBugzillaConstants.BUGZILLA_PREFIX_ISPRIVATE + value);
			}
			definedIsPrivate.setValue("1"); //$NON-NLS-1$
			isPrivate.setValue(!oldValue ? "1" : "0"); //$NON-NLS-1$ //$NON-NLS-2$
			getModel().attributeChanged(getAttribute());
			updateActionState();
		}
	}

	@Override
	protected void fillToolBar(ToolBarManager toolBar) {
		String insidergroup = getModel().getTaskRepository().getProperty(IBugzillaConstants.BUGZILLA_INSIDER_GROUP);
		TaskAttribute isPrivate = getAttribute().getAttribute(IBugzillaConstants.BUGZILLA_DESCRIPTION_IS_PRIVATE);
		if (Boolean.parseBoolean(insidergroup) && isPrivate != null) {
			LockAction lockAction = new LockAction();
			toolBar.add(lockAction);
		}
		super.fillToolBar(toolBar);
	}

}
