package com.mediaflex.timer;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mediaflex.constants.MAConsts;
import com.mediaflex.controller.SubtitleController;
import com.mediaflex.util.KeyManager;
import com.mediaflex.util.MediaAgentUtil;

public class SubtitleRefresh extends TimerTask {
	
	private Logger log = LoggerFactory.getLogger(SubtitleRefresh.class);
	private MediaAgentUtil util = new MediaAgentUtil();
	
	private File refreshDir;
	
	public SubtitleRefresh(File refreshDir) {
		this.refreshDir = refreshDir;
	}

	@Override
	public void run() {
		log.info("================= SUBTITLE REFRESH START =====================");
		start(refreshDir);
		log.info("================== SUBTITLE REFRESH END ======================");
	}
	
	public void start(File refreshDir) {
		
		SubtitleController.subtitlefiles.clear();
		getAllFile(refreshDir);
		chmod(refreshDir.getAbsolutePath(), MAConsts.PERMISSION_777);
		
	}
	
	public void getAllFile(File file) {
		try {
		//������ ��� ����Ʈ�� ����
		if(file.isFile()) {
			
//			String key = FilenameUtils.removeExtension(file.getName());
			String key = new KeyManager().getMovieKey(file, false);
			String ext = FilenameUtils.getExtension(file.getName());
			
			if(util.isSubtitle(file)) {
				ConcurrentHashMap<String, File> subFile = new ConcurrentHashMap<String, File>();
				
				List<String> subtitleExtList = Arrays.asList(MAConsts.subtitleExts);
				for(String subExt : subtitleExtList) {
					if(ext.equalsIgnoreCase(subExt)) {
						//�ø��� ������ ���� ��
						if(util.hasSiriesPattern(file)) {
//							key = util.getPureSiriesName(file.getName());
							key = new KeyManager().getTvKey(file, false);
							String fileKey = util.getRegStr(file.getName(), MAConsts.SEASON_PATTERN) + util.getRegStr(file.getName(), MAConsts.SIRIES_PATTERN);
							String subKey = fileKey + "_" + subExt;
							subFile.put(subKey, file);
							if(!SubtitleController.subtitlefiles.containsKey(key)) {
								SubtitleController.subtitlefiles.put(key, subFile);
							} else {
								SubtitleController.subtitlefiles.get(key).putAll(subFile);
							}
						} else {
							subFile.put(subExt, file);
							if(!SubtitleController.subtitlefiles.containsKey(key)) {
								SubtitleController.subtitlefiles.put(key, subFile);
							} else {
								SubtitleController.subtitlefiles.get(key).putAll(subFile);
							}
						}
					}
				}
				
				log.debug(SubtitleController.subtitlefiles.toString());
			}
		//���丮�� ��� �ٽ� �˻�
		} else if(file.isDirectory()) {
			File[] fileList = file.listFiles();
			//���� ������ ���� ��� Recursive
			if(fileList.length > 0) {
				for(File tempFile : fileList) {
					getAllFile(tempFile);
				}
			}
		} 
		} catch (Exception e) {
			log.error(e.toString());
			e.printStackTrace();
		}
	}//getAllFile
	
	public boolean chmod(String path, String auth) {
		String command = "chmod -R " + auth + " \"" + path + "\"";
		String result = util.runShell(command);
		
		if(result.equals(MAConsts.SUCCESS)) {
			return true;
		} 
		return false;
	}
	
}
