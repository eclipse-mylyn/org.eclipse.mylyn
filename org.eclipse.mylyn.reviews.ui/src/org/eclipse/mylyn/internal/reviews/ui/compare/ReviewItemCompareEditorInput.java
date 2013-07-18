/*******************************************************************************
 * Copyright (c) 2009 Atlassian and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Atlassian - initial API and implementation
 *     Tasktop Technologies - improvements
 *     Sebastien Dubois (Ericsson) - Improvements for bug 400266
 ******************************************************************************/

package org.eclipse.mylyn.internal.reviews.ui.compare;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.structuremergeviewer.Differencer;
import org.eclipse.compare.structuremergeviewer.ICompareInput;
import org.eclipse.compare.structuremergeviewer.IDiffElement;
import org.eclipse.compare.structuremergeviewer.StructureDiffViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.reviews.ui.ReviewBehavior;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Steffen Pingel
 * @author Sebastien Dubois
 * @author Miles Parker
 */
public abstract class ReviewItemCompareEditorInput extends CompareEditorInput {

	final ReviewBehavior behavior;

	public ReviewItemCompareEditorInput(CompareConfiguration configuration, ReviewBehavior behavior) {
		super(configuration);
		this.behavior = behavior;
	}

	@Override
	public Viewer findStructureViewer(Viewer oldViewer, ICompareInput input, Composite parent) {
		Viewer structureViewer = super.findStructureViewer(oldViewer, input, parent);
		if (structureViewer instanceof StructureDiffViewer) {
			StructureDiffViewer diffViewer = (StructureDiffViewer) structureViewer;
			diffViewer.setLabelProvider(new LabelProvider() {

				@Override
				public String getText(Object element) {
					if (element instanceof ITypedElement) {
						return ((ITypedElement) element).getName();
					}
					return "<no name>"; //$NON-NLS-1$
				}

				@Override
				public Image getImage(Object element) {
					if (element instanceof IDiffElement) {
						IDiffElement input = (IDiffElement) element;
						int kind = input.getKind();
						//We need to swap additions and deletions as work-around. See https://bugs.eclipse.org/bugs/show_bug.cgi?id=410534
						if (kind == Differencer.ADDITION) {
							kind = Differencer.DELETION;
						} else if (kind == Differencer.DELETION) {
							kind = Differencer.ADDITION;
						}
						return getCompareConfiguration().getImage(input.getImage(), kind);
					}
					return null;
				}
			});
		}
		return structureViewer;
	}

	@Override
	public Viewer findContentViewer(Viewer oldViewer, ICompareInput input, Composite parent) {
		Viewer contentViewer = super.findContentViewer(oldViewer, input, parent);
		if (input instanceof FileItemNode && ((FileItemNode) input).getFileItem() != null) {
			ReviewCompareAnnotationSupport support = ReviewCompareAnnotationSupport.getAnnotationSupport(contentViewer);
			support.setReviewItem(((FileItemNode) input).getFileItem(), behavior);
		}
		return contentViewer;
	}
}