package com.mediaflex.Listener;

import java.io.File;

import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mediaflex.constants.MAConsts;
import com.mediaflex.controller.TvshowController;
import com.mediaflex.filehandler.TvshowHandler;

public class TvshowMonitorListener extends FileAlterationListenerAdaptor {
	
	private Logger log = LoggerFactory.getLogger(TvshowMonitorListener.class);
	
	@Override
	public void onFileCreate(File file) {
		log.debug("onFileCreate : " + file.getAbsolutePath());
		
		TvshowController.tvshowThreadPool.execute(new TvshowHandler(MAConsts.CREATE, file));
	}
	
	@Override
	public void onFileChange(File file) {
		log.debug("onFileChange : " + file.getAbsolutePath());
		
		TvshowController.tvshowThreadPool.execute(new TvshowHandler(MAConsts.CHANGE, file));
	}
	
	@Override
	public void onFileDelete(File file) {
		log.debug("onFileDelete : " + file.getAbsolutePath());
		
		TvshowController.tvshowThreadPool.execute(new TvshowHandler(MAConsts.DELETE, file));
	}
	
	@Override
	public void onDirectoryCreate(File file) {
		log.debug("onDirectoryCreate : " + file.getAbsolutePath());
		
		TvshowController.tvshowThreadPool.execute(new TvshowHandler(MAConsts.CREATE, file, true));
	}
	
	@Override
	public void onDirectoryChange(File file) {
		log.debug("onDirectoryChange : " + file.getAbsolutePath());
		
		TvshowController.tvshowThreadPool.execute(new TvshowHandler(MAConsts.CHANGE, file, true));
	}
	
	@Override
	public void onDirectoryDelete(File file) {
		log.debug("onDirectoryDelete : " + file.getAbsolutePath());
		
		TvshowController.tvshowThreadPool.execute(new TvshowHandler(MAConsts.DELETE, file, true));
	}
	
}

















