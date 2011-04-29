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

import org.eclipse.core.runtime.AssertionFailedException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@SuppressWarnings("restriction")
@RunWith(MockitoJUnitRunner.class)
public class GitHubClientTest {

	@Test(expected = AssertionFailedException.class)
	public void constructor_NullArgument() {
		new GitHubClient(null);
	}

}
