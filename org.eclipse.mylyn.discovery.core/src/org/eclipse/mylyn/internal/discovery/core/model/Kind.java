package org.eclipse.mylyn.internal.discovery.core.model;


/**
 * @author David Green
 */
public class Kind {
	
	protected ConnectorDescriptorKind kind;
	
	public Kind() {
	}
	
	/**
	 * must be one of 'document', 'task', 'vcs'
	 */
	public ConnectorDescriptorKind getKind() {
		return kind;
	}
	
	public void setKind(ConnectorDescriptorKind kind) {
		this.kind = kind;
	}
	
	public void validate() throws ValidationException {
		if (kind == null) {
			throw new ValidationException("Must specify kind/@kind");
		}
	}
}
