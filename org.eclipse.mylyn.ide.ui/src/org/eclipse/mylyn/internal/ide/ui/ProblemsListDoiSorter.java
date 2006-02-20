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

package org.eclipse.mylar.internal.ide.ui;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylar.provisional.core.IMylarElement;
import org.eclipse.mylar.provisional.core.IMylarStructureBridge;
import org.eclipse.mylar.provisional.core.InterestComparator;
import org.eclipse.mylar.provisional.core.MylarPlugin;
import org.eclipse.ui.views.markers.internal.FieldFolder;
import org.eclipse.ui.views.markers.internal.FieldLineNumber;
import org.eclipse.ui.views.markers.internal.FieldMessage;
import org.eclipse.ui.views.markers.internal.FieldResource;
import org.eclipse.ui.views.markers.internal.FieldSeverity;
import org.eclipse.ui.views.markers.internal.IField;
import org.eclipse.ui.views.markers.internal.ProblemMarker;
import org.eclipse.ui.views.markers.internal.TableSorter;

/**
 * @author Mik Kersten
 */
public class ProblemsListDoiSorter extends TableSorter {

	// COPIED: from ProblemView
	private final static int ASCENDING = TableSorter.ASCENDING;

	private final static int DESCENDING = TableSorter.DESCENDING;

	private final static int SEVERITY = 0;

	private final static int DOI = 1;

	private final static int DESCRIPTION = 2;

	private final static int RESOURCE = 3;

	private final static int[] DEFAULT_PRIORITIES = { SEVERITY, DOI, DESCRIPTION, RESOURCE };

	private final static int[] DEFAULT_DIRECTIONS = { DESCENDING, // severity
			ASCENDING, // folder
			ASCENDING, // resource
			ASCENDING }; // location

	private final static IField[] VISIBLE_FIELDS = { new FieldSeverity(), new FieldMessage(), new FieldResource(),
			new FieldFolder(), new FieldLineNumber() };

	// END COPY

	public ProblemsListDoiSorter() {
		super(VISIBLE_FIELDS, DEFAULT_PRIORITIES, DEFAULT_DIRECTIONS);
	}

	protected InterestComparator<IMylarElement> interestComparator = new InterestComparator<IMylarElement>();

	@Override
	public int compare(Viewer viewer, Object obj1, Object obj2) {
		if (obj1 instanceof ProblemMarker && obj1 instanceof ProblemMarker) {
			ProblemMarker marker1 = (ProblemMarker) obj1;
			ProblemMarker marker2 = (ProblemMarker) obj2;
			if (marker1.getSeverity() == IMarker.SEVERITY_ERROR && marker2.getSeverity() < IMarker.SEVERITY_ERROR) {
				return -1;
			} else if (marker2.getSeverity() == IMarker.SEVERITY_ERROR
					&& marker1.getSeverity() < IMarker.SEVERITY_ERROR) {
				return 1;
			} else {
				if (MylarPlugin.getContextManager().isContextActive()) {
					IMylarStructureBridge bridge = MylarPlugin.getDefault().getStructureBridge(
							marker1.getResource().getFileExtension());
					IMylarElement node1 = MylarPlugin.getContextManager().getElement(
							bridge.getHandleForOffsetInObject(marker1, 0));
					IMylarElement node2 = MylarPlugin.getContextManager().getElement(
							bridge.getHandleForOffsetInObject(marker2, 0));
					return interestComparator.compare(node1, node2);
				}
			}
		}
		return super.compare(viewer, obj1, obj2);
	}
}
