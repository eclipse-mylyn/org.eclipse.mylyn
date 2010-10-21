/*********************************************************************
 * Copyright (c) 2010 Sony Ericsson/ST Ericsson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *      Sony Ericsson/ST Ericsson - initial API and implementation
 *********************************************************************/
package org.eclipse.mylyn.gerrit.ui.editor;

import org.eclipse.mylyn.gerrit.core.GerritConnector;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPageFactory;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorInput;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.forms.editor.IFormPage;



/**
 * @author Mikael Kober, Sony Ericsson
 * @author Tomas Westling, Sony Ericsson -
 *         thomas.westling@sonyericsson.com
 */
public class GerritTaskEditorPageFactory extends AbstractTaskEditorPageFactory {

	@Override
	public boolean canCreatePageFor(TaskEditorInput input) {
		return  (input.getTask().getConnectorKind().equals(GerritConnector.CONNECTOR_KIND)) || 
			(TasksUiUtil.isOutgoingNewTask(input.getTask(), GerritConnector.CONNECTOR_KIND));
	}

	@Override
	public IFormPage createPage(TaskEditor parentEditor) {
		return new GerritTaskEditorPage(parentEditor);
	}

	@Override
	public int getPriority() {
		return PRIORITY_TASK;
	}
	
	@Override
	public Image getPageImage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPageText() {
		return "Gerrit";
	}


}
