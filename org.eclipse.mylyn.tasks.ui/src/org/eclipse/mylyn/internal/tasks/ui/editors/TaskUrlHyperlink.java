/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.URLHyperlink;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;

/**
 * @author Rob Elves
 */
public class TaskUrlHyperlink extends URLHyperlink {

	public TaskUrlHyperlink(IRegion region, String urlString) {
		super(region, urlString);
	}

	@Override
	public void open() {
		TasksUiUtil.openTask(getURLString());
	}

}
