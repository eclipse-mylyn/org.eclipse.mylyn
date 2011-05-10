/*******************************************************************************
 *  Copyright (c) 2011 Christian Trutz
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Christian Trutz - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.github.internal;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.AssertionFailedException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Unit tests for {@link LabelService}.
 */
@SuppressWarnings("restriction")
@RunWith(MockitoJUnitRunner.class)
public class LabelServiceTest {

	@Mock
	private GitHubClient gitHubClient;

	@Mock
	private GitHubResponse response;

	private LabelService labelService;

	@Before
	public void before() throws IOException {
		doReturn(response).when(gitHubClient).get(any(GitHubRequest.class));
		labelService = new LabelService(gitHubClient);
	}

	@Test(expected = AssertionFailedException.class)
	public void constructor_NullArgument() {
		new LabelService(null);
	}

	@Test(expected = AssertionFailedException.class)
	public void getLabels_NullUser() throws IOException {
		labelService.getLabels(null, "not null");
	}

	@Test(expected = AssertionFailedException.class)
	public void getLabels_NullRepository() throws IOException {
		labelService.getLabels("not null", null);
	}

	@Test
	public void getLabels_OK() throws IOException {
		labelService.getLabels("test_user", "test_repository");
		TypeToken<List<Label>> labelsToken = new TypeToken<List<Label>>() {
		};
		verify(gitHubClient).get(any(GitHubRequest.class));
	}

	@Test(expected = AssertionFailedException.class)
	public void setLabels_NullUser() throws IOException {
		labelService.setLabels(null, "not null", "not null",
				new LinkedList<Label>());
	}

	@Test(expected = AssertionFailedException.class)
	public void setLabels_NullRepository() throws IOException {
		labelService.setLabels("not null", null, "not null",
				new LinkedList<Label>());
	}

	@Test(expected = AssertionFailedException.class)
	public void setLabels_NullIssueId() throws IOException {
		labelService.setLabels("not null", "not null", null,
				new LinkedList<Label>());
	}

	@Test
	public void setLabels_NullLabels() throws IOException {
		labelService.setLabels("test_user", "test_repository", "1", null);
		TypeToken<List<Label>> labelsToken = new TypeToken<List<Label>>() {
		};
		verify(gitHubClient).put(
				"/repos/test_user/test_repository/issues/1/labels.json", null,
				labelsToken.getType());
	}

	@Test
	public void setLabels_OK() throws IOException {
		List<Label> labels = new LinkedList<Label>();
		labelService.setLabels("test_user", "test_repository", "1", labels);
		TypeToken<List<Label>> labelsToken = new TypeToken<List<Label>>() {
		};
		verify(gitHubClient).put(
				"/repos/test_user/test_repository/issues/1/labels.json",
				labels, labelsToken.getType());
	}

	@Test(expected = AssertionFailedException.class)
	public void createLabel_NullUser() throws IOException {
		labelService.createLabel(null, "not null", new Label());
	}

	@Test(expected = AssertionFailedException.class)
	public void createLabel_NullRepository() throws IOException {
		labelService.createLabel("not null", null, new Label());
	}

	@Test(expected = AssertionFailedException.class)
	public void createLabel_NullLabel() throws IOException {
		labelService.createLabel("not null", "not null", null);
	}

	@Test
	public void createLabel_OK() throws IOException {
		Label label = new Label();
		labelService.createLabel("test_user", "test_repository", label);
		verify(gitHubClient).post(
				"/repos/test_user/test_repository/labels.json", label,
				Label.class);
	}
}
