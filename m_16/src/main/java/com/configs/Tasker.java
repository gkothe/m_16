package com.configs;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.funcs.Thread_NotPedidoFim;
import com.funcs.Thread_OnOffEmp;

public class Tasker implements ServletContextListener {

	private Thread_OnOffEmp myThread = null;
	private Thread_NotPedidoFim myThread2 = null;

	public void contextInitialized(ServletContextEvent sce) {

		if ((myThread == null) || (!myThread.isAlive())) {
			myThread = new Thread_OnOffEmp();
			myThread.start();
		}
		if ((myThread2 == null) || (!myThread2.isAlive())) {
			myThread2 = new Thread_NotPedidoFim();
			myThread2.start();
		}
	}

	public void contextDestroyed(ServletContextEvent sce) {
		try {
			myThread.rodar = false;
			myThread.doShutdown();
			myThread.interrupt();
		} catch (Exception ex) {
		}

		try {
			myThread2.rodar = false;
			myThread2.doShutdown();
			myThread2.interrupt();

		} catch (Exception e) {

			// TODO: handle exception
		}
	}

}