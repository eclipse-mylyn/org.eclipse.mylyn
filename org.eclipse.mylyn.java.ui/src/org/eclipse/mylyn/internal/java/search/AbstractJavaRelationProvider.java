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
 * Created on Jan 26, 2005
 */
package org.eclipse.mylyn.internal.java.search;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IInitializer;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.internal.core.JavaProject;
import org.eclipse.jdt.internal.ui.search.JavaSearchQuery;
import org.eclipse.jdt.internal.ui.search.JavaSearchResult;
import org.eclipse.jdt.ui.search.ElementQuerySpecification;
import org.eclipse.jdt.ui.search.QuerySpecification;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.AbstractRelationProvider;
import org.eclipse.mylyn.context.core.ContextCorePlugin;
import org.eclipse.mylyn.context.core.IDegreeOfSeparation;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.core.MylarStatusHandler;
import org.eclipse.mylyn.internal.context.core.DegreeOfSeparation;
import org.eclipse.mylyn.internal.context.core.IActiveSearchListener;
import org.eclipse.mylyn.internal.context.core.IMylarSearchOperation;
import org.eclipse.mylyn.internal.java.JavaStructureBridge;
import org.eclipse.mylyn.resources.FocusedResourcesPlugin;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search2.internal.ui.InternalSearchUI;

/**
 * @author Mik Kersten
 */
public abstract class AbstractJavaRelationProvider extends AbstractRelationProvider {

	public static final String ID_GENERIC = "org.eclipse.mylyn.java.relation";

	public static final String NAME = "Java relationships";

	private static final int DEFAULT_DEGREE = 2;

	private static final List<Job> runningJobs = new ArrayList<Job>();

	@Override
	public String getGenericId() {
		return ID_GENERIC;
	}

	protected AbstractJavaRelationProvider(String structureKind, String id) {
		super(structureKind, id);
	}

	@Override
	public List<IDegreeOfSeparation> getDegreesOfSeparation() {
		List<IDegreeOfSeparation> separations = new ArrayList<IDegreeOfSeparation>();
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
		if (node == null)
			return;
		if (node.getContentType() == null) {
			MylarStatusHandler.log("null content type for: " + node, this);
			return;
		}
		if (!node.getContentType().equals(JavaStructureBridge.CONTENT_TYPE))
			return;
		IJavaElement javaElement = JavaCore.create(node.getHandleIdentifier());
		if (!acceptElement(javaElement) || !javaElement.exists() || javaElement instanceof IInitializer) {
			return;
		}

		IJavaSearchScope scope = createJavaSearchScope(javaElement, degreeOfSeparation);
		if (scope != null)
			runJob(node, degreeOfSeparation, getId());
	}

