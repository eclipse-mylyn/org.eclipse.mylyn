package org.eclipse.mylyn.internal.team.ccvs;

import org.eclipse.core.resources.mapping.ResourceMapping;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.team.internal.ccvs.core.mapping.ChangeSetResourceMapping;
import org.eclipse.team.internal.core.subscribers.ActiveChangeSet;

/**
 * @since 3.0
 */
public class CvsChangeSetAdapterFactory implements IAdapterFactory {

	@SuppressWarnings("unchecked")
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adaptableObject instanceof ActiveChangeSet && adapterType == ResourceMapping.class) {
			ActiveChangeSet cs = (ActiveChangeSet) adaptableObject;
			return new ChangeSetResourceMapping(cs);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public Class[] getAdapterList() {
		return new Class[] { ResourceMapping.class };
	}

}
