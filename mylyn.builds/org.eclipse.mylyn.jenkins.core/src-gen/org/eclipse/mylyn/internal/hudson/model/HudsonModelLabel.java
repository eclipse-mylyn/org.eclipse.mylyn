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
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for hudson.model.Label complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="hudson.model.Label">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="busyExecutors" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="cloud" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="idleExecutors" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="loadStatistics" type="{}hudson.model.LoadStatistics" minOccurs="0"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="node" type="{}hudson.model.Node" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="offline" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="tiedJob" type="{}hudson.model.AbstractProject" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="totalExecutors" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "hudson.model.Label", propOrder = { "busyExecutors", "cloud", "description", "idleExecutors",
		"loadStatistics", "name", "node", "offline", "tiedJob", "totalExecutors" })
@SuppressWarnings("all")
public class HudsonModelLabel {

	protected int busyExecutors;

	protected List<Object> cloud;

	protected String description;

	protected int idleExecutors;

	protected HudsonModelLoadStatistics loadStatistics;

	protected String name;

	protected List<HudsonModelNode> node;

	protected boolean offline;

	protected List<HudsonModelAbstractProject> tiedJob;

	protected int totalExecutors;

	/**
	 * Gets the value of the busyExecutors property.
	 */
	public int getBusyExecutors() {
		return busyExecutors;
	}

	/**
	 * Sets the value of the busyExecutors property.
	 */
	public void setBusyExecutors(int value) {
		busyExecutors = value;
	}

	/**
	 * Gets the value of the cloud property.
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to the returned list
	 * will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for the cloud property.
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getCloud().add(newItem);
	 * </pre>
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link Object }
	 */
	public List<Object> getCloud() {
		if (cloud == null) {
			cloud = new ArrayList<>();
		}
		return cloud;
	}

	/**
	 * Gets the value of the description property.
	 * 
	 * @return possible object is {@link String }
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the value of the description property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 */
	public void setDescription(String value) {
		description = value;
	}

	/**
	 * Gets the value of the idleExecutors property.
	 */
	public int getIdleExecutors() {
		return idleExecutors;
	}

	/**
	 * Sets the value of the idleExecutors property.
	 */
	public void setIdleExecutors(int value) {
		idleExecutors = value;
	}

	/**
	 * Gets the value of the loadStatistics property.
	 * 
	 * @return possible object is {@link HudsonModelLoadStatistics }
	 */
	public HudsonModelLoadStatistics getLoadStatistics() {
		return loadStatistics;
	}

	/**
	 * Sets the value of the loadStatistics property.
	 * 
	 * @param value
	 *            allowed object is {@link HudsonModelLoadStatistics }
	 */
	public void setLoadStatistics(HudsonModelLoadStatistics value) {
		loadStatistics = value;
	}

	/**
	 * Gets the value of the name property.
	 * 
	 * @return possible object is {@link String }
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the value of the name property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 */
	public void setName(String value) {
		name = value;
	}

	/**
	 * Gets the value of the node property.
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to the returned list
	 * will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for the node property.
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getNode().add(newItem);
	 * </pre>
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link HudsonModelNode }
	 */
	public List<HudsonModelNode> getNode() {
		if (node == null) {
			node = new ArrayList<>();
		}
		return node;
	}

	/**
	 * Gets the value of the offline property.
	 */
	public boolean isOffline() {
		return offline;
	}

	/**
	 * Sets the value of the offline property.
	 */
	public void setOffline(boolean value) {
		offline = value;
	}

	/**
	 * Gets the value of the tiedJob property.
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to the returned list
	 * will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for the tiedJob property.
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getTiedJob().add(newItem);
	 * </pre>
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link HudsonModelAbstractProject }
	 */
	public List<HudsonModelAbstractProject> getTiedJob() {
		if (tiedJob == null) {
			tiedJob = new ArrayList<>();
		}
		return tiedJob;
	}

	/**
	 * Gets the value of the totalExecutors property.
	 */
	public int getTotalExecutors() {
		return totalExecutors;
	}

	/**
	 * Sets the value of the totalExecutors property.
	 */
	public void setTotalExecutors(int value) {
		totalExecutors = value;
	}

}
