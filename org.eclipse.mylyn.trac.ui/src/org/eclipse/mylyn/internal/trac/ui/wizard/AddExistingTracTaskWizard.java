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
package org.eclipse.mylar.internal.trac.ui.wizard;

import org.eclipse.mylar.internal.tasklist.ui.wizards.AbstractAddExistingTaskWizard;
import org.eclipse.mylar.provisional.tasklist.TaskRepository;

/**
 * @author Steffen Pingel
 */
public class AddExistingTracTaskWizard extends AbstractAddExistingTaskWizard {

	public AddExistingTracTaskWizard(TaskRepository repository) {
		super(repository);
	}

}
