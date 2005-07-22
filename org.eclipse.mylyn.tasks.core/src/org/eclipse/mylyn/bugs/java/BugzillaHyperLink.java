package org.eclipse.mylar.bugs.java;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.mylar.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.bugzilla.ui.BugzillaOpenStructure;
import org.eclipse.mylar.bugzilla.ui.ViewBugzillaAction;

public class BugzillaHyperLink implements IHyperlink {

	private IRegion region;
	
	private int id;
	
	public BugzillaHyperLink(IRegion nlsKeyRegion, int id) {
		this.region = nlsKeyRegion;
		this.id = id;
	}

	public IRegion getHyperlinkRegion() {
		return region;
	}

	public String getTypeLabel() {
		return null;
	}

	public String getHyperlinkText() {
		return null;
	}

	public void open() {
		List<BugzillaOpenStructure> l = new ArrayList<BugzillaOpenStructure>(1);
		l.add(new BugzillaOpenStructure(BugzillaPlugin.getDefault().getServerName(),id, -1));
		new ViewBugzillaAction("Open Bug " + id, l).run(new NullProgressMonitor());
	}

}
