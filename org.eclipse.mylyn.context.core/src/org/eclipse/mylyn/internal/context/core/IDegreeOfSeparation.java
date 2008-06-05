package org.eclipse.mylyn.internal.context.core;

/**
 * NOTE: not used in current Mylyn distribution, likely to change for 3.0.
 * 
 * @author Mik Kersten
 * @since 2.0
 */
public interface IDegreeOfSeparation {

	public abstract String getLabel();

	public abstract int getDegree();

}
