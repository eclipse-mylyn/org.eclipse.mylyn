/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.ui;

import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;

/**
 * Enforces consistency between decorators.
 * 
 * @author Mik Kersten
 */
public abstract class AbstractTaskDecorator implements ILightweightLabelDecorator {

	/**
	 * Default location for the task kind decoration, used to distinguish
	 * between different tasks within a repository (e.g. defect vs.
	 * enhancement).
	 */
	protected int getQuadrantForKind() {
		return IDecoration.BOTTOM_RIGHT;
	}
}
