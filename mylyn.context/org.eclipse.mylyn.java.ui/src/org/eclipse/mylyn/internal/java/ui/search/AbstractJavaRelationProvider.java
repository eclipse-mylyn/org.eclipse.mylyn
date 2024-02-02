/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.java.ui.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SafeRunner;
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
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.internal.context.core.AbstractRelationProvider;
import org.eclipse.mylyn.internal.context.core.DegreeOfSeparation;
import org.eclipse.mylyn.internal.context.core.IActiveSearchListener;
import org.eclipse.mylyn.internal.context.core.IActiveSearchOperation;
import org.eclipse.mylyn.internal.context.core.IDegreeOfSeparation;
import org.eclipse.mylyn.internal.java.ui.JavaStructureBridge;
import org.eclipse.mylyn.internal.java.ui.JavaUiBridgePlugin;
import org.eclipse.mylyn.internal.resources.ui.ResourcesUiBridgePlugin;
import org.eclipse.osgi.util.NLS;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search2.internal.ui.InternalSearchUI;

/**
 * @author Mik Kersten
 */
@SuppressWarnings("restriction")
public abstract class AbstractJavaRelationProvider extends AbstractRelationProvider {

	public static final String ID_GENERIC = "org.eclipse.mylyn.java.relation"; //$NON-NLS-1$

	public static final String NAME = "Java relationships"; //$NON-NLS-1$

	private static final int DEFAULT_DEGREE = 2;

	private static final List<Job> runningJobs = new ArrayList<>();

	@Override
	public String getGenericId() {
		return ID_GENERIC;
	}

	protected AbstractJavaRelationProvider(String structureKind, String id) {
		super(structureKind, id);
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
		if (node == null) {
			return;
		}
		if (node.getContentType() == null) {
			StatusHandler.log(new Status(IStatus.WARNING, JavaUiBridgePlugin.ID_PLUGIN, "Null content type for: " //$NON-NLS-1$
					+ node));
			return;
		}
		if (!node.getContentType().equals(JavaStructureBridge.CONTENT_TYPE)) {
			return;
		}
		IJavaElement javaElement = JavaCore.create(node.getHandleIdentifier());
		if (!acceptElement(javaElement) || !javaElement.exists() || javaElement instanceof IInitializer) {
			return;
		}

		IJavaSearchScope scope = createJavaSearchScope(javaElement, degreeOfSeparation);
		if (scope != null) {
			runJob(node, degreeOfSeparation, getId());
		}
	}

