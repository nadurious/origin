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
import com.mediaflex.controller.TvshowController;
import com.mediaflex.util.KeyManager;
import com.mediaflex.util.MediaAgentUtil;

public class TvshowRefresh extends TimerTask {
	
	private Logger log = LoggerFactory.getLogger(TvshowRefresh.class);
	private MediaAgentUtil util = new MediaAgentUtil();
	
	private File refreshDir;
	
	public TvshowRefresh(File refreshDir) {
		this.refreshDir = refreshDir;
	}

	@Override
	public void run() {
		log.info("================== TVSHOW REFRESH START ======================");
		start(refreshDir);
		log.info("=================== TVSHOW REFRESH END =======================");
	}
	
	public void start(File refreshDir) {
		
		TvshowController.tvshowfiles.clear();
		getAllFile(MAConsts.TV_DIR);
		chmod(refreshDir.getAbsolutePath(), MAConsts.PERMISSION_777);
		
	}
	
	public void getAllFile(File file) {
		
		try {
			ConcurrentHashMap<String, File> tvshowFile = new ConcurrentHashMap<>();
			
			//������ ��� ����Ʈ�� ����
			if(file.isFile()) {
				
				//�ø��� ������ ������
				if(util.hasSiriesPattern(file)) {
//					String key = util.getPureSiriesName(file.getName());
					String key = new KeyManager().getTvKey(file, false);
					String fileKey = util.getRegStr(file.getName(), MAConsts.SEASON_PATTERN) + util.getRegStr(file.getName(), MAConsts.SIRIES_PATTERN);
					
					String ext = FilenameUtils.getExtension(file.getName());
					
					if(util.isMediaFile(file)) {
						ConcurrentHashMap<String, File> files = new ConcurrentHashMap<>();
						files.put(fileKey, file);
						
						//Ű���� ������
						if(!TvshowController.tvshowfiles.containsKey(key)) {
							TvshowController.tvshowfiles.put(key, files);
						} else {
							TvshowController.tvshowfiles.get(key).putAll(files);
						}
						log.debug(TvshowController.tvshowfiles.toString());
						return;
					} else if(util.isSubtitle(file)) {
						
						ConcurrentHashMap<String, File> files = new ConcurrentHashMap<>();
						
						List<String> subtitleExtList = Arrays.asList(MAConsts.subtitleExts);
						for(String subExt : subtitleExtList) {
							if(ext.equalsIgnoreCase(subExt)) {
								String subKey = fileKey + "_" + subExt;
								files.put(subKey, file);
								if(!TvshowController.tvshowfiles.containsKey(key)) {
									TvshowController.tvshowfiles.put(key, files);
								} else {
									TvshowController.tvshowfiles.get(key).putAll(files);
								}
								log.debug(TvshowController.tvshowfiles.toString());
							}
						}
					}
				}
				//��¥ ���ϸ� ������
				else if(util.hasDatePattern(file) && !util.hasSiriesPattern(file)) {
					
				}
				return;
				
			//���丮�� ��� �ٽ� �˻�
			} else if(file.isDirectory()) {
//				String key = file.getName();
				String key = new KeyManager().getTvKey(file, true);
				
				File[] fileList = file.listFiles();
				//���� ������ ���� ��� Recursive
				if(fileList.length > 0) {
					if(!file.equals(MAConsts.TV_DIR)) {
						tvshowFile.put(MAConsts.DIRECTORY, file);
						if(!TvshowController.tvshowfiles.containsKey(key)) {
							TvshowController.tvshowfiles.put(key, tvshowFile);
						} else {
							TvshowController.tvshowfiles.get(key).putAll(tvshowFile);
						}
					}
					for(File tempFile : fileList) {
						getAllFile(tempFile);
					}
				}
			}
		} catch (Exception e) {
			log.info(e.toString());
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




















