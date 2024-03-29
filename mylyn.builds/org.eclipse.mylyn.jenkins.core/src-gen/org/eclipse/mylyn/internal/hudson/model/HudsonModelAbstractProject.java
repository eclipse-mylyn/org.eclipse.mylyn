/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.

package org.eclipse.mylyn.internal.hudson.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for hudson.model.AbstractProject complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="hudson.model.AbstractProject">
 *   &lt;complexContent>
 *     &lt;extension base="{}hudson.model.Job">
 *       &lt;sequence>
 *         &lt;element name="concurrentBuild" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="downstreamProject" type="{}hudson.model.AbstractProject" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="scm" type="{}hudson.scm.SCM" minOccurs="0"/>
 *         &lt;element name="upstreamProject" type="{}hudson.model.AbstractProject" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "hudson.model.AbstractProject", propOrder = { "concurrentBuild", "downstreamProject", "scm",
		"upstreamProject" })
@XmlSeeAlso({ HudsonModelProject.class })
@SuppressWarnings("all")
public class HudsonModelAbstractProject extends HudsonModelJob {

	protected boolean concurrentBuild;

	protected List<HudsonModelAbstractProject> downstreamProject;

	protected HudsonScmSCM scm;

	protected List<HudsonModelAbstractProject> upstreamProject;

	/**
	 * Gets the value of the concurrentBuild property.
	 */
	public boolean isConcurrentBuild() {
		return concurrentBuild;
	}

	/**
	 * Sets the value of the concurrentBuild property.
	 */
	public void setConcurrentBuild(boolean value) {
		concurrentBuild = value;
	}

	/**
	 * Gets the value of the downstreamProject property.
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to the returned list
	 * will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for the downstreamProject property.
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getDownstreamProject().add(newItem);
	 * </pre>
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link HudsonModelAbstractProject }
	 */
	public List<HudsonModelAbstractProject> getDownstreamProject() {
		if (downstreamProject == null) {
			downstreamProject = new ArrayList<>();
		}
		return downstreamProject;
	}

	/**
	 * Gets the value of the scm property.
	 * 
	 * @return possible object is {@link HudsonScmSCM }
	 */
	public HudsonScmSCM getScm() {
		return scm;
	}

	/**
	 * Sets the value of the scm property.
	 * 
	 * @param value
	 *            allowed object is {@link HudsonScmSCM }
	 */
	public void setScm(HudsonScmSCM value) {
		scm = value;
	}

	/**
	 * Gets the value of the upstreamProject property.
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to the returned list
	 * will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for the upstreamProject property.
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getUpstreamProject().add(newItem);
	 * </pre>
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link HudsonModelAbstractProject }
	 */
	public List<HudsonModelAbstractProject> getUpstreamProject() {
		if (upstreamProject == null) {
			upstreamProject = new ArrayList<>();
		}
		return upstreamProject;
	}

}
