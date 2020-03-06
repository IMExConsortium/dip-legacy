package edu.ucla.mbi.dip;

/*==========================================================================
 * $HeadURL:: https://imex.mbi.ucla.edu/svn/dip-ws/trunk/dip-legacy/src/ma#$
 * $Id:: WSContextManager.java 2616 2012-08-07 05:17:42Z lukasz            $
 * Version: $Rev:: 2616                                                    $
 *==========================================================================
 *
 * WSContextManager: 
 *
 *========================================================================== */

import javax.servlet.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class WSContextManager 
    implements javax.servlet.ServletContextListener {

    public void contextInitialized(javax.servlet.ServletContextEvent sce){
	System.out.println("WSContextManager: contextInitialized");
    }

    public void contextDestroyed(ServletContextEvent sce) {
	ClassLoader contextClassLoader = 
	    Thread.currentThread().getContextClassLoader();
	LogFactory.release(contextClassLoader);
	System.out.println("WSContextManager: contextDestroyed");
    }
}
