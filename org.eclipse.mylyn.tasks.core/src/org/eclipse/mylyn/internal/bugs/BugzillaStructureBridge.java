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

package org.eclipse.mylar.internal.bugs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.mylar.internal.bugzilla.ui.search.BugzillaSearchHit;
import org.eclipse.mylar.internal.core.DegreeOfSeparation;
import org.eclipse.mylar.internal.tasklist.ui.editors.RepositoryTaskOutlineNode;
import org.eclipse.mylar.internal.tasklist.ui.editors.RepositoryTaskSelection;
import org.eclipse.mylar.internal.tasklist.ui.editors.OutlineTools;
import org.eclipse.mylar.provisional.core.AbstractRelationProvider;
import org.eclipse.mylar.provisional.core.IDegreeOfSeparation;
import org.eclipse.mylar.provisional.core.IMylarStructureBridge;
import org.eclipse.ui.views.markers.internal.ProblemMarker;

/**
 * @author Mik Kersten
 * @author Shawn Minto
 */
public class BugzillaStructureBridge implements IMylarStructureBridge {

	public final static String CONTENT_TYPE = "bugzilla";

	public List<AbstractRelationProvider> providers;

	public String getContentType() {
		return CONTENT_TYPE;
	}

	public BugzillaStructureBridge() {
		super();
		providers = new ArrayList<AbstractRelationProvider>();
		// providers.add(MylarBugsPlugin.getReferenceProvider());
	}

	/**
	 * Handle format: <server-name:port>;<bug-id>;<comment#>
	 * 
	 * Use: OutlineTools ???
	 */
	public String getHandleIdentifier(Object object) {
		if (object instanceof RepositoryTaskOutlineNode) {
			RepositoryTaskOutlineNode node = (RepositoryTaskOutlineNode) object;
			return OutlineTools.getHandle(node);
		} else if (object instanceof RepositoryTaskSelection) {
			RepositoryTaskSelection n = (RepositoryTaskSelection) object;
			return OutlineTools.getHandle(n);
		}
		return null;
	}

//	private BugzillaReport result;

	/**
	 * TODO: this will not return a non-cached handle
	 */
	public Object getObjectForHandle(final String handle) {
//		result = null;
//
//		// HACK: determine appropriate repository
//		final TaskRepository repository = MylarTaskListPlugin.getRepositoryManager().getRepositoryForActiveTask(
//				BugzillaPlugin.REPOSITORY_KIND);
//
//		String[] parts = handle.split(";");
//		if (parts.length >= 2) {
//			String server = parts[0];
//			final int id = Integer.parseInt(parts[1]);
//
//			final String bugHandle = server + ";" + id;
//
//			int commentNumber = -1;
//			if (parts.length == 3) {
//				commentNumber = Integer.parseInt(parts[2]);
//			}
//
//			// get the bugzillaOutlineNode for the element
//			IEditorPart editorPart = null;
//			try {
//				editorPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
//			} catch (NullPointerException e) {
//				// do nothing, this just means that there is no active page
//			}
//			if (editorPart != null && editorPart instanceof AbstractRepositoryTaskEditor) {
//				AbstractRepositoryTaskEditor abe = ((AbstractRepositoryTaskEditor) editorPart);
//				RepositoryTaskOutlineNode node = abe.getOutlineModel();
//				return findNode(node, commentNumber);
//			}
//
//			BugzillaReportElement reportNode = MylarBugsPlugin.getReferenceProvider().getCached(handle);
//
//			// try to get from the cache, if it doesn't exist, startup an
//			// operation to get it
//			result = MylarBugsPlugin.getDefault().getCache().getFromCache(bugHandle);
//			if (result == null && reportNode != null) {
//				return reportNode;
//			} else if (result == null && reportNode == null) {
//				IRunnableWithProgress op = new IRunnableWithProgress() {
//					public void run(IProgressMonitor monitor) {
//						monitor.beginTask("Downloading Bug# " + id, IProgressMonitor.UNKNOWN);
//						try {
//							Proxy proxySettings = MylarTaskListPlugin.getDefault().getProxySettings();
//							// XXX: move this
//							result = BugzillaRepositoryUtil.getBug(repository.getUrl(), repository.getUserName(), repository.getPassword(), proxySettings, repository.getCharacterEncoding(), id);
//							if (result != null) {
//								MylarBugsPlugin.getDefault().getCache().cache(bugHandle, result);
//							}
//						} catch (Exception e) {
//							result = null;
//						}
//					}
//				};
//
//				IProgressService service = PlatformUI.getWorkbench().getProgressService();
//				try {
//					service.run(false, false, op);
//				} catch (InvocationTargetException e) {
//					// RepositoryOperation was canceled
//				} catch (InterruptedException e) {
//					// Handle the wrapped exception
//				}
//				return null;
//			}
//		}
		return null;
	}

//	private RepositoryTaskOutlineNode findNode(RepositoryTaskOutlineNode startNode, int commentNumber) {
//
//		if (commentNumber == -1) {
//			return startNode;
//		} else if (startNode.getComment() != null && startNode.getComment().getNumber() == commentNumber - 1) {
//			return startNode;
//		} else if (startNode.isCommentHeader() && commentNumber == 1) {
//			return startNode;
//		} else if (startNode.isDescription() && commentNumber == 0) {
//			return startNode;
//		}
//
//		RepositoryTaskOutlineNode[] children = startNode.getChildren();
//		for (int i = 0; i < children.length; i++) {
//			RepositoryTaskOutlineNode n = findNode(children[i], commentNumber);
//			if (n != null)
//				return n;
//		}
//		return null;
//	}

