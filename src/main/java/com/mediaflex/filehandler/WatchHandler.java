package com.mediaflex.filehandler;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mediaflex.constants.MAConsts;
import com.mediaflex.util.MediaAgentUtil;

public class WatchHandler extends AbstractFileHandler {
	
	boolean isDirectory = false;
	
	private MediaAgentUtil util = new MediaAgentUtil();
	private Logger log = LoggerFactory.getLogger(MovieHandler.class);

	public WatchHandler(String stat, File file) {
		super(stat, file);
	}
	
	public WatchHandler(String stat, File file, boolean isDirectory) {
		super(stat, file);
		this.isDirectory = isDirectory;
	}
	
	@Override
	public void run() {
		
		try {
			if(stat.equals(MAConsts.CREATE)) {
				
				String path = file.getAbsolutePath();

				//파일의 소유자를 변경해 준다
				chown(path);

				//미디어 파일인 경우
				if(util.isMediaFile(file)) {
					
					//TV 시리즈 인 경우
					if(util.hasSiriesPattern(file)) {
						String dest = MAConsts.TV_PATH + file.separator + file.getName();
						if(MAConsts.RENAME_USE.equalsIgnoreCase(MAConsts.TRUE)) {
							dest = MAConsts.TV_PATH + file.separator + util.renameSiries(file.getName());
							log.info("RENAME : [" + file.getName() + "] -> " + util.renameSiries(file.getName()) + "]");
						}
						File destFile = move(path, dest);
						log.info("MOVE : [" + file.getParentFile().getName() + file.separator +file.getName() 
								+ "] -> [" + destFile.getParentFile().getName() + file.separator + destFile.getName() + "]");
						return;
					} else if(util.hasDatePattern(file) && !util.hasSiriesPattern(file)) {
						String dest = MAConsts.TV_PATH + file.separator + file.getName();
						File destFile = move(path, dest);
						log.info("MOVE : [" + file.getParentFile().getName() + file.separator +file.getName() 
								+ "] -> [" + destFile.getParentFile().getName() + file.separator + destFile.getName() + "]");
						return;
					}
					//MOVIE 미디어 파일인 경우
					else {
						String ext = FilenameUtils.getExtension(file.getName());
						String destFileName = util.changeAllSpecialChar(FilenameUtils.removeExtension(file.getName()), ".");
						while(true) {
							if(destFileName.endsWith(".")) {
								destFileName = destFileName.substring(0, destFileName.length()-1);
							} else {
								destFileName = destFileName + "." + ext;
								break;
							}
						}
						String dest = MAConsts.MOVIE_PATH + file.separator+ util.changeAllSpecialChar(destFileName, ".");
						File destFile = move(path, dest);
						log.info("MOVE : [" + file.getParentFile().getName() + file.separator +file.getName() 
								+ "] -> [" + destFile.getParentFile().getName() + file.separator + destFile.getName() + "]");
						return;
					}
					
					
				} 
				//Subtitle 자막 파일 인 경우
				else if(util.isSubtitle(file)) {
					String dest = MAConsts.SUB_PATH + file.separator + file.getName();
					if(util.hasSiriesPattern(file)) {
						if(MAConsts.RENAME_USE.equalsIgnoreCase(MAConsts.TRUE)) {
							dest = MAConsts.SUB_PATH + file.separator + util.renameSiries(file.getName());
							log.info("RENAME : [" + file.getName() + "] -> " + util.renameSiries(file.getName()) + "]");
						}
					} else {
						String ext = FilenameUtils.getExtension(file.getName());
						String destFileName = util.changeAllSpecialChar(FilenameUtils.removeExtension(file.getName()), ".");
						while(true) {
							if(destFileName.endsWith(".")) {
								destFileName = destFileName.substring(0, destFileName.length()-1);
							} else {
								destFileName = destFileName + "." + ext;
								break;
							}
						}
						dest = MAConsts.SUB_PATH + file.separator + util.changeAllSpecialChar(destFileName, ".");
					}
					File destFile = move(path, dest);
					log.info("MOVE : [" + file.getParentFile().getName() + file.separator +file.getName() 
							+ "] -> [" + destFile.getParentFile().getName() + file.separator + destFile.getName() + "]");
					return;
				}
				else {
					log.info("[" + file.getAbsolutePath() + "] is out of range");
					return;
				}
			}//CREATE
			else if(stat.equals(MAConsts.CHANGE)) {
				if(isDirectory) {
					if(file.listFiles().length == 0) {
						file.delete();
					}
				}
			}//CHANGE
			else if(stat.equals(MAConsts.DELETE)) {
			}//DELETE
			else {
				throw new Exception("WatchHandler : 알 수 없는 생성값입니다.");
			}
		} catch (Exception e) {
			log.error(e.toString());
			e.printStackTrace();
			return;
		}
		
	}
	
}
