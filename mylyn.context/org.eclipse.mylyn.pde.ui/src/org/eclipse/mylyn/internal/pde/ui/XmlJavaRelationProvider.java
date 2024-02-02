/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.pde.ui;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.internal.resources.File;
import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.internal.context.core.AbstractRelationProvider;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.context.core.DegreeOfSeparation;
import org.eclipse.mylyn.internal.context.core.IActiveSearchListener;
import org.eclipse.mylyn.internal.context.core.IActiveSearchOperation;
import org.eclipse.mylyn.internal.context.core.IDegreeOfSeparation;
import org.eclipse.mylyn.internal.ide.ui.XmlNodeHelper;
import org.eclipse.mylyn.internal.java.ui.search.XmlActiveSearchUpdater;
import org.eclipse.mylyn.internal.resources.ui.ResourcesUiBridgePlugin;
import org.eclipse.search.core.text.TextSearchScope;
import org.eclipse.search.internal.ui.text.FileSearchQuery;
import org.eclipse.search.internal.ui.text.FileSearchResult;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.text.FileTextSearchScope;
import org.eclipse.search.ui.text.Match;

/**
 * @author Shawn Minto
 * @author Mik Kersten
 */
@SuppressWarnings("restriction")
public class XmlJavaRelationProvider extends AbstractRelationProvider {

	public static final String SOURCE_ID = "org.eclipse.mylyn.xml.search.references"; //$NON-NLS-1$

	public static final String NAME = "referenced by"; //$NON-NLS-1$

	public static final int DEFAULT_DEGREE = 3;

	public static final List<Job> runningJobs = new ArrayList<>();

	public static final Map<Match, XmlNodeHelper> nodeMap = new HashMap<>();

	public XmlJavaRelationProvider() {
		// TODO: should this be a generic XML extension?
		super(PdeStructureBridge.CONTENT_TYPE, SOURCE_ID);
	}

	@Override
	public List<IDegreeOfSeparation> getDegreesOfSeparation() {
		List<IDegreeOfSeparation> separations = new ArrayList<>();
		separations.add(new DegreeOfSeparation(DOS_0_LABEL, 0));
		separations.add(new DegreeOfSeparation(DOS_1_LABEL, 1));
		separations.add(new DegreeOfSeparation(DOS_2_LABEL, 2));
		separations.add(new DegreeOfSeparation(DOS_3_LABEL, 3));
		separations.add(new DegreeOfSeparation(DOS_4_LABEL, 4));
		separations.add(new DegreeOfSeparation(DOS_5_LABEL, 5));
		return separations;
	}

	@Override
	protected void findRelated(final IInteractionElement node, int degreeOfSeparation) {
		if (!node.getContentType().equals("java")) { //$NON-NLS-1$
			return;
		}
		IJavaElement javaElement = JavaCore.create(node.getHandleIdentifier());
		if (javaElement == null || javaElement instanceof ICompilationUnit || !javaElement.exists()
				|| !acceptElement(javaElement)) {
			return;
		}

		TextSearchScope scope = createTextSearchScope(degreeOfSeparation);
		if (scope != null) {
			runJob(node, javaElement, degreeOfSeparation, getId());
		}
	}

