/*******************************************************************************
 * Copyright (c) 2004, 2014 Tasktop Technologies and others.
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

package org.eclipse.mylyn.tasks.ui.editors;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.mylyn.internal.tasks.ui.editors.BooleanAttributeEditor;
import org.eclipse.mylyn.internal.tasks.ui.editors.DateAttributeEditor;
import org.eclipse.mylyn.internal.tasks.ui.editors.DoubleAttributeEditor;
import org.eclipse.mylyn.internal.tasks.ui.editors.IntegerAttributeEditor;
import org.eclipse.mylyn.internal.tasks.ui.editors.LabelsAttributeEditor;
import org.eclipse.mylyn.internal.tasks.ui.editors.LastCommentedAttributeEditor;
import org.eclipse.mylyn.internal.tasks.ui.editors.LongAttributeEditor;
import org.eclipse.mylyn.internal.tasks.ui.editors.LongTextAttributeEditor;
import org.eclipse.mylyn.internal.tasks.ui.editors.MultiSelectionAttributeEditor;
import org.eclipse.mylyn.internal.tasks.ui.editors.PersonAttributeEditor;
import org.eclipse.mylyn.internal.tasks.ui.editors.RepositoryTextViewerConfiguration.Mode;
import org.eclipse.mylyn.internal.tasks.ui.editors.RichTextAttributeEditor;
import org.eclipse.mylyn.internal.tasks.ui.editors.SingleSelectionAttributeEditor;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorExtensions;
import org.eclipse.mylyn.internal.tasks.ui.editors.TextAttributeEditor;
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

	public AttributeEditorFactory(@NonNull TaskDataModel model, @NonNull TaskRepository taskRepository) {
		this(model, taskRepository, null);
	}

	/**
	 * @since 3.1
	 */
	public AttributeEditorFactory(@NonNull TaskDataModel model, @NonNull TaskRepository taskRepository,
			@Nullable IServiceLocator serviceLocator) {
		Assert.isNotNull(model);
		Assert.isNotNull(taskRepository);
		this.model = model;
		this.taskRepository = taskRepository;
		this.serviceLocator = serviceLocator;
	}

	/**
	 * @since 3.1
	 */
	@Nullable
	public AttributeEditorToolkit getEditorToolkit() {
		return editorToolkit;
	}

	/**
	 * @since 3.1
	 */
	public void setEditorToolkit(@Nullable AttributeEditorToolkit editorToolkit) {
		this.editorToolkit = editorToolkit;
	}

	@NonNull
	public AbstractAttributeEditor createEditor(@NonNull String type, @NonNull TaskAttribute taskAttribute) {
		Assert.isNotNull(type);
		Assert.isNotNull(taskAttribute);

		if (TaskAttribute.TYPE_BOOLEAN.equals(type)) {
			return new BooleanAttributeEditor(model, taskAttribute);
		} else if (TaskAttribute.TYPE_DATE.equals(type)) {
			return new DateAttributeEditor(model, taskAttribute);
		} else if (TaskAttribute.TYPE_DATETIME.equals(type)) {
			if (taskAttribute.getParentAttribute() != null
					&& TaskAttribute.TYPE_COMMENT.equals(taskAttribute.getParentAttribute().getMetaData().getType())) {
				LastCommentedAttributeEditor editor = new LastCommentedAttributeEditor(model, taskAttribute);
				return editor;
			}
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
					AbstractTaskEditorExtension extension = TaskEditorExtensions
							.getTaskEditorExtension(model.getTaskRepository(), taskAttribute);
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
					SWT.MULTI | SWT.NO_SCROLL) {
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
		} else if (TaskAttribute.TYPE_DOUBLE.equals(type)) {
			return new DoubleAttributeEditor(model, taskAttribute);
		} else if (TaskAttribute.TYPE_INTEGER.equals(type)) {
			return new IntegerAttributeEditor(model, taskAttribute);
		} else if (TaskAttribute.TYPE_LONG.equals(type)) {
			return new LongAttributeEditor(model, taskAttribute);
		} else if (TaskAttribute.TYPE_LABEL.equals(type) || TaskAttribute.TYPE_MULTI_LABEL.equals(type)) {
			return new LabelsAttributeEditor(model, taskAttribute);
		}

		throw new IllegalArgumentException("Unsupported editor type: \"" + type + "\""); //$NON-NLS-1$ //$NON-NLS-2$
	}

}
