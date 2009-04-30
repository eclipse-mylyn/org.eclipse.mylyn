/*******************************************************************************
 * Copyright (c) 2004, 2008 Willian Mitsuda and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Willian Mitsuda - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.workingsets;

import java.util.Comparator;

import org.eclipse.ui.IWorkingSet;

import com.ibm.icu.text.Collator;

/**
 * @author Willian Mitsuda
 */
public class WorkingSetLabelComparator implements Comparator<IWorkingSet> {

	public int compare(IWorkingSet ws1, IWorkingSet ws2) {
		return Collator.getInstance().compare(ws1.getLabel(), ws2.getLabel());
	}
}