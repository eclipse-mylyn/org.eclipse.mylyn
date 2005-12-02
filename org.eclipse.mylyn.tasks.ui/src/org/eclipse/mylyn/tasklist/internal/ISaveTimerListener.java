package org.eclipse.mylar.tasklist.internal;

/**
 * Interface implemented by clients who are to be notified of
 * periodic requests to save data to disk.
 * 
 * @author Wesley Coelho
 */
public interface ISaveTimerListener {

	/** 
	 * Called to notify the client of a PeriodicSaveTimer that
	 * a save should be performed 
	 */
	public void saveRequested();
}
