/*=============================================================================#
 # Copyright (c) 2007-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.debug.ui.config;

import java.util.Comparator;

import com.ibm.icu.text.Collator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.RefreshTab;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.statushandlers.StatusManager;

import de.walware.ecommons.ICommonStatusConstants;
import de.walware.ecommons.debug.internal.ui.Messages;
import de.walware.ecommons.ui.internal.UIMiscellanyPlugin;


/**
 * Methods for common task when working with launch configurations and processes
 */
public class LaunchConfigUtil {
	
	
	/**
	 * Refreshes resources as specified by a launch configuration, when
	 * an associated process terminates.
	 */
	private static class BackgroundResourceRefresher implements IDebugEventSetListener  {
		
		
		private final ILaunchConfiguration fConfiguration;
		private IProcess fProcess;
		
		
		public BackgroundResourceRefresher(final ILaunchConfiguration configuration, final IProcess process) {
			this.fConfiguration= configuration;
			this.fProcess= process;
			
			initialize();
		}
		
		/**
		 * If the process has already terminated, resource refreshing is scheduled
		 * immediately. Otherwise, refreshing is done when the process terminates.
		 */
		private synchronized void initialize() {
			DebugPlugin.getDefault().addDebugEventListener(this);
			if (this.fProcess.isTerminated()) {
				sheduleRefresh();
			}
		}
		
		@Override
		public void handleDebugEvents(final DebugEvent[] events) {
			for (int i= 0; i < events.length; i++) {
				final DebugEvent event= events[i];
				if (event.getSource() == this.fProcess && event.getKind() == DebugEvent.TERMINATE) {
					sheduleRefresh();
					return;
				}
			}
		}
		
		/**
		 * Submits a job to do the refresh
		 */
		protected synchronized void sheduleRefresh() {
			if (this.fProcess != null) {
				DebugPlugin.getDefault().removeDebugEventListener(this);
				this.fProcess= null;
				final Job job= new Job(Messages.BackgroundResourceRefresher_Job_name) {
					@Override
					public IStatus run(final IProgressMonitor monitor) {
						try {
							RefreshTab.refreshResources(BackgroundResourceRefresher.this.fConfiguration, monitor);
						}
						catch (final CoreException e) {
							StatusManager.getManager().handle(new Status(
									ICommonStatusConstants.LAUNCHING, UIMiscellanyPlugin.PLUGIN_ID,
									NLS.bind("An error occurred when refreshing resources for launch configuration ''{0}''.", BackgroundResourceRefresher.this.fConfiguration.getName()), e));
							return e.getStatus();
						}
						return Status.OK_STATUS;
					}
				};
				job.schedule();
			}
		}
	}
	
	
	/**
	 * Manages resource refresh according to the settings in launch configuration.
	 */
	public static void launchResourceRefresh(final ILaunchConfiguration configuration,
			final IProcess process, final IProgressMonitor monitor) throws CoreException {
		if (CommonTab.isLaunchInBackground(configuration)) {
			// refresh resources after process finishes
			if (RefreshTab.getRefreshScope(configuration) != null) {
				new BackgroundResourceRefresher(configuration, process);
			}
		} else {
			// wait for process to exit
			while (!process.isTerminated()) {
				try {
					if (monitor.isCanceled()) {
						process.terminate();
						break;
					}
					Thread.sleep(50);
				}
				catch (final InterruptedException e) {
					// continue loop, monitor and process is checked
				}
			}
			
			// refresh resources
			RefreshTab.refreshResources(configuration, monitor);
		}
	}
	
	
	public static class LaunchConfigurationComparator implements Comparator<ILaunchConfiguration> {
		
		private final Collator collator= Collator.getInstance();
		
		@Override
		public int compare(final ILaunchConfiguration c1, final ILaunchConfiguration c2) {
			return this.collator.compare(c1.getName(), c2.getName());
		}
		
	};
	
	
	private LaunchConfigUtil() {}
	
}
