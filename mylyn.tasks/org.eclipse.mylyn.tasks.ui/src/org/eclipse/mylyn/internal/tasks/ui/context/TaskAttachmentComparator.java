/*******************************************************************************
 * Copyright (c) 2009, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.context;

import java.util.Comparator;
import java.util.Date;

import org.eclipse.mylyn.tasks.core.ITaskAttachment;

class TaskAttachmentComparator implements Comparator<ITaskAttachment> {
	public int compare(ITaskAttachment attachment1, ITaskAttachment attachment2) {
		Date created1 = null;
		Date created2 = null;
		created1 = attachment1.getCreationDate();
		created2 = attachment2.getCreationDate();
		if (created1 != null && created2 != null) {
			return (-1) * created1.compareTo(created2);
		} else if (created1 == null && created2 != null) {
			return 1;
		} else if (created1 != null && created2 == null) {
			return -1;
		} else {
			return 0;
		}
	}
}