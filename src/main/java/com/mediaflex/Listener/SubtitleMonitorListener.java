package com.mediaflex.Listener;

import java.io.File;

import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mediaflex.constants.MAConsts;
import com.mediaflex.controller.SubtitleController;
import com.mediaflex.filehandler.SubtitleHandler;

public class SubtitleMonitorListener extends FileAlterationListenerAdaptor {
	
	private Logger log = LoggerFactory.getLogger(SubtitleMonitorListener.class);
	
	@Override
	public void onFileCreate(File file) {
		log.debug("onFileCreate : " + file.getAbsolutePath());
		
		SubtitleController.subtitleThreadPool.execute(new SubtitleHandler(MAConsts.CREATE, file));
	}
	
	@Override
	public void onFileChange(File file) {
		log.debug("onFileChange : " + file.getAbsolutePath());
		
		SubtitleController.subtitleThreadPool.execute(new SubtitleHandler(MAConsts.CHANGE, file));
	}
	
	@Override
	public void onFileDelete(File file) {
		log.debug("onFileDelete : " + file.getAbsolutePath());
		
		SubtitleController.subtitleThreadPool.execute(new SubtitleHandler(MAConsts.DELETE, file));
	}
	
	@Override
	public void onDirectoryCreate(File file) {
		log.debug("onDirectoryCreate : " + file.getAbsolutePath());
		
		SubtitleController.subtitleThreadPool.execute(new SubtitleHandler(MAConsts.CREATE, file, true));
	}
	
	@Override
	public void onDirectoryChange(File file) {
		log.debug("onDirectoryChange : " + file.getAbsolutePath());
		
		SubtitleController.subtitleThreadPool.execute(new SubtitleHandler(MAConsts.CHANGE, file, true));
	}
	
	@Override
	public void onDirectoryDelete(File file) {
		log.debug("onDirectoryDelete : " + file.getAbsolutePath());
		
		SubtitleController.subtitleThreadPool.execute(new SubtitleHandler(MAConsts.DELETE, file, true));
	}
	
}

















