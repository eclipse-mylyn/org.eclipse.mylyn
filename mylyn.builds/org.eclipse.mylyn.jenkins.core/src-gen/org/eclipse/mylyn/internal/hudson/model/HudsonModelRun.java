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
 * Java class for hudson.model.Run complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="hudson.model.Run">
 *   &lt;complexContent>
 *     &lt;extension base="{}hudson.model.Actionable">
 *       &lt;sequence>
 *         &lt;element name="artifact" type="{}hudson.model.Run-Artifact" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="building" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="duration" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="fullDisplayName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="keepLog" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="number" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="result" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/>
 *         &lt;element name="timestamp" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="url" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "hudson.model.Run", propOrder = { "artifact", "building", "description", "duration", "fullDisplayName",
		"id", "keepLog", "number", "result", "timestamp", "url" })
@XmlSeeAlso({ HudsonModelAbstractBuild.class })
@SuppressWarnings("all")
public class HudsonModelRun extends HudsonModelActionable {

	protected List<HudsonModelRunArtifact> artifact;

	protected boolean building;

	protected String description;

	protected long duration;

	protected String fullDisplayName;

	protected String id;

	protected boolean keepLog;

	protected int number;

	protected Object result;

	protected Long timestamp;

	protected String url;

	/**
	 * Gets the value of the artifact property.
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to the returned list
	 * will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for the artifact property.
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getArtifact().add(newItem);
	 * </pre>
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link HudsonModelRunArtifact }
	 */
	public List<HudsonModelRunArtifact> getArtifact() {
		if (artifact == null) {
			artifact = new ArrayList<>();
		}
		return artifact;
	}

	/**
	 * Gets the value of the building property.
	 */
	public boolean isBuilding() {
		return building;
	}

	/**
	 * Sets the value of the building property.
	 */
	public void setBuilding(boolean value) {
		building = value;
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
	 * Gets the value of the duration property.
	 */
	public long getDuration() {
		return duration;
	}

	/**
	 * Sets the value of the duration property.
	 */
	public void setDuration(long value) {
		duration = value;
	}

	/**
	 * Gets the value of the fullDisplayName property.
	 * 
	 * @return possible object is {@link String }
	 */
	public String getFullDisplayName() {
		return fullDisplayName;
	}

	/**
	 * Sets the value of the fullDisplayName property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 */
	public void setFullDisplayName(String value) {
		fullDisplayName = value;
	}

	/**
	 * Gets the value of the id property.
	 * 
	 * @return possible object is {@link String }
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the value of the id property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 */
	public void setId(String value) {
		id = value;
	}

	/**
	 * Gets the value of the keepLog property.
	 */
	public boolean isKeepLog() {
		return keepLog;
	}

	/**
	 * Sets the value of the keepLog property.
	 */
	public void setKeepLog(boolean value) {
		keepLog = value;
	}

	/**
	 * Gets the value of the number property.
	 */
	public int getNumber() {
		return number;
	}

	/**
	 * Sets the value of the number property.
	 */
	public void setNumber(int value) {
		number = value;
	}

	/**
	 * Gets the value of the result property.
	 * 
	 * @return possible object is {@link Object }
	 */
	public Object getResult() {
		return result;
	}

	/**
	 * Sets the value of the result property.
	 * 
	 * @param value
	 *            allowed object is {@link Object }
	 */
	public void setResult(Object value) {
		result = value;
	}

	/**
	 * Gets the value of the timestamp property.
	 * 
	 * @return possible object is {@link Long }
	 */
	public Long getTimestamp() {
		return timestamp;
	}

	/**
	 * Sets the value of the timestamp property.
	 * 
	 * @param value
	 *            allowed object is {@link Long }
	 */
	public void setTimestamp(Long value) {
		timestamp = value;
	}

	/**
	 * Gets the value of the url property.
	 * 
	 * @return possible object is {@link String }
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Sets the value of the url property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 */
	public void setUrl(String value) {
		url = value;
	}

}
