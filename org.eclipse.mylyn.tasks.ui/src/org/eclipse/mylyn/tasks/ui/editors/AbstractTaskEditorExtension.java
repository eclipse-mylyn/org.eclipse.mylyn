/*******************************************************************************
 * Copyright (c) 2004, 2008 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.ui.editors;

import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.contexts.IContextService;

/**
 * An extension that provides task editor capabilities beyond the default, oriented towards providing markup-aware
 * editing and viewing
 * 
 * @author David Green
 * @since 3.1
 */
public abstract class AbstractTaskEditorExtension {

	/**
	 * The key to access the {@link TaskRepository} property that stores the URL of an associated wiki.
	 */
	public static final String INTERNAL_WIKI_LINK_PATTERN = "wikiLinkPattern"; //$NON-NLS-1$

	/**
	 * Creates a source viewer that can be used to view content in the task editor. The source viewer should be
	 * configured with a source viewer configuration prior to returning.
	 * 
	 * @param taskRepository
	 *            the task repository for which the viewer is created
	 * @param parent
	 *            the control parent of the source viewer
	 * @param style
	 *            the styles to use
	 */
	public abstract SourceViewer createViewer(TaskRepository taskRepository, Composite parent, int style);

	/**
	 * Creates a source viewer that can be used to edit content in the task editor. The source viewer should be
	 * configured with a source viewer configuration prior to returning.
	 * 
	 * @param taskRepository
	 *            the task repository for which the viewer is created
	 * @param parent
	 *            the control parent of the source viewer
	 * @param style
	 *            the styles to use
	 */
	public abstract SourceViewer createEditor(TaskRepository taskRepository, Composite parent, int style);

	/**
	 * Returns the editor context id, to be passed to the {@link IContextService} when the editor is in focus.
	 */
	public abstract String getEditorContextId();

}
