/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

/**
 * @author Mik Kersten
 */
//public class ManageEditorsAction extends Action {
//
//	public static final String ID = "org.eclipse.mylyn.ui.editors.auto.manage";
//	public ManageEditorsAction() {
//		super("Manage Editors with Context", IAction.AS_CHECK_BOX);
//		setId(ID);
//		update(MylarTaskListPlugin.getPrefs().getBoolean(TaskListPreferenceConstants.AUTO_MANAGE_EDITORS));
//	}
//
//	@Override
//	public void run() {
//		update(isChecked());
//	}
//
//	public void update(boolean on) {
//		setChecked(on);
//		MylarTaskListPlugin.getPrefs().setValue(TaskListPreferenceConstants.AUTO_MANAGE_EDITORS, on);
//
//		if (on) {
//			boolean previousValue = WorkbenchPlugin.getDefault().getPreferenceStore().getBoolean(
//					IPreferenceConstants.REUSE_EDITORS_BOOLEAN);
//			MylarTaskListPlugin.getPrefs().setValue(IPreferenceConstants.REUSE_EDITORS_BOOLEAN, previousValue);
//			WorkbenchPlugin.getDefault().getPreferenceStore().setValue(IPreferenceConstants.REUSE_EDITORS_BOOLEAN,
//					false);
//		} else {
//			boolean previousValue = MylarTaskListPlugin.getPrefs().getBoolean(
//					IPreferenceConstants.REUSE_EDITORS_BOOLEAN);
//			WorkbenchPlugin.getDefault().getPreferenceStore().setValue(IPreferenceConstants.REUSE_EDITORS_BOOLEAN,
//					previousValue);
//		}
//	}
//}
