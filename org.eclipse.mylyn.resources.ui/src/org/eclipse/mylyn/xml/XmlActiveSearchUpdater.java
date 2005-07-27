package org.eclipse.mylar.xml;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.search.internal.ui.text.FileSearchResult;
import org.eclipse.search.ui.IQueryListener;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.search.ui.text.Match;

/**
 * COPIED FROM: org.eclipse.search.internal.ui.text.SearchResultUpdater
 * @author Shawn Minto
 *
 */
public class XmlActiveSearchUpdater implements IResourceChangeListener, IQueryListener {
	private FileSearchResult fResult;

	public XmlActiveSearchUpdater(FileSearchResult result) {
		fResult= result;
		NewSearchUI.addQueryListener(this);
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}

	public void resourceChanged(IResourceChangeEvent event) {
		IResourceDelta delta= event.getDelta();
		if (delta != null)
			handleDelta(delta);
	}

	private void handleDelta(IResourceDelta d) {
		try {
			d.accept(new IResourceDeltaVisitor() {
				public boolean visit(IResourceDelta delta) throws CoreException {
					switch (delta.getKind()) {
						case IResourceDelta.ADDED :
							return false;
						case IResourceDelta.REMOVED:
							IResource res = delta.getResource();
							if (res instanceof IFile) {
								Match[] matches = fResult.getMatches(res);
								fResult.removeMatches(matches);
	
//								// change the file into a document
//								FileEditorInput fei = new FileEditorInput(
//										(IFile) res);
//	
//								for (int j = 0; j < matches.length; j++) {
//									Match m = matches[j];
//									try {
//										XmlNodeHelper xnode = new XmlNodeHelper(
//												fei, m.getOffset()); // need to find the node without the file
//										IMylarStructureBridge bridge = MylarPlugin
//												.getDefault().getStructureBridge(
//														((IFile)res).getName());
//										String handle = xnode.getHandle();
//										Object o = bridge
//												.getObjectForHandle(handle);
//										String name = bridge.getName(o);
//										if (o != null) {
//											// XXX remove the node and the reference
//											System.out.println("REMOVED RES: " + handle);
//										}
//										System.out.println("REMOVED RES: " + handle);
//									} catch (Exception e) {
//										MylarPlugin.log(e, "search failed");
//									}
//								}
							}
							break;
						case IResourceDelta.CHANGED :
							// TODO want to do something on chages to invalidate
							// handle changed resource
							break;
					}
					return true;
				}
			});
		} catch (CoreException e) {
			MylarPlugin.log(e.getStatus());
		}
	}

	public void queryAdded(ISearchQuery query) {
		// don't care
	}

	public void queryRemoved(ISearchQuery query) {
		if (fResult.equals(query.getSearchResult())) {
			ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
			NewSearchUI.removeQueryListener(this);
		}
	}
	
	public void queryStarting(ISearchQuery query) {
		// don't care
	}

	public void queryFinished(ISearchQuery query) {
		// don't care
	}
}
