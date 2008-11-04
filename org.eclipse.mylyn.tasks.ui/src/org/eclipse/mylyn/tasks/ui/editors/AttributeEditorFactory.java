/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.ui.editors;

import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.internal.tasks.ui.editors.BooleanAttributeEditor;
import org.eclipse.mylyn.internal.tasks.ui.editors.DateAttributeEditor;
import org.eclipse.mylyn.internal.tasks.ui.editors.LongTextAttributeEditor;
import org.eclipse.mylyn.internal.tasks.ui.editors.MultiSelectionAttributeEditor;
import org.eclipse.mylyn.internal.tasks.ui.editors.PersonAttributeEditor;
import org.eclipse.mylyn.internal.tasks.ui.editors.RichTextAttributeEditor;
import org.eclipse.mylyn.internal.tasks.ui.editors.SingleSelectionAttributeEditor;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorExtensions;
import org.eclipse.mylyn.internal.tasks.ui.editors.TextAttributeEditor;
import org.eclipse.mylyn.internal.tasks.ui.editors.RepositoryTextViewerConfiguration.Mode;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint.ColumnSpan;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint.RowSpan;
import org.eclipse.swt.SWT;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.services.IServiceLocator;

/**
 * @since 3.0
 * @author Steffen Pingel
 */
public class AttributeEditorFactory {

	private final TaskDataModel model;

	private final TaskRepository taskRepository;

	private final IServiceLocator serviceLocator;

	private AttributeEditorToolkit editorToolkit;

	public AttributeEditorFactory(TaskDataModel model, TaskRepository taskRepository) {
		this(model, taskRepository, null);
	}

	/**
	 * @since 3.1
	 */
	public AttributeEditorFactory(TaskDataModel model, TaskRepository taskRepository, IServiceLocator serviceLocator) {
		Assert.isNotNull(model);
		Assert.isNotNull(taskRepository);
		this.model = model;
		this.taskRepository = taskRepository;
		this.serviceLocator = serviceLocator;
	}

	/**
	 * @since 3.1
	 */
	public AttributeEditorToolkit getEditorToolkit() {
		return editorToolkit;
	}

	/**
	 * @since 3.1
	 */
	public void setEditorToolkit(AttributeEditorToolkit editorToolkit) {
		this.editorToolkit = editorToolkit;
	}

	public AbstractAttributeEditor createEditor(String type, TaskAttribute taskAttribute) {
		Assert.isNotNull(type);

		if (TaskAttribute.TYPE_BOOLEAN.equals(type)) {
			return new BooleanAttributeEditor(model, taskAttribute);
		} else if (TaskAttribute.TYPE_DATE.equals(type)) {
			return new DateAttributeEditor(model, taskAttribute);
		} else if (TaskAttribute.TYPE_DATETIME.equals(type)) {
			DateAttributeEditor editor = new DateAttributeEditor(model, taskAttribute);
			editor.setShowTime(true);
			return editor;
		} else if (TaskAttribute.TYPE_PERSON.equals(type)) {
			return new PersonAttributeEditor(model, taskAttribute);
		} else if (TaskAttribute.TYPE_LONG_RICH_TEXT.equals(type)) {
			RichTextAttributeEditor editor = null;
			if (serviceLocator != null) {
				IContextService contextService = (IContextService) serviceLocator.getService(IContextService.class);
				if (contextService != null) {
					AbstractTaskEditorExtension extension = TaskEditorExtensions.getTaskEditorExtension(model.getTaskRepository());
					if (extension != null) {
						editor = new RichTextAttributeEditor(model, taskRepository, taskAttribute, SWT.MULTI,
								contextService, extension);
					}
				}
			}
			if (editor == null) {
				editor = new RichTextAttributeEditor(model, taskRepository, taskAttribute);
			}
			if (editorToolkit != null) {
				editor.setRenderingEngine(editorToolkit.getRenderingEngine(taskAttribute));
			}
			return editor;
		} else if (TaskAttribute.TYPE_LONG_TEXT.equals(type)) {
			return new LongTextAttributeEditor(model, taskAttribute);
		} else if (TaskAttribute.TYPE_MULTI_SELECT.equals(type)) {
			return new MultiSelectionAttributeEditor(model, taskAttribute);
		} else if (TaskAttribute.TYPE_SHORT_RICH_TEXT.equals(type)) {
			return new RichTextAttributeEditor(model, taskRepository, taskAttribute, SWT.SINGLE);
		} else if (TaskAttribute.TYPE_SHORT_TEXT.equals(type)) {
			return new TextAttributeEditor(model, taskAttribute);
		} else if (TaskAttribute.TYPE_SINGLE_SELECT.equals(type)) {
			return new SingleSelectionAttributeEditor(model, taskAttribute);
		} else if (TaskAttribute.TYPE_TASK_DEPENDENCY.equals(type)) {
			RichTextAttributeEditor editor = new RichTextAttributeEditor(model, taskRepository, taskAttribute,
					SWT.MULTI | TasksUiInternal.SWT_NO_SCROLL) {
				@Override
				public String getValue() {
					return getAttributeMapper().getValueLabel(getTaskAttribute());
				}
			};
			editor.setMode(Mode.TASK_RELATION);
			editor.setLayoutHint(new LayoutHint(RowSpan.SINGLE, ColumnSpan.SINGLE) {
				@Override
				public int getPriority() {
					return DEFAULT_PRIORITY + 1;
				}
			});
			return editor;
		} else if (TaskAttribute.TYPE_URL.equals(type)) {
			RichTextAttributeEditor editor = new RichTextAttributeEditor(model, taskRepository, taskAttribute,
					SWT.SINGLE);
			editor.setMode(Mode.URL);
			return editor;
		}

		throw new IllegalArgumentException("Unsupported editor type: \"" + type + "\"");
	}

}
