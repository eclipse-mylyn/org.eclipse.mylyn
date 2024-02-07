/*******************************************************************************
1 * Copyright (c) 2012, 2022 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     ArSysOp - adapt to SimRel 2022-12
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.eclipse.mylyn.commons.workbench.EditorHandle;
import org.eclipse.mylyn.internal.gerrit.core.GerritConnector;
import org.eclipse.mylyn.internal.gerrit.ui.editor.GerritTaskEditorPage;
import org.eclipse.mylyn.reviews.core.model.IFileItem;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.core.model.IReviewItemSet;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Steffen Pingel
 */
//FIXME: AF: enable tests
//https://github.com/eclipse-mylyn/org.eclipse.mylyn.reviews/issues/5
@Ignore
@SuppressWarnings("nls")
public class GerritUrlHandlerTest {

	private final TaskRepository repository = new TaskRepository(GerritConnector.CONNECTOR_KIND,
			"http://review.mylyn.org");

	private final GerritUrlHandler handler = new GerritUrlHandler();

	private IWorkbenchPage page;

	private String taskId;

	private int patchSetNumber;

	private String path;

	@Test
	public void testGetTaskId() {
		assertEquals("123", handler.getTaskId(repository, "http://review.mylyn.org/123"));
	}

	@Test
	public void testGetTaskIdTrailingSlashAfterId() {
		assertEquals("123", handler.getTaskId(repository, "http://review.mylyn.org/123/foo/bar"));
	}

	@Test
	public void testGetTaskIdInvalidId() {
		assertEquals(null, handler.getTaskId(repository, "http://review.mylyn.org/ab123"));
	}

	@Test
	public void testGetTaskIdRepositoryMismatch() {
		assertEquals(null, handler.getTaskId(repository, "http://mylyn.org/reviews/123"));
	}

	@Test
	public void testGetTaskIdSubPath() {
		assertEquals("123", handler.getTaskId(repository, "http://review.mylyn.org/123"));
	}

	@Test
	public void testGetTaskIdTrailingSlash() {
		TaskRepository trailingSlashRepository = new TaskRepository(GerritConnector.CONNECTOR_KIND,
				"http://review.mylyn.org/");
		assertEquals("123", handler.getTaskId(trailingSlashRepository, "http://review.mylyn.org/123"));
	}

	@Test
	public void testGetTaskIdAbsolute() {
		assertEquals("123", handler.getTaskId(repository, "http://review.mylyn.org/#/c/123"));
	}

	@Test
	public void testGetTaskIdLetters() {
		assertEquals(null, handler.getTaskId(repository, "http://review.mylyn.org/#/c/abc/"));
	}

	@Test
	public void testGetTaskIdEmpty() {
		assertEquals(null, handler.getTaskId(repository, "http://review.mylyn.org/#/c//"));
	}

	@Test
	public void testGetTaskIdAbsoluteTrailingSlash() {
		assertEquals("123", handler.getTaskId(repository, "http://review.mylyn.org/#/c/123/"));
	}

	@Test
	public void testGetTaskIdPatchSet() {
		assertEquals("4698", handler.getTaskId(repository, "http://review.mylyn.org/#/c/4698/5"));
	}

	@Test
	public void testGetTaskIdFile() {
		assertEquals("4698", handler.getTaskId(repository, "http://review.mylyn.org/#/c/4698/5/foo/bar"));
	}

	@Test
	public void testGetPatchSetNumberPatchSet() {
		String url = "http://review.mylyn.org/#/c/4698/5";
		taskId = handler.getTaskId(repository, url);
		assertEquals(5, handler.getPatchSetNumber(repository, url, taskId));
	}

	@Test
	public void testGetPatchSetNumberPatchSetTrailingSlash() {
		String url = "http://review.mylyn.org/#/c/4698/5/";
		taskId = handler.getTaskId(repository, url);
		assertEquals(5, handler.getPatchSetNumber(repository, url, taskId));
	}

	@Test
	public void testGetPatchSetNumberPatchSetFile() {
		String url = "http://review.mylyn.org/#/c/4698/5/foo/bar";
		taskId = handler.getTaskId(repository, url);
		assertEquals(5, handler.getPatchSetNumber(repository, url, taskId));
	}

	@Test
	public void testGetPatchSetNumberNoneSpecified() {
		String url = "http://review.mylyn.org/#/c/4698";
		taskId = handler.getTaskId(repository, url);
		assertEquals(-1, handler.getPatchSetNumber(repository, url, taskId));
	}

	@Test
	public void testGetPatchSetNumberNoneSpecifiedTrailingSlash() {
		String url = "http://review.mylyn.org/#/c/4698/";
		taskId = handler.getTaskId(repository, url);
		assertEquals(-1, handler.getPatchSetNumber(repository, url, taskId));
	}

	@Test
	public void testGetPatchSetNumberNoneSpecifiedNotAnInteger() {
		String url = "http://review.mylyn.org/#/c/A1";
		taskId = handler.getTaskId(repository, url);
		assertEquals(-1, handler.getPatchSetNumber(repository, url, taskId));
	}

	@Test
	public void testGetPathNoneSpecified() {
		assertPath(null, "http://review.mylyn.org/#/c/4698/5");
	}

