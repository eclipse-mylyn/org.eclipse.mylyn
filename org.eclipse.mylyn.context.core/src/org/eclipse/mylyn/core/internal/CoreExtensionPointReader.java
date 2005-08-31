/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.core.internal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.mylar.core.IMylarStructureBridge;
import org.eclipse.mylar.core.MylarPlugin;

/**
 * @author Mik Kersten
 * @author Shawn Minto
 */
public class CoreExtensionPointReader {
	
	private static boolean extensionsRead = false;
	private static CoreExtensionPointReader thisReader = new CoreExtensionPointReader();
	
	// read the extensions and load the required plugins
	public static void initExtensions() {
		// code from "contributing to eclipse" with modifications for deprecated code
		if(!extensionsRead){
			IExtensionRegistry registry = Platform.getExtensionRegistry();
			IExtensionPoint extensionPoint = registry.getExtensionPoint(MylarPlugin.EXTENSION_ID_CONTEXT);
			IExtension[] extensions = extensionPoint.getExtensions();
			for(int i = 0; i < extensions.length; i++){
				IConfigurationElement[] elements = extensions[i].getConfigurationElements();
				for(int j = 0; j < elements.length; j++){
					if(elements[j].getName().compareTo(MylarPlugin.ELEMENT_STRUCTURE_BRIDGE) == 0){
						readBridge(elements[j]);
					} 
				}
			}
			extensionsRead = true;
		}
	}

	private static void readBridge(IConfigurationElement element) {
		try{
			Object bridge = element.createExecutableExtension(MylarPlugin.ELEMENT_STRUCTURE_BRIDGE_CLASS);
			if (bridge instanceof IMylarStructureBridge) {
				MylarPlugin.getDefault().internalAddBridge((IMylarStructureBridge) bridge);
				if (element.getAttribute(MylarPlugin.ELEMENT_STRUCTURE_BRIDGE_PARENT) != null) {
					Object parent = element.createExecutableExtension(MylarPlugin.ELEMENT_STRUCTURE_BRIDGE_PARENT);
					if (parent instanceof IMylarStructureBridge) {
						((IMylarStructureBridge)bridge).setParentBridge(((IMylarStructureBridge)parent));
					} else {
						MylarPlugin.log("Could not load parent bridge: " + parent.getClass().getCanonicalName() + " must implement " + IMylarStructureBridge.class.getCanonicalName(), thisReader);	
					}
				}
			} else {
				MylarPlugin.log("Could not load bridge: " + bridge.getClass().getCanonicalName() + " must implement " + IMylarStructureBridge.class.getCanonicalName(), thisReader);	
			}
		} catch (CoreException e){
			MylarPlugin.log(e, "Could not load bridge extension");
		}
	}
}
