package net.sourceforge.nattable.internal;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;


/**
 * The activator class for the plug-in life cycle
 */
public class NatTablePlugin extends Plugin {
	
	
	public static final String PLUGIN_ID = "de.walware.thirdparty-net.sourceforge.nattable.core"; //$NON-NLS-1$
	
	
	/** The shared instance. */
	private static NatTablePlugin gPlugin;
	
	/**
	 * Returns the shared plug-in instance
	 * 
	 * @return the shared instance
	 */
	public static NatTablePlugin getDefault() {
		return gPlugin;
	}
	
	
	public NatTablePlugin() {
		gPlugin = this;
	}
	
	
	@Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		
		gPlugin = null;
	}
	
	public void log(final IStatus status) {
		final ILog log = getLog();
		if (log != null) {
			log.log(status);
		}
	}
	
}
