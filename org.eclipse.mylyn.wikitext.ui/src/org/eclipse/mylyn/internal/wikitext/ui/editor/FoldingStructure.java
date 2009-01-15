/*******************************************************************************
 * Copyright (c) 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.ui.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.mylyn.wikitext.core.parser.outline.OutlineItem;

/**
 * 
 * @author David Green
 */
class FoldingStructure implements IFoldingStructure {

	private interface AnnotationOperation {
		public boolean operate(HeadingProjectionAnnotation annotation);
	}

	private static abstract class AbstractItemsAnnotationOperation implements AnnotationOperation {

		final Set<String> ids;

		protected AbstractItemsAnnotationOperation(Collection<OutlineItem> items) {
			ids = idsOf(items);
		}

		Set<String> idsOf(Collection<OutlineItem> items) {
			if (items == null || items.isEmpty()) {
				return Collections.emptySet();
			}
			Set<String> ids = new HashSet<String>();
			for (OutlineItem item : items) {
				ids.add(item.getId());
			}
			return ids;
		}

		public final boolean operate(HeadingProjectionAnnotation annotation) {
			if (ids.contains(annotation.getHeadingId())) {
				return operateOnSelected(annotation);
			} else {
				return operateOnUnselected(annotation);
			}
		}

		public abstract boolean operateOnSelected(HeadingProjectionAnnotation annotation);

		public boolean operateOnUnselected(HeadingProjectionAnnotation annotation) {
			return false;
		}
	}

	final ProjectionViewer viewer;

	final ITextOperationTarget textOperationTarget;

	public FoldingStructure(MarkupEditor editor) {
		viewer = (ProjectionViewer) editor.getViewer();
		textOperationTarget = (ITextOperationTarget) editor.getAdapter(ITextOperationTarget.class);
	}

	public void collapseAll() {
		if (!isFoldingEnabled()) {
			return;
		}
		textOperationTarget.doOperation(ProjectionViewer.COLLAPSE_ALL);
	}

	public void collapseElements(Collection<OutlineItem> items) {
		if (!isFoldingEnabled()) {
			return;
		}
		if (items == null || items.isEmpty()) {
			return;
		}
		operateOnAnnotations(new AbstractItemsAnnotationOperation(items) {
			@Override
			public boolean operateOnSelected(HeadingProjectionAnnotation annotation) {
				if (!annotation.isCollapsed()) {
					annotation.markCollapsed();
					return true;
				}
				return false;
			}
		});
	}

	public void expandAll() {
		if (!isFoldingEnabled()) {
			return;
		}
		textOperationTarget.doOperation(ProjectionViewer.EXPAND_ALL);
	}

	public void expandElements(Collection<OutlineItem> items) {
		if (!isFoldingEnabled()) {
			return;
		}
		if (items == null || items.isEmpty()) {
			return;
		}
		operateOnAnnotations(new AbstractItemsAnnotationOperation(items) {
			@Override
			public boolean operateOnSelected(HeadingProjectionAnnotation annotation) {
				if (annotation.isCollapsed()) {
					annotation.markExpanded();
					return true;
				}
				return false;
			}
		});
	}

	public void expandElementsExclusive(Collection<OutlineItem> items) {
		if (!isFoldingEnabled()) {
			return;
		}
		if (items == null || items.isEmpty()) {
			collapseAll();
			return;
		}
		operateOnAnnotations(new AbstractItemsAnnotationOperation(items) {
			@Override
			public boolean operateOnSelected(HeadingProjectionAnnotation annotation) {
				if (annotation.isCollapsed()) {
					annotation.markExpanded();
					return true;
				}
				return false;
			}

			@Override
			public boolean operateOnUnselected(HeadingProjectionAnnotation annotation) {
				if (!annotation.isCollapsed()) {
					annotation.markCollapsed();
					return true;
				}
				return false;
			}
		});
	}

	@SuppressWarnings("unchecked")
	public void operateOnAnnotations(AnnotationOperation operation) {
		if (!isFoldingEnabled()) {
			return;
		}
		ProjectionAnnotationModel annotationModel = viewer.getProjectionAnnotationModel();
		List<Annotation> modifications = null;
		Iterator<Annotation> iterator = annotationModel.getAnnotationIterator();
		while (iterator.hasNext()) {
			Annotation annotation = iterator.next();
			if (annotation instanceof HeadingProjectionAnnotation) {
				HeadingProjectionAnnotation projectionAnnotation = (HeadingProjectionAnnotation) annotation;
				if (operation.operate(projectionAnnotation)) {
					if (modifications == null) {
						modifications = new ArrayList<Annotation>();
					}
					modifications.add(projectionAnnotation);
				}
			}
		}
		if (modifications != null) {
			annotationModel.modifyAnnotations(null, null, modifications.toArray(new Annotation[modifications.size()]));
		}
	}

	public final boolean isFoldingEnabled() {
		return viewer.getProjectionAnnotationModel() != null;
	}
}