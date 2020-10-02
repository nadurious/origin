package com.mediaflex.Listener;

import java.io.File;

import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mediaflex.constants.MAConsts;
import com.mediaflex.controller.WatchController;
import com.mediaflex.filehandler.WatchHandler;

public class WatchMonitorListener extends FileAlterationListenerAdaptor {
	
	private Logger log = LoggerFactory.getLogger(WatchMonitorListener.class);
	
	@Override
	public void onFileCreate(File file) {
		log.debug("onFileCreate : " + file.getAbsolutePath());
		
		WatchController.watchThreadPool.execute(new WatchHandler(MAConsts.CREATE, file));
	}
	
	@Override
	public void onDirectoryChange(File file) {
		log.debug("onDirectoryChange : " + file.getAbsolutePath());
		
		WatchController.watchThreadPool.execute(new WatchHandler(MAConsts.CHANGE, file, true));
	}
}

















