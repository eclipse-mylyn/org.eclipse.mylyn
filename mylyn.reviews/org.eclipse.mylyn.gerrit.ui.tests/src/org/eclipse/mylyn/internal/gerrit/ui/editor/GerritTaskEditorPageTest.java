/*******************************************************************************
 * Copyright (c) 2015 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.ui.editor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.internal.gerrit.core.GerritConnector;
import org.eclipse.mylyn.internal.gerrit.core.GerritQueryResultSchema;
import org.eclipse.mylyn.internal.gerrit.ui.editor.GerritTaskEditorPage.GerritAttributePart;
import org.eclipse.mylyn.internal.tasks.core.TaskTask;
import org.eclipse.mylyn.internal.tasks.ui.editors.PersonAttributeEditor;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorCommentPart;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorNewCommentPart;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorInput;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorPartDescriptor;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.handlers.IHandlerService;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import junit.framework.TestCase;

@SuppressWarnings("restriction")
public class GerritTaskEditorPageTest extends TestCase {

	private static class TestGerritTaskEditorPage extends GerritTaskEditorPage {

		private final TaskDataModel model;

		public TestGerritTaskEditorPage() {
			super(mock(TaskEditor.class));
			model = mock(TaskDataModel.class);
			TaskRepository repository = new TaskRepository(GerritConnector.CONNECTOR_KIND, "mock");
			TaskData taskData = new TaskData(new TaskAttributeMapper(repository), GerritConnector.CONNECTOR_KIND,
					"mock", "mock");
			taskData.getRoot().createAttribute(GerritQueryResultSchema.getDefault().BRANCH.getKey());
			taskData.getRoot().createAttribute(GerritQueryResultSchema.getDefault().PROJECT.getKey());
			taskData.getRoot().createAttribute(GerritQueryResultSchema.getDefault().STATUS.getKey());
			when(model.getTaskData()).thenReturn(taskData);
			when(model.getTaskRepository()).thenReturn(repository);

			IEditorSite editorSite = mock(IEditorSite.class);
			IHandlerService service = mock(IHandlerService.class);
			when(editorSite.getService(IHandlerService.class)).thenReturn(service);
			TaskEditorInput taskEditorInput = new TaskEditorInput(repository,
					new TaskTask(GerritConnector.CONNECTOR_KIND, "mock", "mock"));
			when(getTaskEditor().getTaskEditorInput()).thenReturn(taskEditorInput);
			init(editorSite, taskEditorInput);
		}

		@Override
		protected TaskDataModel createModel(TaskEditorInput input) throws CoreException {
			return model;
		}

		@Override
		public TaskDataModel getModel() {
			return model;
		}
	}

	private GerritTaskEditorPage page;

	private ArrayList<TaskEditorPartDescriptor> descriptors;

	@Override
	protected void setUp() throws Exception {
		page = new TestGerritTaskEditorPage();
		descriptors = new ArrayList<TaskEditorPartDescriptor>(page.createPartDescriptors());
	}

	public void testCreatePartDescriptorsCustomOrder() {
		Iterable<Class<?>> partClasses = ImmutableList
				.copyOf(Iterables.transform(descriptors.subList(descriptors.size() - 4, descriptors.size()),
						new Function<TaskEditorPartDescriptor, Class<?>>() {
							public Class<?> apply(TaskEditorPartDescriptor o) {
								return o.createPart().getClass();
							}
						}));
		assertEquals(ImmutableList.of(GerritReviewDetailSection.class, PatchSetSection.class,
				TaskEditorCommentPart.class, TaskEditorNewCommentPart.class), partClasses);
		List<String> ids = Lists.transform(descriptors, new Function<TaskEditorPartDescriptor, String>() {
			public String apply(TaskEditorPartDescriptor o) {
				return o.getId();
			}
		});
		assertTrue("Missing descriptors. Found " + ids, descriptors.size() >= 7);
	}

	public void testCreateAttributesSectionOverlayAttributes() {
		TaskEditorPartDescriptor descriptor = findById(descriptors, AbstractTaskEditorPage.ID_PART_ATTRIBUTES).get();
		AbstractTaskEditorPart part = descriptor.createPart();
		part.initialize(page);
		assertThat(part, instanceOf(GerritAttributePart.class));
		assertEquals(AbstractTaskEditorPage.PATH_ATTRIBUTES, descriptor.getPath());
		List<TaskAttribute> overlayAttributes = ((GerritAttributePart) part).getOverlayAttributes();
		assertEquals(2, overlayAttributes.size());
		assertEquals(GerritQueryResultSchema.getDefault().PROJECT.getKey(), overlayAttributes.get(0).getId());
		assertEquals(GerritQueryResultSchema.getDefault().BRANCH.getKey(), overlayAttributes.get(1).getId());
	}

	public void testRemoveUnneededSections() {
		Optional<TaskEditorPartDescriptor> descriptor = findByPath(descriptors, AbstractTaskEditorPage.PATH_ACTIONS);
		assertFalse(descriptor.isPresent());
		descriptor = findByPath(descriptors, AbstractTaskEditorPage.PATH_PEOPLE);
		assertFalse(descriptor.isPresent());
	}

	private Optional<TaskEditorPartDescriptor> findByPath(ArrayList<TaskEditorPartDescriptor> descriptors,
			final String path) {
		return Iterables.tryFind(descriptors, new Predicate<TaskEditorPartDescriptor>() {
			public boolean apply(TaskEditorPartDescriptor descriptor) {
				return descriptor.getPath().equals(path);
			}
		});
	}

	private Optional<TaskEditorPartDescriptor> findById(ArrayList<TaskEditorPartDescriptor> descriptors,
			final String id) {
		return Iterables.tryFind(descriptors, new Predicate<TaskEditorPartDescriptor>() {
			public boolean apply(TaskEditorPartDescriptor descriptor) {
				return descriptor.getId().equals(id);
			}
		});
	}

	public void testCreatePersonAttribute() throws Exception {
		TaskAttribute assigneeAttribute = page.getModel()
				.getTaskData()
				.getRoot()
				.createAttribute(TaskAttribute.USER_ASSIGNED);
		assigneeAttribute.getMetaData().setReadOnly(true);
		PersonAttributeEditor editor = (PersonAttributeEditor) page.createAttributeEditorFactory()
				.createEditor(TaskAttribute.TYPE_PERSON, assigneeAttribute);
		assertTrue(editor.isReadOnly());

		assigneeAttribute.setValue("joel.user");
		assertEquals("joel.user", editor.getValue());

		assigneeAttribute.putOption("joel.user", "Joel K. User");
		assertEquals("Joel K. User", editor.getValue());

		editor.setReadOnly(false);
		assertEquals("joel.user", editor.getValue());
	}
}
