/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
/*
 * Created on Feb 2, 2005
 */
package org.eclipse.mylar.internal.bugs.search;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.mylar.internal.bugs.BugzillaReportElement;
import org.eclipse.mylar.internal.bugs.BugzillaSearchManager;
import org.eclipse.mylar.internal.bugs.BugzillaStructureBridge;
import org.eclipse.mylar.internal.bugs.MylarBugsPlugin;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.internal.core.search.IActiveSearchListener;
import org.eclipse.mylar.internal.core.search.IMylarSearchOperation;
import org.eclipse.mylar.provisional.core.AbstractRelationProvider;
import org.eclipse.mylar.provisional.core.IMylarElement;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.TaskRepository;
import org.eclipse.ui.PlatformUI;

/**
 * @author Shawn Minto
 */
public class BugzillaReferencesProvider extends AbstractRelationProvider {

	public static final String ID = "org.eclipse.mylar.bugs.search.references";

	public static final String NAME = "referenced by";

	public static final int DEFAULT_DEGREE = 0;

	public BugzillaReferencesProvider() {
		super(BugzillaStructureBridge.CONTENT_TYPE, ID);
	}

	protected boolean acceptElement(IJavaElement javaElement) {
		return javaElement != null && (javaElement instanceof IMember || javaElement instanceof IType)
				&& javaElement.exists();
	}

	/**
	 * HACK: checking kind as string - don't want the dependancy to mylar.java
	 */
	@Override
	protected void findRelated(final IMylarElement node, int degreeOfSeparation) {
		if (!node.getContentType().equals("java"))
			return;
		IJavaElement javaElement = JavaCore.create(node.getHandleIdentifier());
		if (!acceptElement(javaElement)) {
			return;
		}
		runJob(node, degreeOfSeparation);
	}

	@Override
	public IMylarSearchOperation getSearchOperation(IMylarElement node, int limitTo, int degreeOfSepatation) {
		IJavaElement javaElement = JavaCore.create(node.getHandleIdentifier());

		TaskRepository repository = MylarTaskListPlugin.getRepositoryManager().getRepositoryForActiveTask(
				BugzillaPlugin.REPOSITORY_KIND);
		return new BugzillaMylarSearch(degreeOfSepatation, javaElement, repository.getUrl());
	}

	private void runJob(final IMylarElement node, final int degreeOfSeparation) {
		BugzillaMylarSearch search = (BugzillaMylarSearch) getSearchOperation(node, 0, degreeOfSeparation);

		search.addListener(new IActiveSearchListener() {

			private boolean gathered = false;

			public void searchCompleted(List<?> nodes) {
				Iterator<?> itr = nodes.iterator();

				if (MylarBugsPlugin.getDefault() == null)
					return;

				while (itr.hasNext()) {
					Object o = itr.next();
					if (o instanceof BugzillaReportElement) {
						BugzillaReportElement bugzillaNode = (BugzillaReportElement) o;
						final String handle = bugzillaNode.getElementHandle();
						if (MylarBugsPlugin.getDefault().getCache().getCached(handle) == null)
							cache(handle, bugzillaNode);

						PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
							public void run() {
								incrementInterest(node, BugzillaStructureBridge.CONTENT_TYPE, handle,
										degreeOfSeparation);
							}
						});
					}
				}
				gathered = true;
				BugzillaReferencesProvider.this.searchCompleted(node);
			}

			public boolean resultsGathered() {
				return gathered;
			}

		});
		search.run(new NullProgressMonitor(), Job.DECORATE - 10);
	}

	@Override
	public String getGenericId() {
		return ID;
	}

	@Override
	protected String getSourceId() {
		return ID;
	}

	@Override
	public String getName() {
		return NAME;
	}

	/*
	 * 
	 * STUFF FOR TEMPORARILY CACHING A PROXY REPORT
	 * 
	 * TODO remove the proxys and update the BugzillaStructureBridge cache so
	 * that on restart, we dont have to get all of the bugs
	 * 
	 */
	private static final Map<String, BugzillaReportElement> reports = new HashMap<String, BugzillaReportElement>();

	public BugzillaReportElement getCached(String handle) {
		return reports.get(handle);
	}

	protected void cache(String handle, BugzillaReportElement bugzillaNode) {
		reports.put(handle, bugzillaNode);
	}

	public void clearCachedReports() {
		reports.clear();
	}

	public Collection<? extends String> getCachedHandles() {
		return reports.keySet();
	}

	@Override
	public void stopAllRunningJobs() {
		BugzillaSearchManager.cancelAllRunningJobs();

	}

	@Override
	protected int getDefaultDegreeOfSeparation() {
		return DEFAULT_DEGREE;
	}
}
