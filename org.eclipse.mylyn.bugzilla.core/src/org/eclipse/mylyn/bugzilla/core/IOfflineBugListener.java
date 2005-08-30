package org.eclipse.mylar.bugzilla.core;


public interface IOfflineBugListener {

	public enum BugzillaOfflineStaus {
		SAVED, SAVED_WITH_OUTGOING_CHANGES, DELETED, SAVED_WITH_INCOMMING_CHANGES, CONFLICT
	}

	public void offlineStatusChange(IBugzillaBug bug, BugzillaOfflineStaus status);
	
}
