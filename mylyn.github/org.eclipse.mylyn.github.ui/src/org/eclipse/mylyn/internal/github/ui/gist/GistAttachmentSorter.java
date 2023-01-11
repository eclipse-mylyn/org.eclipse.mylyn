/*******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.github.ui.gist;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.mylyn.commons.core.CoreUtil;
import org.eclipse.mylyn.commons.ui.TableSorter;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;

/**
 * @author Kevin Sawicki (kevin@github.com)
 */
public class GistAttachmentSorter extends TableSorter {

	/**
	 * Compare
	 * org.eclipse.mylyn.internal.provisional.commons.ui.AbstractColumnViewerSorter#compare(org.eclipse.jface.viewers.ColumnViewer,
	 * java.lang.Object, java.lang.Object, int)
	 */
	@Override
	public int compare(TableViewer viewer, Object e1, Object e2, int columnIndex) {
		ITaskAttachment attachment1 = (ITaskAttachment) e1;
		ITaskAttachment attachment2 = (ITaskAttachment) e2;
		switch (columnIndex) {
		case 0:
			return CoreUtil.compare(attachment1.getFileName(), attachment2.getFileName());
		case 1:
			return CoreUtil.compare(Long.valueOf(attachment1.getLength()),
					Long.valueOf(attachment2.getLength()));
		case 2:
			return CoreUtil.compare(attachment1.getAuthor().toString(), attachment2
					.getAuthor().toString());
		default:
			return super.compare(viewer, e1, e2, columnIndex);
		}
	}

}
