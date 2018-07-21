/*******************************************************************************
 * Copyright (c) 2015 Tasktop Technologies and others.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.mylyn.commons.workbench.WorkbenchUtil;
import org.eclipse.mylyn.commons.workbench.editors.CommonTextSupport;
import org.eclipse.mylyn.internal.tasks.ui.OptionsProposalProvider;
import org.eclipse.mylyn.internal.tasks.ui.PersonProposalProvider;
import org.eclipse.mylyn.internal.tasks.ui.editors.LabelsAttributeEditor;
import org.eclipse.mylyn.internal.tasks.ui.editors.MultiSelectionAttributeEditor;
import org.eclipse.mylyn.internal.tasks.ui.editors.SingleSelectionAttributeEditor;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.tests.TaskTestUtil;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.fieldassist.ContentAssistCommandAdapter;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

public class AttributeEditorToolkitTest {
	public class TestAttributeEditorToolkit extends AttributeEditorToolkit {

		private ContentAssistCommandAdapter commandAdapter;

		TestAttributeEditorToolkit(CommonTextSupport textSupport) {
			super(textSupport);
		}

		@Override
		public ContentAssistCommandAdapter createContentAssistCommandAdapter(Control control,
				IContentProposalProvider proposalProvider) {
			commandAdapter = super.createContentAssistCommandAdapter(control, proposalProvider);
			return commandAdapter;
		}

		@Override
		public IContentProposalProvider createContentProposalProvider(AbstractAttributeEditor editor) {
			return super.createContentProposalProvider(editor);
		}
	}

	private TestAttributeEditorToolkit toolkit;

	private TaskAttribute taskAttribute;

	private final FormToolkit formToolkit = new FormToolkit(Display.getCurrent());

	private final TaskDataModel taskDataModel = mock(TaskDataModel.class);

	@SuppressWarnings("restriction")
	@Before
	public void setUp() {
		CommonTextSupport textSupport = mock(CommonTextSupport.class);
		toolkit = spy(new TestAttributeEditorToolkit(textSupport));
		TaskData taskData = TaskTestUtil.createMockTaskData("1");
		taskAttribute = taskData.getRoot();
		when(taskDataModel.getTaskData()).thenReturn(taskData);
	}

	@After
	public void tearDown() {
		formToolkit.dispose();
	}

	@Test
	public void testAdaptSingleSelectionAttributeEditor() {
		SingleSelectionAttributeEditor editor = new SingleSelectionAttributeEditor(taskDataModel, taskAttribute);
		assertNoOptionsProposalProvider(editor);
	}

	@Test
	public void testAdaptMultiSelectionAttributeEditor() {
		MultiSelectionAttributeEditor editor = new MultiSelectionAttributeEditor(taskDataModel, taskAttribute);
		assertNoOptionsProposalProvider(editor);
	}

	private void assertNoOptionsProposalProvider(AbstractAttributeEditor editor) {
		editor.createControl(WorkbenchUtil.getShell(), formToolkit);
		toolkit.adapt(editor);
		verify(toolkit, never()).createContentProposalProvider(any(AbstractAttributeEditor.class));
		verify(toolkit, never()).createContentAssistCommandAdapter(any(Control.class),
				any(IContentProposalProvider.class));
	}

	@Test
	public void testAdaptLabelsAttributeEditor() {
		assertOptionsProposalProvider(true, false);
		assertOptionsProposalProvider(false, false);
		assertOptionsProposalProvider(true, true);
	}

	private void assertOptionsProposalProvider(boolean isMultiSelect, boolean isPerson) {
		taskAttribute.getMetaData().setKind(isPerson ? TaskAttribute.KIND_PEOPLE : TaskAttribute.KIND_DEFAULT);
		taskAttribute.getMetaData()
				.setType(isMultiSelect ? TaskAttribute.TYPE_MULTI_SELECT : TaskAttribute.TYPE_SINGLE_SELECT);
		LabelsAttributeEditor editor = new LabelsAttributeEditor(taskDataModel, taskAttribute);
		editor.createControl(WorkbenchUtil.getShell(), formToolkit);
		toolkit.adapt(editor);

		verify(toolkit).createContentProposalProvider(editor);
		ArgumentCaptor<IContentProposalProvider> providerCaptor = ArgumentCaptor
				.forClass(IContentProposalProvider.class);
		verify(toolkit).createContentAssistCommandAdapter(eq(editor.getControl()), providerCaptor.capture());
		IContentProposalProvider proposalProvider = providerCaptor.getValue();
		if (isPerson) {
			assertTrue(proposalProvider instanceof PersonProposalProvider);
		} else {
			assertTrue(proposalProvider instanceof OptionsProposalProvider);
			assertEquals(isMultiSelect, ((OptionsProposalProvider) proposalProvider).isMultiSelect());
		}
		assertNull(toolkit.commandAdapter.getAutoActivationCharacters());
	}

}
