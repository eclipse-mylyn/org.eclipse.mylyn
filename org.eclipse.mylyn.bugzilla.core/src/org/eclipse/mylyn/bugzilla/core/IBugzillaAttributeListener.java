package org.eclipse.mylar.bugzilla.core;


/**
 * @author Ken Sueda
 */
public interface IBugzillaAttributeListener {
	public abstract void attributeChanged(String attribute, String value);
}
