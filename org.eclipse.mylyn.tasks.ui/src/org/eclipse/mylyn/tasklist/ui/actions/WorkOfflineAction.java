package org.eclipse.mylar.tasklist.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.tasklist.MylarTasklistPlugin;

public class WorkOfflineAction extends Action {

	public static final String ID = "org.eclipse.mylar.tasklist.actions.work.offline";
	
	public WorkOfflineAction() {
		setId(ID);
		setText("Work Offline");
		setToolTipText("Work Offline");
		setChecked(MylarTasklistPlugin.getPrefs().getBoolean(MylarPlugin.WORK_OFFLINE));
	}
	
	@Override
	public void run() {
		boolean on = !MylarTasklistPlugin.getPrefs().getBoolean(MylarPlugin.WORK_OFFLINE);
		MylarTasklistPlugin.getPrefs().setValue(MylarPlugin.WORK_OFFLINE, on);
	}
}
