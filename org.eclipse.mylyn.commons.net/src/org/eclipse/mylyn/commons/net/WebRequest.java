package org.eclipse.mylyn.commons.net;

import java.util.concurrent.Callable;

/**
 * @author Steffen Pingel
 * @since 3.0
 */
public abstract class WebRequest<T> implements Callable<T> {

	/**
	 * @since 3.0
	 */
	public abstract void abort();

}