package com.mediaflex.controller;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import com.mediaflex.Listener.SubtitleMonitorListener;

public class SubtitleController implements FileController {
	
	public static ExecutorService subtitleThreadPool = Executors.newFixedThreadPool(30);
	public static Map<String, ConcurrentHashMap<String, File>> subtitlefiles = new ConcurrentHashMap<String, ConcurrentHashMap<String ,File>>();

	private File subtitleDir;
	
	public SubtitleController(File subtitleDir) {
		this.subtitleDir = subtitleDir;
	}
	
	@Override
	public void run() {
		try {
			
			FileAlterationObserver 	observer 	= new FileAlterationObserver(subtitleDir);
			FileAlterationMonitor 	monitor 	= new FileAlterationMonitor(5000);
			FileAlterationListener 	listener 	= new SubtitleMonitorListener();
			
			observer.addListener(listener);
			monitor.addObserver(observer);
			
			monitor.start();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
