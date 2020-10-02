package com.mediaflex.controller;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import com.mediaflex.Listener.TvshowMonitorListener;

public class TvshowController implements FileController {
	
	public static ExecutorService tvshowThreadPool = Executors.newCachedThreadPool();
	public static Map<String, ConcurrentHashMap<String, File>> tvshowfiles = new ConcurrentHashMap<String, ConcurrentHashMap<String ,File>>();

	private File tvshowDir;
	
	public TvshowController(File tvshowDir) {
		this.tvshowDir = tvshowDir;
	}
	
	@Override
	public void run() {
		try {
			
			FileAlterationObserver 	observer 	= new FileAlterationObserver(tvshowDir);
			FileAlterationMonitor 	monitor 	= new FileAlterationMonitor(5000);
			FileAlterationListener 	listener 	= new TvshowMonitorListener();
			
			observer.addListener(listener);
			monitor.addObserver(observer);
			
			monitor.start();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