	@Test
	public void testGetPathNoneSpecifiedTrailingSlash() {
		assertPath(null, "http://review.mylyn.org/#/c/4698/5/");
	}

	@Test
	public void testGetPathNoneSpecifiedInvalidPatchNumber() {
		assertPath(null, "http://review.mylyn.org/#/c/4698/-1abcd");
	}

	@Test
	public void testGetPathNoneSpecifiedInvalidPatchNumberTrailingSlash() {
		assertPath(null, "http://review.mylyn.org/#/c/4698/-1abcd/");
	}

	@Test
	public void testGetPath() {
		assertPath("foo/bar.java", "http://review.mylyn.org/#/c/4698/5/foo/bar.java");
	}

	@Test
	public void testGetPathWithTrailingSlash() {
		assertPath("foo/bar.java", "http://review.mylyn.org/#/c/4698/5/foo/bar.java/");
	}

	@Test
	public void testOpenUrlWithInvalidReview() throws Exception {
		String url = "http://review.mylyn.org/#/c/4698/1/foo/bar.java";
		GerritUrlHandler spy = setUpOpenUrlTests(url);
		doReturn(null).when(spy).revealPatchSet(any(EditorHandle.class), anyInt());

		spy.openUrl(page, url, 0);

		verify(spy, times(1)).revealPatchSet(any(EditorHandle.class), anyInt());
		verify(spy, never()).getFileItem(any(IReview.class), anyInt(), anyString());
		verify(spy, never()).openCompareEditor(null);
	}

	@Test
	public void testOpenUrlWithValidPathOpenReview() throws Exception {
		String url = "http://review.mylyn.org/#/c/4698/1/foo/bar.java";
		GerritUrlHandler spy = setUpOpenUrlTests(url);
		GerritTaskEditorPage page = mock(GerritTaskEditorPage.class);
		IReview review = createMockReview();
		when(page.getReview()).thenReturn(review);
		doReturn(page).when(spy).revealPatchSet(any(EditorHandle.class), anyInt());
		openReviewTest(spy, url);

		verify(spy, times(1)).getFileItem(review, 1, "foo/bar.java");
		assertEquals(path, spy.getFileItem(review, patchSetNumber, path).getName());
	}

	@Test
	public void testOpenUrlWithInvalidPatchSetNumberOpenReview() throws Exception {
		String url = "http://review.mylyn.org/#/c/4698/5/foo/bar.java";
		GerritUrlHandler spy = setUpOpenUrlTests(url);
		GerritTaskEditorPage page = mock(GerritTaskEditorPage.class);
		IReview review = createMockReview();
		when(page.getReview()).thenReturn(review);
		doReturn(page).when(spy).revealPatchSet(any(EditorHandle.class), anyInt());
		openReviewTest(spy, url);

		verify(spy, times(1)).getFileItem(review, 5, "foo/bar.java");
		assertNull(spy.getFileItem(review, patchSetNumber, path));
	}

	@Test
	public void testOpenUrlWithInvalidPathOpenReview() throws Exception {
		String url = "http://review.mylyn.org/#/c/4698/1/foo/bar.jav";
		GerritUrlHandler spy = setUpOpenUrlTests(url);
		GerritTaskEditorPage page = mock(GerritTaskEditorPage.class);
		IReview review = createMockReview();
		when(page.getReview()).thenReturn(review);
		doReturn(page).when(spy).revealPatchSet(any(EditorHandle.class), anyInt());
		openReviewTest(spy, url);

		verify(spy, times(1)).getFileItem(review, 1, "foo/bar.jav");
		assertNull(spy.getFileItem(review, patchSetNumber, path));
	}

	private GerritUrlHandler setUpOpenUrlTests(String url) {
		page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		GerritUrlHandler spy = spy(handler);
		TasksUi.getRepositoryManager().addRepository(repository);
		taskId = spy.getTaskId(repository, url);
		patchSetNumber = spy.getPatchSetNumber(repository, url, taskId);
		path = spy.getFilePath(repository, url, taskId, patchSetNumber);

		return spy;
	}

	private IReview createMockReview() {
		IReview mockReview = mock(IReview.class);
		IReviewItemSet mockSet = mock(IReviewItemSet.class);
		IFileItem mockFile = mock(IFileItem.class);

		when(mockFile.getName()).thenReturn("foo/bar.java");
		when(mockSet.getId()).thenReturn("1");
		when(mockSet.getItems()).thenReturn(Arrays.asList(mockFile));
		when(mockReview.getSets()).thenReturn(Arrays.asList(mockSet));

		return mockReview;
	}

	private void openReviewTest(GerritUrlHandler spy, String url) {
		doNothing().when(spy).openCompareEditor(any(IFileItem.class));
		spy.openUrl(page, url, 0);

		verify(spy, times(1)).revealPatchSet(any(EditorHandle.class), anyInt());
		verify(spy, times(1)).openCompareEditor(any(IFileItem.class));
	}

	private void assertPath(String expectedPath, String testUrl) {
		taskId = handler.getTaskId(repository, testUrl);
		patchSetNumber = handler.getPatchSetNumber(repository, testUrl, taskId);
		assertEquals(expectedPath, handler.getFilePath(repository, testUrl, taskId, patchSetNumber));
	}
}
