/*******************************************************************************
 * Copyright (c) 2004, 2009 Willian Mitsuda and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
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