	protected TextSearchScope createTextSearchScope(int degreeOfSeparation) {
		Set<IInteractionElement> landmarks = ContextCore.getContextManager().getActiveLandmarks();

		switch (degreeOfSeparation) {
			case 1:
				// create a search scope for the projects of landmarks
				Set<IResource> l = new HashSet<>();
				for (IInteractionElement landmark : landmarks) {
					if (landmark.getContentType().equals(PdeStructureBridge.CONTENT_TYPE)) {
						// ||
						// landmark.getContentType().equals(AntStructureBridge.CONTENT_TYPE))
						// {
						String handle = landmark.getHandleIdentifier();
						IResource element = null;
						int first = handle.indexOf(";"); //$NON-NLS-1$
						String filename = handle;
						if (first != -1) {
							filename = handle.substring(0, first);
						}
						try {
							// change the file into a document
							IPath path = new Path(filename);
							element = ((Workspace) ResourcesPlugin.getWorkspace()).newResource(path, IResource.FILE);
						} catch (Exception e) {
							StatusHandler.log(
									new Status(IStatus.WARNING, PdeUiBridgePlugin.ID_PLUGIN, "Scope creation failed", //$NON-NLS-1$
											e));
						}
						l.add(element);
					}
				}

				IResource[] res = new IResource[l.size()];
				res = l.toArray(res);
				TextSearchScope doiScope = FileTextSearchScope.newSearchScope(res,
						new String[] { PdeStructureBridge.CONTENT_TYPE }, false);
				return l.isEmpty() ? null : doiScope;
			case 2:
				// create a search scope for the projects of landmarks
				Set<IProject> projectsToSearch = new HashSet<>();
				for (IInteractionElement landmark : landmarks) {
					AbstractContextStructureBridge bridge = ContextCore.getStructureBridge(landmark.getContentType());
					IResource resource = ResourcesUiBridgePlugin.getDefault().getResourceForElement(landmark, true);
					IProject project = null;
					if (resource != null) {
						project = resource.getProject();
					} else {
						Object object = bridge.getObjectForHandle(landmark.getHandleIdentifier());
						if (object instanceof IJavaElement) {
							project = ((IJavaElement) object).getJavaProject().getProject();
						}
					}
					if (project != null) {
						projectsToSearch.add(project);
					}
				}

				res = new IProject[projectsToSearch.size()];
				res = projectsToSearch.toArray(res);
				TextSearchScope projScope = FileTextSearchScope.newSearchScope(res,
						new String[] { PdeStructureBridge.CONTENT_TYPE }, false);

				return projectsToSearch.isEmpty() ? null : projScope;
			case 3:
				// create a search scope for the workspace
				return FileTextSearchScope.newSearchScope(new IResource[] { ResourcesPlugin.getWorkspace().getRoot() },
						new String[] { PdeStructureBridge.CONTENT_TYPE }, false);
			case 4:
				// create a search scope for the workspace
				return FileTextSearchScope.newSearchScope(new IResource[] { ResourcesPlugin.getWorkspace().getRoot() },
						new String[] { PdeStructureBridge.CONTENT_TYPE }, false);
			default:
				return null;
		}

	}

	protected boolean acceptElement(IJavaElement javaElement) {
		return javaElement != null && (javaElement instanceof IMember || javaElement instanceof IType);
	}

	private void runJob(final IInteractionElement node, final IJavaElement javaElement, final int degreeOfSeparation,
			final String kind) {

		// get the fully qualified name and if it is null, don't search
		String fullyQualifiedName = getFullyQualifiedName(javaElement);

		if (fullyQualifiedName == null) {
			return;
		}

		// Create the search query
		final XMLSearchOperation query = (XMLSearchOperation) getSearchOperation(node, 0, degreeOfSeparation);
		if (query != null) {
			XMLSearchJob job = new XMLSearchJob(query.getLabel(), query);
			query.addListener(new IActiveSearchListener() {

				private boolean gathered = false;

				@Override
				public void searchCompleted(List<?> l) {
					// deal with File
					if (l.isEmpty()) {
						return;
					}

					Map<String, String> nodes = new HashMap<>();

					if (l.get(0) instanceof FileSearchResult) {
						FileSearchResult fsr = (FileSearchResult) l.get(0);

						Object[] far = fsr.getElements();
						for (Object element : far) {
							Match[] mar = fsr.getMatches(element);

							if (element instanceof File f) {

								// change the file into a document
								// FileEditorInput fei = new FileEditorInput(f);

								for (Match m : mar) {
									try {
										AbstractContextStructureBridge bridge = ContextCorePlugin.getDefault()
												.getStructureBridge(f.getName());
										String handle = bridge.getHandleForOffsetInObject(f, m.getOffset());
										if (handle != null) {
											String second = handle.substring(handle.indexOf(";")); //$NON-NLS-1$

											XmlNodeHelper xnode = new XmlNodeHelper(f.getFullPath().toString(), second);
											nodeMap.put(m, xnode);
											Object o = bridge.getObjectForHandle(handle);
											String name = bridge.getLabel(o);
											if (o != null) {
												nodes.put(handle, name);
											}
										}
									} catch (Exception e) {
										StatusHandler.log(new Status(IStatus.ERROR, PdeUiBridgePlugin.ID_PLUGIN,
												"Unable to create match", e)); //$NON-NLS-1$
									}
								}
							}
						}
					}

					for (String handle : nodes.keySet()) {

						incrementInterest(node, PdeStructureBridge.CONTENT_TYPE, handle, degreeOfSeparation);
					}
					gathered = true;
					XmlJavaRelationProvider.this.searchCompleted(node);
				}

				@Override
				public boolean resultsGathered() {
					return gathered;
				}
			});
			runningJobs.add(job);
			job.setPriority(Job.DECORATE - 10);
			job.schedule();
		}
	}