	private IJavaSearchScope createJavaSearchScope(IJavaElement element, int degreeOfSeparation) {
		List<IInteractionElement> landmarks = ContextCorePlugin.getContextManager().getActiveLandmarks();
		List<IInteractionElement> interestingElements = ContextCorePlugin.getContextManager().getActiveContext().getInteresting();

		Set<IJavaElement> searchElements = new HashSet<IJavaElement>();
		int includeMask = IJavaSearchScope.SOURCES;
		if (degreeOfSeparation == 1) {
			for (IInteractionElement landmark : landmarks) {
				AbstractContextStructureBridge bridge = ContextCorePlugin.getDefault().getStructureBridge(landmark.getContentType());
				if (includeNodeInScope(landmark, bridge)) {
					Object o = bridge.getObjectForHandle(landmark.getHandleIdentifier());
					if (o instanceof IJavaElement) {
						IJavaElement landmarkElement = (IJavaElement) o;
						if (landmarkElement.exists()) {
							if (landmarkElement instanceof IMember && !landmark.getInterest().isPropagated()) {
								searchElements.add(((IMember) landmarkElement).getCompilationUnit());
							} else if (landmarkElement instanceof ICompilationUnit) {
								searchElements.add(landmarkElement);
							}
						}
					}
				}
			}
		} else if (degreeOfSeparation == 2) {
			for (IInteractionElement interesting : interestingElements) {
				AbstractContextStructureBridge bridge = ContextCorePlugin.getDefault()
						.getStructureBridge(interesting.getContentType());
				if (includeNodeInScope(interesting, bridge)) {
					Object object = bridge.getObjectForHandle(interesting.getHandleIdentifier());
					if (object instanceof IJavaElement) {
						IJavaElement interestingElement = (IJavaElement) object;
						if (interestingElement.exists()) {
							if (interestingElement instanceof IMember && !interesting.getInterest().isPropagated()) {
								searchElements.add(((IMember) interestingElement).getCompilationUnit());
							} else if (interestingElement instanceof ICompilationUnit) {
								searchElements.add(interestingElement);
							}
						}
					}
				}
			}
		} else if (degreeOfSeparation == 3 || degreeOfSeparation == 4) {
			for (IInteractionElement interesting : interestingElements) {
				AbstractContextStructureBridge bridge = ContextCorePlugin.getDefault()
						.getStructureBridge(interesting.getContentType());
				if (includeNodeInScope(interesting, bridge)) {
					// TODO what to do when the element is not a java element,
					// how determine if a javaProject?
					IResource resource = FocusedResourcesPlugin.getDefault().getResourceForElement(interesting, true);
					if (resource != null) {
						IProject project = resource.getProject();
						if (project != null && JavaProject.hasJavaNature(project) && project.exists()) {
							IJavaProject javaProject = JavaCore.create(project);// ((IJavaElement)o).getJavaProject();
							if (javaProject != null && javaProject.exists())
								searchElements.add(javaProject);
						}
					}
				}
			}
			if (degreeOfSeparation == 4) {

				includeMask = IJavaSearchScope.SOURCES | IJavaSearchScope.APPLICATION_LIBRARIES
						| IJavaSearchScope.SYSTEM_LIBRARIES;
			}
		} else if (degreeOfSeparation == 5) {
			return SearchEngine.createWorkspaceScope();
		}

		if (searchElements.size() == 0) {
			return null;
		} else {
			IJavaElement[] elements = new IJavaElement[searchElements.size()];
			int j = 0;
			for (IJavaElement searchElement : searchElements) {
				elements[j] = searchElement;
				j++;
			}
			return SearchEngine.createJavaSearchScope(elements, includeMask);
		}
	}

	/**
	 * Only include Java elements and files.
	 */
	private boolean includeNodeInScope(IInteractionElement interesting, AbstractContextStructureBridge bridge) {
		if (interesting == null || bridge == null) {
			return false;
		} else {
			if (interesting.getContentType() == null) {
				// TODO: remove
				MylarStatusHandler.log("null content type for: " + interesting.getHandleIdentifier(), this);
				return false;
			} else {
				return interesting.getContentType().equals(JavaStructureBridge.CONTENT_TYPE)
						|| bridge.isDocument(interesting.getHandleIdentifier());
			}
		}
	}

	protected boolean acceptResultElement(IJavaElement element) {
		return !(element instanceof IImportDeclaration);
	}

	protected boolean acceptElement(IJavaElement javaElement) {
		return javaElement != null && (javaElement instanceof IMember || javaElement instanceof IType);
	}

