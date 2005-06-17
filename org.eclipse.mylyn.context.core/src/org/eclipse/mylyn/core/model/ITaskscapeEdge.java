/*
 * Created on Jun 15, 2005
 */
package org.eclipse.mylar.core.model;

/**
 * @author Mik Kersten
 */
public interface ITaskscapeEdge {

    public abstract ITaskscapeNode getTarget();

    public abstract IDegreeOfInterest getDegreeOfInterest();

    public abstract String getLabel();

    public abstract String getRelationshipHandle();

    public abstract String getStructureKind();

    public abstract ITaskscapeNode getSource();

}