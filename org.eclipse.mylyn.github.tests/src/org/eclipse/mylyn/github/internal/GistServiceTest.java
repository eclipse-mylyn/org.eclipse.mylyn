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

import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.eclipse.core.runtime.AssertionFailedException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Unit tests for {@link GistService}
 */
@SuppressWarnings("restriction")
@RunWith(MockitoJUnitRunner.class)
public class GistServiceTest {

	@Mock
	private GitHubClient gitHubClient;

	private GistService gistService;

	@Before
	public void before() {
		gistService = new GistService(gitHubClient);
	}

	@Test(expected = AssertionFailedException.class)
	public void constructor_NullArgument() {
		new GistService(null);
	}

	@Test(expected = AssertionFailedException.class)
	public void getGist_NullId() throws IOException {
		gistService.getGist(null);
	}

	@Test
	public void getGist_OK() throws IOException {
		gistService.getGist("1");
		verify(gitHubClient).get("/gists/1.json", Gist.class);
	}

}