	private IJavaSearchScope createJavaSearchScope(IJavaElement element, int degreeOfSeparation) {
		Set<IInteractionElement> landmarks = ContextCore.getContextManager().getActiveLandmarks();
		List<IInteractionElement> interestingElements = ContextCore.getContextManager()
				.getActiveContext()
				.getInteresting();

		Set<IJavaElement> searchElements = new HashSet<>();
		int includeMask = IJavaSearchScope.SOURCES;
		if (degreeOfSeparation == 1) {
			for (IInteractionElement landmark : landmarks) {
				AbstractContextStructureBridge bridge = ContextCore.getStructureBridge(landmark.getContentType());
				if (includeNodeInScope(landmark, bridge)) {
					Object o = bridge.getObjectForHandle(landmark.getHandleIdentifier());
					if (o instanceof IJavaElement landmarkElement) {
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
				AbstractContextStructureBridge bridge = ContextCore.getStructureBridge(interesting.getContentType());
				if (includeNodeInScope(interesting, bridge)) {
					Object object = bridge.getObjectForHandle(interesting.getHandleIdentifier());
					if (object instanceof IJavaElement interestingElement) {
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
				AbstractContextStructureBridge bridge = ContextCore.getStructureBridge(interesting.getContentType());
				if (includeNodeInScope(interesting, bridge)) {
					// TODO what to do when the element is not a java element,
					// how determine if a javaProject?
					IResource resource = ResourcesUiBridgePlugin.getDefault().getResourceForElement(interesting, true);
					if (resource != null) {
						IProject project = resource.getProject();
						if (project != null && JavaProject.hasJavaNature(project) && project.exists()) {
							IJavaProject javaProject = JavaCore.create(project);// ((IJavaElement)o).getJavaProject();
							if (javaProject != null && javaProject.exists()) {
								searchElements.add(javaProject);
							}
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
		} else if (interesting.getContentType() == null) {
			// TODO: remove
			StatusHandler.log(new Status(IStatus.WARNING, JavaUiBridgePlugin.ID_PLUGIN, "Null content type for: " //$NON-NLS-1$
					+ interesting.getHandleIdentifier()));
			return false;
		} else {
			return interesting.getContentType().equals(JavaStructureBridge.CONTENT_TYPE)
					|| bridge.isDocument(interesting.getHandleIdentifier());
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
		if (query == null) {
			return;
		}

		JavaSearchJob job = new JavaSearchJob(query.getLabel(), query);
		query.addListener(new IActiveSearchListener() {

			private boolean gathered = false;

			@Override
			public boolean resultsGathered() {
				return gathered;
			}

			@Override
			@SuppressWarnings("rawtypes")
			public void searchCompleted(List l) {
				if (l == null) {
					return;
				}
				List<IJavaElement> relatedHandles = new ArrayList<>();
				Object[] elements = l.toArray();
				for (Object element : elements) {
					if (element instanceof IJavaElement) {
						relatedHandles.add((IJavaElement) element);
					}
				}

				for (IJavaElement element : relatedHandles) {
					if (!acceptResultElement(element)) {
						continue;
					}
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
	public IActiveSearchOperation getSearchOperation(IInteractionElement node, int limitTo, int degreeOfSeparation) {
		IJavaElement javaElement = JavaCore.create(node.getHandleIdentifier());
		if (javaElement == null || !javaElement.exists()) {
			return null;
		}

		IJavaSearchScope scope = createJavaSearchScope(javaElement, degreeOfSeparation);

		if (scope == null) {
			return null;
		}

		QuerySpecification specs = new ElementQuerySpecification(javaElement, limitTo, scope,
				Messages.AbstractJavaRelationProvider_Mylyn_degree_of_separation + degreeOfSeparation);

		return new JavaSearchOperation(specs);
	}

	protected static class JavaSearchJob extends Job {

		private final JavaSearchOperation op;

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

	protected static class JavaSearchOperation extends JavaSearchQuery implements IActiveSearchOperation {
		private ISearchResult result = null;

		@Override
		public ISearchResult getSearchResult() {
			if (result == null) {
				result = new JavaSearchResult(this);
			}
			new JavaActiveSearchResultUpdater((JavaSearchResult) result);
			return result;
		}

		@Override
		public IStatus run(IProgressMonitor monitor) {
			IStatus runStatus = super.run(monitor);
			ISearchResult result = getSearchResult();
			if (result instanceof JavaSearchResult) {
				Object[] objs = ((JavaSearchResult) result).getElements();
				if (objs == null) {
					notifySearchCompleted(null);
				} else {
					List<Object> l = new ArrayList<>();
					Collections.addAll(l, objs);
					notifySearchCompleted(l);
				}
			}
			return runStatus;
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
		 * Notifies all of the listeners that the bugzilla search is completed
		 * 
		 * @param doiList
		 *            A list of BugzillaSearchHitDoiInfo
		 * @param member
		 *            The IMember that the search was performed on
		 */
		public void notifySearchCompleted(final List<Object> l) {
			for (final IActiveSearchListener listener : listeners) {
				SafeRunner.run(new ISafeRunnable() {
					@Override
					public void run() throws Exception {
						listener.searchCompleted(l);
					}

					@Override
					public void handleException(Throwable e) {
						StatusHandler.log(new Status(IStatus.ERROR, JavaUiBridgePlugin.ID_PLUGIN,
								NLS.bind("Unexpected error during listener invocation: {0}", listener.getClass()), e)); //$NON-NLS-1$
					}
				});
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
