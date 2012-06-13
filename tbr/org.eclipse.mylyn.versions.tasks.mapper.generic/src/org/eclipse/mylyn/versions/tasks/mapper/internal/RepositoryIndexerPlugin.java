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
package org.eclipse.mylyn.versions.tasks.mapper.internal;

import java.io.File;

import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.versions.tasks.mapper.generic.IChangeSetIndexSearcher;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
/**
 * 
 * @author Kilian Matt
 *
 */
public class RepositoryIndexerPlugin  implements BundleActivator {
	private static RepositoryIndexerPlugin instance;
	public static final String PLUGIN_ID="org.eclipse.mylyn.versions.tasks.mapper";
	
	private RepositoryIndexer synchronizer;
	private IChangeSetIndexSearcher indexSearch;
	
	public RepositoryIndexerPlugin() {
		instance = this;
	}

	public static RepositoryIndexerPlugin getDefault() {
		return instance;
	}
	
	public void start(BundleContext context) throws Exception {
		synchronizer= new RepositoryIndexer();
		
		File file = new File(TasksUiPlugin.getDefault().getDataDirectory(),".changeSetIndex");
		indexSearch=new ChangeSetIndexer(file,new EclipseWorkspaceRepositorySource());
	}
	public IChangeSetIndexSearcher getIndexer(){
		return indexSearch;	
	}

	public void stop(BundleContext context) throws Exception {
		this.instance=null;
		
	}

}
