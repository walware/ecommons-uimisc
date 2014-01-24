/*=============================================================================#
 # Copyright (c) 2012-2014 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.ui.workbench;

import static de.walware.ecommons.ui.internal.Messages.Variable_error_Resource_EmptySelection_message;

import java.io.File;
import java.net.URI;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.variables.IDynamicVariable;
import org.eclipse.core.variables.IDynamicVariableResolver;
import org.eclipse.osgi.util.NLS;

import de.walware.ecommons.ui.internal.Messages;
import de.walware.ecommons.ui.internal.UIMiscellanyPlugin;


public class ResourceVariableResolver implements IDynamicVariableResolver {
	
	
	public static class ContainerVariableResolver extends ResourceVariableResolver {
		
		
		public ContainerVariableResolver() {
			super();
		}
		
		public ContainerVariableResolver(final ResourceVariablesUtil util) {
			super(util);
		}
		
		
		@Override
		protected IContainer toVariableResource(final IDynamicVariable variable, final IResource resource) {
			return resource.getParent();
		}
		
		
	}
	
	public static class ProjectVariableResolver extends ResourceVariableResolver {
		
		
		public ProjectVariableResolver() {
			super();
		}
		
		public ProjectVariableResolver(final ResourceVariablesUtil util) {
			super(util);
		}
		
		
		@Override
		protected IProject toVariableResource(final IDynamicVariable variable, final IResource resource) {
			return resource.getProject();
		}
		
		
	}
	
	
	protected ResourceVariablesUtil fUtil;
	
	
	public ResourceVariableResolver() {
	}
	
	public ResourceVariableResolver(final ResourceVariablesUtil util) {
		fUtil = util;
	}
	
	
	@Override
	public String resolveValue(final IDynamicVariable variable, final String argument)
			throws CoreException {
		return getResource(variable, argument).getFullPath().toString();
	}
	
	
	protected IResource getResource(final IDynamicVariable variable, final String argument)
			throws CoreException {
		IResource resource;
		if (argument == null) {
			resource = (fUtil != null) ?
					fUtil.getSelectedResource() : ResourceVariablesUtil.fetchSelectedResource();
			if (resource == null) {
				throw new CoreException(new Status(IStatus.ERROR, UIMiscellanyPlugin.PLUGIN_ID,
						NLS.bind(Variable_error_Resource_EmptySelection_message,
								variable.getName() )));
			}
		}
		else {
			final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			final IPath path;
			if (!root.getFullPath().isValidPath(argument) ||
					(path = new Path(argument)).isEmpty()
					|| path.getDevice() != null) {
				throw new CoreException(new Status(IStatus.ERROR, UIMiscellanyPlugin.PLUGIN_ID,
						NLS.bind(Messages.Variable_error_Resource_InvalidPath_message, 
								variable.getName(), argument )));
			}
			resource = ResourcesPlugin.getWorkspace().getRoot().findMember(path);
			if (resource == null) {
				throw new CoreException(new Status(IStatus.ERROR, UIMiscellanyPlugin.PLUGIN_ID,
						NLS.bind(Messages.Variable_error_Resource_NonExisting_message, 
								variable.getName(), argument )));
			}
		}
		resource = toVariableResource(variable, resource);
		if (resource == null || !resource.exists()) {
			throw new CoreException(new Status(IStatus.ERROR, UIMiscellanyPlugin.PLUGIN_ID,
					NLS.bind(Messages.Variable_error_Resource_NonExisting_message, 
							variable.getName(), argument )));
		}
		return resource;
	}
	
	protected IResource toVariableResource(final IDynamicVariable variable, final IResource resource) {
		return resource;
	}
	
	protected String toVariableValue(final IDynamicVariable variable, final IResource resource) throws CoreException {
		final String name = variable.getName();
		if (name.endsWith("_loc")) { //$NON-NLS-1$
			final URI uri = resource.getLocationURI();
			if (uri != null) {
				final File file = EFS.getStore(uri).toLocalFile(0, null);
				if (file != null) {
					return file.getAbsolutePath();
				}
			}
			throw new CoreException(new Status(IStatus.ERROR, UIMiscellanyPlugin.PLUGIN_ID,
					NLS.bind(Messages.Variable_error_Resource_InvalidPath_message, 
							variable.getName(), resource.getFullPath().toString() )));
		}
		else if (name.endsWith("_path")) { //$NON-NLS-1$
			return resource.getFullPath().toString();
		} 
		else if (name.endsWith("_name")) { //$NON-NLS-1$
			return resource.getName();
		}
		throw new IllegalStateException(variable.getName());
	}
	
}
