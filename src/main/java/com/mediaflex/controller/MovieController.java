package com.mediaflex.controller;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import com.mediaflex.Listener.MovieMonitorListener;

public class MovieController implements FileController {
	
	public static ExecutorService movieThreadPool = Executors.newCachedThreadPool();
	public static Map<String, ConcurrentHashMap<String, File>> moviefiles = new ConcurrentHashMap<String, ConcurrentHashMap<String ,File>>();
	
	private File movieDir;
	
	public MovieController(File movieDir) {
		this.movieDir = movieDir;
	}

	@Override
	public void run() {
		try {
			
			FileAlterationObserver 	observer 	= new FileAlterationObserver(movieDir);
			FileAlterationMonitor 	monitor 	= new FileAlterationMonitor(5000);
			FileAlterationListener 	listener 	= new MovieMonitorListener();
			
			observer.addListener(listener);
			monitor.addObserver(observer);
			
			monitor.start();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	
	
}
