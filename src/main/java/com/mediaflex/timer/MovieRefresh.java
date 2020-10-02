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
import com.mediaflex.controller.MovieController;
import com.mediaflex.util.KeyManager;
import com.mediaflex.util.MediaAgentUtil;

public class MovieRefresh extends TimerTask {
	
	private Logger log = LoggerFactory.getLogger(MovieRefresh.class);
	private MediaAgentUtil util = new MediaAgentUtil();
	
	private File refreshDir;
	
	public MovieRefresh(File refreshDir) {
		this.refreshDir = refreshDir;
	}

	@Override
	public void run() {
		log.info("================== MOVIE REFRESH START =======================");
		start(refreshDir);
		log.info("=================== MOVIE REFRESH END ========================");
	}
	
	public void start(File refreshDir) {
		
		MovieController.moviefiles.clear();
		getAllFile(refreshDir);
		chmod(refreshDir.getAbsolutePath(), MAConsts.PERMISSION_777);
		
	}
	
	public void getAllFile(File file) {
		
		try {
			ConcurrentHashMap<String, File> movieFile = new ConcurrentHashMap<>();
			
			//������ ��� ����Ʈ�� ����
			if(file.isFile()) {
//				String key = FilenameUtils.removeExtension(file.getName());
				String key = new KeyManager().getMovieKey(file, false);
				String ext = FilenameUtils.getExtension(file.getName());
				
				if(util.isMediaFile(file)) {
					movieFile.put(MAConsts.CONTENT, file);
					if(!MovieController.moviefiles.containsKey(key)) {
						MovieController.moviefiles.put(key, movieFile);
					} else {
						MovieController.moviefiles.get(key).putAll(movieFile);
					}
				}
				else if(util.isSubtitle(file)) {
					List<String> subtitleExtList = Arrays.asList(MAConsts.subtitleExts);
					for(String subExt : subtitleExtList) {
						if(ext.equalsIgnoreCase(subExt)) {
							movieFile.put(subExt, file);
							if(!MovieController.moviefiles.containsKey(key)) {
								MovieController.moviefiles.put(key, movieFile);
							} else {
								MovieController.moviefiles.get(key).putAll(movieFile);
							}
						}
					}
				}
				
			//���丮�� ��� �ٽ� �˻�
			} else if(file.isDirectory()) {
//				String key = file.getName();
				String key = new KeyManager().getMovieKey(file, true);
				File[] fileList = file.listFiles();
				//���� ������ ���� ��� Recursive
				if(fileList.length > 0) {
					if(!file.equals(MAConsts.MOVIE_DIR)) {
						movieFile.put(MAConsts.DIRECTORY, file);
						if(!MovieController.moviefiles.containsKey(key)) {
							MovieController.moviefiles.put(key, movieFile);
						} else {
							MovieController.moviefiles.get(key).putAll(movieFile);
						}
					}
				}
				for(File tempFile : fileList) {
					getAllFile(tempFile);
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
