package com.configs;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class Tasker implements ServletContextListener {


	private Thread_ext myThread = null;

    public void contextInitialized(ServletContextEvent sce) {
        if ((myThread == null) || (!myThread.isAlive())) {
            myThread = new Thread_ext();
            myThread.start();
        }
    }

    public void contextDestroyed(ServletContextEvent sce){
        try {
        	
            myThread.doShutdown();
            myThread.interrupt();
        } catch (Exception ex) {
        }
    }

	
}