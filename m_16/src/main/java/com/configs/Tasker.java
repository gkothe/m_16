package com.configs;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.funcs.Thread_OnOffEmp;

public class Tasker implements ServletContextListener {


	private Thread_OnOffEmp myThread = null;

    public void contextInitialized(ServletContextEvent sce) {
        if ((myThread == null) || (!myThread.isAlive())) {
            myThread = new Thread_OnOffEmp();
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