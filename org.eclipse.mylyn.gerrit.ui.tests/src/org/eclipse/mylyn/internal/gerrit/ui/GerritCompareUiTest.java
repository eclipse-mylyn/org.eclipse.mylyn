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

package org.eclipse.mylyn.internal.gerrit.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.mylyn.internal.reviews.ui.compare.FileItemCompareEditorInput;
import org.eclipse.mylyn.internal.reviews.ui.compare.ReviewItemSetCompareEditorInput;
import org.eclipse.mylyn.reviews.core.model.IFileItem;
import org.eclipse.mylyn.reviews.core.model.IFileVersion;
import org.eclipse.mylyn.reviews.core.model.IReviewItemSet;
import org.eclipse.mylyn.reviews.core.model.IReviewsFactory;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.PartInitException;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("restriction")
public class GerritCompareUiTest {

	private static final IEditorReference[] EMPTY_IEDITOR_REFERENCE_ARRAY = new IEditorReference[0];

	private static final String FILE_ITEM_ID = "Test file item ID";

	private static final String REVIEW_ITEM_SET_ID = "Test review item set ID";

	private static final String TEST_TASK_ID = "Test task ID";

	private static final String COMPARE_EDITOR_TITLE = "Compare Editor Title";

	private static final String COMPARE_EDITOR = "org.eclipse.compare.CompareEditor";

	private List<IEditorReference> editorReferences;

	private FileItemCompareEditorInput fileItemEditorInput;

	private ReviewItemSetCompareEditorInput reviewItemSetEditorInput;

	private IFileItem item;

	private IReviewItemSet items;

	@Before
	public void setUp() throws PartInitException {
		editorReferences = new ArrayList<IEditorReference>();
		editorReferences.add(getMockEditor("org.eclipse.other.Editor"));

		item = IReviewsFactory.INSTANCE.createFileItem();
		item.setId(FILE_ITEM_ID);
		item.setBase(getIFileVersion("Base Test Description"));
		item.setTarget(getIFileVersion("Target Test Description"));

		items = IReviewsFactory.INSTANCE.createReviewItemSet();
		items.setName(REVIEW_ITEM_SET_ID);

		GerritReviewBehavior behavior = new GerritReviewBehavior(mock(ITask.class));
		CompareConfiguration configuration = new CompareConfiguration();
		fileItemEditorInput = new FileItemCompareEditorInput(configuration, item, behavior);
		reviewItemSetEditorInput = new ReviewItemSetCompareEditorInput(configuration, items, null, behavior);
	}

	private IFileVersion getIFileVersion(String description) {
		IFileVersion fileVersion = IReviewsFactory.INSTANCE.createFileVersion();
		fileVersion.setDescription(description);
		return fileVersion;
	}

	@Test
	public void testNoOpenCompareEditors() {
		CompareEditorInput newInput = GerritCompareUi.getComparisonEditorInput(
				editorReferences.toArray(EMPTY_IEDITOR_REFERENCE_ARRAY), fileItemEditorInput,
				GerritCompareUi.getFileComparePredicate(item));
		assertEquals(newInput, fileItemEditorInput);

		newInput = GerritCompareUi.getComparisonEditorInput(editorReferences.toArray(EMPTY_IEDITOR_REFERENCE_ARRAY),
				reviewItemSetEditorInput, GerritCompareUi.getReviewItemSetComparePredicate(items, TEST_TASK_ID));
		assertEquals(newInput, reviewItemSetEditorInput);
	}

	@Test
	public void testOpenNewFileCompareEditor() throws Exception {
		editorReferences.add(getMockFileCompareEditor());
		item.setId("New File Item ID");

		CompareEditorInput newInput = GerritCompareUi.getComparisonEditorInput(
				editorReferences.toArray(EMPTY_IEDITOR_REFERENCE_ARRAY), fileItemEditorInput,
				GerritCompareUi.getFileComparePredicate(item));
		assertEquals(newInput, fileItemEditorInput);
	}

	@Test
	public void testOpenNewReviewItemSetCompareEditor() throws Exception {
		editorReferences.add(getMockReviewItemSetCompareEditor());
		items.setName("New Review Item ID");

		CompareEditorInput newInput = GerritCompareUi.getComparisonEditorInput(
				editorReferences.toArray(EMPTY_IEDITOR_REFERENCE_ARRAY), reviewItemSetEditorInput,
				GerritCompareUi.getReviewItemSetComparePredicate(items, TEST_TASK_ID));

		assertEquals(newInput, reviewItemSetEditorInput);

		items.setName(REVIEW_ITEM_SET_ID);

		newInput = GerritCompareUi.getComparisonEditorInput(editorReferences.toArray(EMPTY_IEDITOR_REFERENCE_ARRAY),
				reviewItemSetEditorInput, GerritCompareUi.getReviewItemSetComparePredicate(items, "New Test Task Id"));

		assertEquals(newInput, reviewItemSetEditorInput);
	}

	@Test
	public void testOpenExistingFileCompareEditor() throws Exception {
		editorReferences.add(getMockFileCompareEditor());

		CompareEditorInput newInput = GerritCompareUi.getComparisonEditorInput(
				editorReferences.toArray(EMPTY_IEDITOR_REFERENCE_ARRAY), fileItemEditorInput,
				GerritCompareUi.getFileComparePredicate(item));

		assertTrue(newInput != fileItemEditorInput);
		assertEquals(COMPARE_EDITOR_TITLE, newInput.getTitle());
	}

	@Test
	public void testOpenExistingReviewItemSetCompareEditor() throws Exception {
		editorReferences.add(getMockReviewItemSetCompareEditor());

		CompareEditorInput newInput = GerritCompareUi.getComparisonEditorInput(
				editorReferences.toArray(EMPTY_IEDITOR_REFERENCE_ARRAY), reviewItemSetEditorInput,
				GerritCompareUi.getReviewItemSetComparePredicate(items, TEST_TASK_ID));

		assertTrue(newInput != reviewItemSetEditorInput);
		assertEquals(COMPARE_EDITOR_TITLE, newInput.getTitle());
	}

	private IEditorReference getMockEditor(String id) {
		IEditorReference ref = mock(IEditorReference.class);
		when(ref.getId()).thenReturn(id);
		return ref;
	}

	private IEditorReference getMockFileCompareEditor() throws PartInitException {
		FileItemCompareEditorInput input = mock(FileItemCompareEditorInput.class);
		when(input.getFileItemId()).thenReturn(FILE_ITEM_ID);
		when(input.getTitle()).thenReturn(COMPARE_EDITOR_TITLE);
		IEditorReference ref = getMockEditor(COMPARE_EDITOR);
		when(ref.getEditorInput()).thenReturn(input);
		return ref;
	}

	private IEditorReference getMockReviewItemSetCompareEditor() throws PartInitException {
		ReviewItemSetCompareEditorInput input = mock(ReviewItemSetCompareEditorInput.class);
		when(input.getName()).thenReturn(REVIEW_ITEM_SET_ID);
		when(input.getTitle()).thenReturn(COMPARE_EDITOR_TITLE);
		when(input.getItemTaskId()).thenReturn(TEST_TASK_ID);
		IEditorReference ref = getMockEditor(COMPARE_EDITOR);
		when(ref.getEditorInput()).thenReturn(input);
		return ref;
	}
}