	@Override
	public IActiveSearchOperation getSearchOperation(IInteractionElement node, int limitTo, int degreeOfSeparation) {
		IJavaElement javaElement = JavaCore.create(node.getHandleIdentifier());
		TextSearchScope scope = createTextSearchScope(degreeOfSeparation);
		if (scope == null) {
			return null;
		}

		String fullyQualifiedName = getFullyQualifiedName(javaElement);

		return new XMLSearchOperation(scope, fullyQualifiedName);
	}

	private String getFullyQualifiedName(IJavaElement je) {
		if (!(je instanceof IMember m)) {
			return null;
		}

		if (m.getDeclaringType() == null) {
			return ((IType) m).getFullyQualifiedName();
		} else {
			return m.getDeclaringType().getFullyQualifiedName() + "." + m.getElementName(); //$NON-NLS-1$
		}
	}

	public static class XMLSearchJob extends Job {

		private final XMLSearchOperation op;

		/**
		 * Constructor
		 * 
		 * @param name
		 */
		public XMLSearchJob(String name, XMLSearchOperation op) {
			super(name);
			this.op = op;
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			return op.run(monitor);
		}

	}

	public static class XMLSearchOperation extends FileSearchQuery implements IActiveSearchOperation {

		@Override
		public ISearchResult getSearchResult() {
			try {
				// get the current page of the outline
				Class<?> clazz = FileSearchQuery.class;
				Field field = clazz.getDeclaredField("fResult"); //$NON-NLS-1$
				field.setAccessible(true);
				FileSearchResult fResult = (FileSearchResult) field.get(this);
				if (fResult == null) {
					fResult = new FileSearchResult(this);
					field.set(this, fResult);
					new XmlActiveSearchUpdater(fResult);
				}
				return fResult;
			} catch (Exception e) {
				StatusHandler.log(new Status(IStatus.WARNING, PdeUiBridgePlugin.ID_PLUGIN,
						"Failed to get search result: " + e.getMessage())); //$NON-NLS-1$
			}
			return super.getSearchResult();
		}

		@Override
		public IStatus run(IProgressMonitor monitor) {
			try {
				super.run(monitor);
				ISearchResult result = getSearchResult();
				if (result instanceof FileSearchResult) {
					List<Object> l = new ArrayList<>();
					if (((FileSearchResult) result).getElements().length != 0) {
						l.add(result);
					}

					notifySearchCompleted(l);
				}
				return Status.OK_STATUS;
			} catch (Throwable t) {
				return new Status(IStatus.WARNING, ContextCorePlugin.ID_PLUGIN, 0,
						Messages.XmlJavaRelationProvider_Skipped_XML_search, null);
			}
		}

		/**
		 * Constructor
		 * 
		 * @param data
		 */
		public XMLSearchOperation(TextSearchScope scope, String searchString) {
			super(searchString, false, true, (FileTextSearchScope) scope);
		}

		/** List of listeners wanting to know about the searches */
		private final List<IActiveSearchListener> listeners = new ArrayList<>();

		/**
		 * Add a listener for when the bugzilla search is completed
		 * 
		 * @param l
		 *            The listener to add
		 */
		@Override
		public void addListener(IActiveSearchListener l) {
			// add the listener to the list
			listeners.add(l);
		}

		/**
		 * Remove a listener for when the bugzilla search is completed
		 * 
		 * @param l
		 *            The listener to remove
		 */
		@Override
		public void removeListener(IActiveSearchListener l) {
			// remove the listener from the list
			listeners.remove(l);
		}

		/**
		 * Notify all of the listeners that the bugzilla search is completed
		 * 
		 * @param doiList
		 *            A list of BugzillaSearchHitDoiInfo
		 * @param member
		 *            The IMember that the search was performed on
		 */
		public void notifySearchCompleted(List<Object> l) {
			// go through all of the listeners and call
			// searchCompleted(colelctor,
			// member)
			for (IActiveSearchListener listener : listeners) {
				listener.searchCompleted(l);
			}
		}

	}

	@Override
	public String getGenericId() {
		return SOURCE_ID;
	}

	@Override
	protected String getSourceId() {
		return SOURCE_ID;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void stopAllRunningJobs() {
		for (Job j : runningJobs) {
			j.cancel();
		}
		runningJobs.clear();
	}

	@Override
	protected int getDefaultDegreeOfSeparation() {
		return DEFAULT_DEGREE;
	}
}
