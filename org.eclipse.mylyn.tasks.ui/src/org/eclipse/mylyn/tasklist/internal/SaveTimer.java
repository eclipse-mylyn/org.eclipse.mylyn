package org.eclipse.mylar.tasklist.internal;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.util.ITimerThreadListener;
import org.eclipse.mylar.core.util.TimerThread;

/**
 * Timer that periodically runs saveRequested() on its client as a job
 * 
 * @author Wesley Coelho
 */
public class SaveTimer implements ITimerThreadListener {

	private final static int DEFAULT_SAVE_INTERVAL = 100 * 60 * 1000; // (Ten
																		// minutes)
	private int saveInterval = DEFAULT_SAVE_INTERVAL;

	private ISaveTimerListener listener = null;

	private TimerThread timer = null;

	public SaveTimer(ISaveTimerListener listener) {
		this.listener = listener;
		timer = new TimerThread(saveInterval / 1000); // This constructor
														// wants seconds
		timer.addListener(this);
		timer.start();
	}

	public void setSaveIntervalMillis(int saveIntervalMillis) {
		this.saveInterval = saveIntervalMillis;
		timer.setTimeoutMillis(saveIntervalMillis);
	}

	public int getSaveIntervalMillis() {
		return saveInterval;
	}

	/**
	 * Called by the ActivityTimerThread Calls save in a new job
	 */
	public void fireTimedOut() {
		try {
			final SaveJob job = new SaveJob("Saving Task Data", listener);
			job.schedule();
		} catch (RuntimeException e) {
			MylarPlugin.log("Could not schedule save job", this);
		}
	}

	/** Job that makes the save call */
	private class SaveJob extends Job {
		private ISaveTimerListener listener = null;

		public SaveJob(String name, ISaveTimerListener listener) {
			super(name);
			this.listener = listener;
		}

		protected IStatus run(IProgressMonitor monitor) {
			listener.saveRequested();
			return Status.OK_STATUS;
		}
	}

}