	public String getParentHandle(String handle) {

		// check so that we don't need to try to get the parent if we are
		// already at the bug report
		if (!handle.matches(".*;.*;.*"))
			return null;

		RepositoryTaskOutlineNode bon = (RepositoryTaskOutlineNode) getObjectForHandle(handle);
		if (bon != null && bon.getParent() != null)
			return OutlineTools.getHandle(bon.getParent());
		else
			return null;
		// String [] parts = handle.split(";");
		// if (parts.length == 1){
		// return null;
		// }else if (parts.length > 2) {
		// String newHandle = "";
		// for(int i = 0; i < parts.length - 1; i++)
		// newHandle += parts[i] + ";";
		// return newHandle.substring(0, newHandle.length() - 1);
		// // return handle.substring(0, handle.lastIndexOf(";"));
		// }
		// return null;
	}

	public String getName(Object object) {
		if (object instanceof RepositoryTaskOutlineNode) {
			RepositoryTaskOutlineNode b = (RepositoryTaskOutlineNode) object;
			return OutlineTools.getName(b);
		} else if (object instanceof BugzillaReportInfo) {
			BugzillaSearchHit hit = ((BugzillaReportInfo) object).getHit();
			return hit.getRepositoryUrl() + ": Bug#: " + hit.getId() + ": " + hit.getDescription();
		}
		return "";
	}

	public boolean canBeLandmark(String handle) {
		return false;
	}

	public boolean acceptsObject(Object object) {
		return object instanceof RepositoryTaskOutlineNode || object instanceof RepositoryTaskSelection;
	}

	public boolean canFilter(Object element) {
		return true;
	}

	public boolean isDocument(String handle) {
		return (handle.indexOf(';') == handle.lastIndexOf(';') && handle.indexOf(";") != -1);
	}

	public String getHandleForMarker(ProblemMarker marker) {
		return null;
	}

	public String getContentType(String elementHandle) {
		return getContentType();
	}

	public List<AbstractRelationProvider> getRelationshipProviders() {
		return providers;
	}

	public List<IDegreeOfSeparation> getDegreesOfSeparation() {
		List<IDegreeOfSeparation> separations = new ArrayList<IDegreeOfSeparation>();
		separations.add(new DegreeOfSeparation("disabled", 0));
		separations.add(new DegreeOfSeparation("local, fully qualified matches", 1));
		separations.add(new DegreeOfSeparation("local, unqualified matches", 2));
		separations.add(new DegreeOfSeparation("server, fully quaified matches", 3));
		separations.add(new DegreeOfSeparation("server, unqualified matches", 4));

		return separations;
	}

	public String getHandleForOffsetInObject(Object resource, int offset) {
		return null;
	}

	public void setParentBridge(IMylarStructureBridge bridge) {
		// ignore
	}

	public List<String> getChildHandles(String handle) {
		return Collections.emptyList();
	}
}