	private void runJob(final IInteractionElement node, final int degreeOfSeparation, final String kind) {

		int limitTo = 0;
		if (kind.equals(JavaReferencesProvider.ID)) {
			limitTo = IJavaSearchConstants.REFERENCES;
		} else if (kind.equals(JavaImplementorsProvider.ID)) {
			limitTo = IJavaSearchConstants.IMPLEMENTORS;
		} else if (kind.equals(JUnitReferencesProvider.ID)) {
			limitTo = IJavaSearchConstants.REFERENCES;
		} else if (kind.equals(JavaReadAccessProvider.ID)) {
			limitTo = IJavaSearchConstants.REFERENCES;
		} else if (kind.equals(JavaWriteAccessProvider.ID)) {
			limitTo = IJavaSearchConstants.REFERENCES;
		}

		final JavaSearchOperation query = (JavaSearchOperation) getSearchOperation(node, limitTo, degreeOfSeparation);
		if (query == null)
			return;

		JavaSearchJob job = new JavaSearchJob(query.getLabel(), query);
		query.addListener(new IActiveSearchListener() {

			private boolean gathered = false;

			public boolean resultsGathered() {
				return gathered;
			}

			@SuppressWarnings("unchecked")
			public void searchCompleted(List l) {
				if (l == null)
					return;
				List<IJavaElement> relatedHandles = new ArrayList<IJavaElement>();
				Object[] elements = l.toArray();
				for (int i = 0; i < elements.length; i++) {
					if (elements[i] instanceof IJavaElement)
						relatedHandles.add((IJavaElement) elements[i]);
				}

				for (IJavaElement element : relatedHandles) {
					if (!acceptResultElement(element))
						continue;
					incrementInterest(node, JavaStructureBridge.CONTENT_TYPE, element.getHandleIdentifier(),
							degreeOfSeparation);
				}
				gathered = true;
				AbstractJavaRelationProvider.this.searchCompleted(node);
			}

		});
		InternalSearchUI.getInstance();

		runningJobs.add(job);
		job.setPriority(Job.DECORATE - 10);
		job.schedule();
	}

	@Override
	public IMylarSearchOperation getSearchOperation(IInteractionElement node, int limitTo, int degreeOfSeparation) {
		IJavaElement javaElement = JavaCore.create(node.getHandleIdentifier());
		if (javaElement == null || !javaElement.exists())
			return null;

		IJavaSearchScope scope = createJavaSearchScope(javaElement, degreeOfSeparation);

		if (scope == null)
			return null;

		QuerySpecification specs = new ElementQuerySpecification(javaElement, limitTo, scope,
				"Mylar degree of separation: " + degreeOfSeparation);

		return new JavaSearchOperation(specs);
	}

	protected static class JavaSearchJob extends Job {

		private JavaSearchOperation op;

		public JavaSearchJob(String name, JavaSearchOperation op) {
			super(name);
			this.op = op;
		}

		/**
		 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
		 */
		@Override
		protected IStatus run(IProgressMonitor monitor) {
			return op.run(monitor);
		}

	}

	protected static class JavaSearchOperation extends JavaSearchQuery implements IMylarSearchOperation {
		private ISearchResult result = null;

		@Override
		public ISearchResult getSearchResult() {
			if (result == null)
				result = new JavaSearchResult(this);
			new JavaActiveSearchResultUpdater((JavaSearchResult) result);
			return result;
		}

		@Override
		public IStatus run(IProgressMonitor monitor) {
			try {
				IStatus runStatus = super.run(monitor);
				ISearchResult result = getSearchResult();
				if (result instanceof JavaSearchResult) {
					// TODO make better
					Object[] objs = ((JavaSearchResult) result).getElements();
					if (objs == null) {
						notifySearchCompleted(null);
					} else {
						List<Object> l = new ArrayList<Object>();
						for (int i = 0; i < objs.length; i++) {
							l.add(objs[i]);
						}
						notifySearchCompleted(l);
					}
				}
				return runStatus;
			} catch (ConcurrentModificationException cme) {
				MylarStatusHandler.log(cme, "java search failed");
			} catch (Throwable t) {
				MylarStatusHandler.log(t, "java search failed");
			}

			IStatus status = new Status(IStatus.WARNING, ContextCorePlugin.PLUGIN_ID, IStatus.OK,
					"could not run Java search", null);
			notifySearchCompleted(null);
			return status;
		}

		/**
		 * Constructor
		 * 
		 * @param data
		 */
		public JavaSearchOperation(QuerySpecification data) {
			super(data);

		}

		/** List of listeners wanting to know about the searches */
		private List<IActiveSearchListener> listeners = new ArrayList<IActiveSearchListener>();

		/**
		 * Add a listener for when the bugzilla search is completed
		 * 
		 * @param l
		 *            The listener to add
		 */
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
