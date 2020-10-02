package com.mediaflex.controller;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import com.mediaflex.Listener.WatchMonitorListener;

public class WatchController implements FileController {
	
	public static ExecutorService watchThreadPool = Executors.newFixedThreadPool(30);
	
	private static File watchDir;
	
	public WatchController(File watchDir) {
		this.watchDir = watchDir;
	}

	@Override
	public void run() {
		try {
			
			FileAlterationObserver 	observer 	= new FileAlterationObserver(watchDir);
			FileAlterationMonitor 	monitor 	= new FileAlterationMonitor(5000);
			FileAlterationListener 	listener 	= new WatchMonitorListener();
			
			observer.addListener(listener);
			monitor.addObserver(observer);
			
			monitor.start();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
