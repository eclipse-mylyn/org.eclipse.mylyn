package org.eclipse.mylyn.versions.tasks.mapper.internal;
/*******************************************************************************
 * Copyright (c) 2012 Research Group for Industrial Software (INSO), Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Research Group for Industrial Software (INSO), Vienna University of Technology - initial API and implementation
 *******************************************************************************/


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
//import org.mockito.Mockito;
import org.osgi.framework.BundleContext;

/**
 *
 * @author Kilian Matt
 */
public class RepositoryIndexerPluginTest {

	private RepositoryIndexerPlugin plugin;
	private BundleContext context;

	@Before
	public void prepare() {
		plugin = new RepositoryIndexerPlugin();
		context =null;
//		Mockito.mock(BundleContext.class);
	}

	@Test
	public void startsSavesObjectAsDefault() throws Exception {
		plugin.start(context);
		Assert.assertSame(plugin, RepositoryIndexerPlugin.getDefault());
	}

	@Test
	public void lastStartedObjectIsSavedAsDefault() throws Exception {
		plugin.start(context);

		RepositoryIndexerPlugin other = new RepositoryIndexerPlugin();
		other.start(context);

		Assert.assertSame(other, RepositoryIndexerPlugin.getDefault());
		Assert.assertNotSame(plugin, RepositoryIndexerPlugin.getDefault());
	}

}
