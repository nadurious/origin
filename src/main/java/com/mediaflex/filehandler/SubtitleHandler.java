package com.mediaflex.filehandler;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mediaflex.constants.MAConsts;
import com.mediaflex.controller.MovieController;
import com.mediaflex.controller.SubtitleController;
import com.mediaflex.controller.TvshowController;
import com.mediaflex.util.MediaAgentUtil;

public class SubtitleHandler extends AbstractFileHandler {
	
	private boolean isDirectory = false;
	
	private MediaAgentUtil util = new MediaAgentUtil();
	private Logger log = LoggerFactory.getLogger(SubtitleHandler.class);

	public SubtitleHandler(String stat, File file) {
		super(stat, file);
	}
	
	public SubtitleHandler(String stat, File file, boolean isDirectory) {
		super(stat, file);
		this.isDirectory = isDirectory;
	}
	
	@Override
	public void run() {
		
		try {
			
//			String key = util.getPuerSiriesName(file.getName());
			String key = file.getName();
			String ext = FilenameUtils.getExtension(file.getName());
			String fileKey = util.getRegStr(file.getName(), MAConsts.SEASON_PATTERN) + util.getRegStr(file.getName(), MAConsts.SIRIES_PATTERN);
			
			if(stat.equals(MAConsts.CREATE)) {
				try {
					
					if(fileKey.endsWith(".smi")) {
						File createdSrtFile = util.createSRTFile(file);
						if(createdSrtFile != null) {
							log.info("[" + createdSrtFile.getName() + "] 파일이 생성되었습니다.");
						} else {
							log.error("[" + file.getName() + "] 파일의 srt변환을 실패했습니다.");
						}
					}
					
					ConcurrentHashMap<String, File> subFile = new ConcurrentHashMap<String, File>();
					
					log.debug("KEY : [" + key + "], FILEKEY : [" + fileKey + "], ext : [" + ext + "]");
					
					List<String> subtitleExtList = Arrays.asList(MAConsts.subtitleExts);
					for(String subExt : subtitleExtList) {
						if(ext.equalsIgnoreCase(subExt)) {
							//�ø��� ������ ���� ��
							if(util.hasSiriesPattern(file)) {
//								key = util.getSiriesName(file.getName());
//								String seasonKey = util.getRegStr(file.getName(), MAConsts.SEASON_PATTERN);
//								String epiKey = util.getRegStr(file.getName(), MAConsts.SIRIES_PATTERN);
//								String fileKey = util.getRegStr(file.getName(), MAConsts.SEASON_PATTERN) + util.getRegStr(file.getName(), MAConsts.SIRIES_PATTERN);
								key = util.getPureSiriesName(file.getName());
								String subKey = fileKey + "_" + subExt;
								subFile.put(subKey, file);
								log.debug("KEY : [" + key + "], FILEKEY : [" + fileKey + "], ext : [" + ext + "]");
								if(!SubtitleController.subtitlefiles.containsKey(key)) {
									SubtitleController.subtitlefiles.put(key, subFile);
								} else {
									SubtitleController.subtitlefiles.get(key).putAll(subFile);
								}
							} else {
								key = FilenameUtils.removeExtension(file.getName());
								subFile.put(subExt, file);
								log.debug("KEY : [" + key + "], FILEKEY : [" + fileKey + "], ext : [" + ext + "]");
								if(!SubtitleController.subtitlefiles.containsKey(key)) {
									SubtitleController.subtitlefiles.put(key, subFile);
								} else {
									SubtitleController.subtitlefiles.get(key).putAll(subFile);
								}
							}
						}
					}
					
					log.info("ADD : [" + file.getParentFile().getName() + file.separator + file.getName() +"]");
					log.debug(SubtitleController.subtitlefiles.toString());
					
					chmod(file.getAbsolutePath(), MAConsts.PERMISSION_777);
					
					//�ڸ������� �Ű��ִ� �۾�
					//TV �ڸ��� ���
					if(util.hasSiriesPattern(file)){
						putTvshowSubtitle();
					}
					//��ȭ �ڸ��� ���
					else {
						putMovieSubtitle();
					}
				} catch (Exception e) {
					log.error(e.toString());
					e.printStackTrace();
				}
				
			}//CREATE
			
			else if(stat.equals(MAConsts.CHANGE)) {
			}//CHANGE
			
			else if(stat.equals(MAConsts.DELETE)) {
				
				List<String> subtitleExtList = Arrays.asList(MAConsts.subtitleExts);
				for(String subExt : subtitleExtList) {
					//�ø��� ������ ���� ��
					if(util.hasSiriesPattern(file)) {
//						String fileKey = util.getRegStr(file.getName(), MAConsts.SEASON_PATTERN) + util.getRegStr(file.getName(), MAConsts.SIRIES_PATTERN);
						key = util.getPureSiriesName(file.getName());
						String subKey = fileKey + "_" + subExt;
						log.debug("KEY : [" + key + "], FILEKEY : [" + fileKey + "], ext : [" + ext + "]");
						SubtitleController.subtitlefiles.get(key).remove(subKey);
						log.info("REMOVE : [" + file.getParentFile().getName() + file.separator +file.getName() + "]");
					} else {
						log.debug("KEY : [" + key + "], FILEKEY : [" + fileKey + "], ext : [" + ext + "]");
						key = FilenameUtils.removeExtension(file.getName());
						SubtitleController.subtitlefiles.get(key).remove(subExt);
						log.info("REMOVE : [" + file.getParentFile().getName() + file.separator +file.getName() + "]");
					}
				}
				
//				log.info("REMOVE : [" + file.getParentFile().getName() + file.separator +file.getName() + "]");
				log.debug("REMOVE : " + SubtitleController.subtitlefiles.toString());
				
//				if(SubtitleController.subtitlefiles.get(key).isEmpty()) {
//					SubtitleController.subtitlefiles.remove(key);
//				}
			} 
			else {
				throw new Exception("SubtitleHandler : ���°��� ������ �ʾҽ��ϴ�.");
			}//DELETE
			
			
			
			return;
		} catch (Exception e) {
			log.error(e.toString());
			e.printStackTrace();
		}
		
	}

	private void putTvshowSubtitle() {

		String key = util.getPureSiriesName(file.getName());
		String fileKey = util.getRegStr(file.getName(), MAConsts.SEASON_PATTERN) + util.getRegStr(file.getName(), MAConsts.SIRIES_PATTERN);
		
		String logKey = key.substring(0, key.length()/2) + "...." + FilenameUtils.getExtension(file.getName());
		
		ConcurrentHashMap<String, File> tvshowFile = null;
		if(TvshowController.tvshowfiles.containsKey(key)) {
			tvshowFile = TvshowController.tvshowfiles.get(key);
		} else {
			log.info(logKey + "... ������ �̵�� ������ �������� �ʽ��ϴ�.");
			return;
		}
		
		ConcurrentHashMap<String, File> subtitleFile = null;
		if(SubtitleController.subtitlefiles.containsKey(key)) {
			subtitleFile = SubtitleController.subtitlefiles.get(key);	
		} else {
			log.info(logKey + "... ������ �ڸ� ������ �������� �ʽ��ϴ�.");
			return;
		}
		
		if(tvshowFile.get(MAConsts.DIRECTORY) != null && subtitleFile != null) {
			List<String> subtitleExtList = Arrays.asList(MAConsts.subtitleExts);
			for(String subExt : subtitleExtList) {
				String subKey = fileKey + "_" + subExt;
				if(subtitleFile.containsKey(subKey) && !tvshowFile.containsKey(subKey)) {
					copy(subtitleFile.get(subKey).getAbsolutePath()
							, tvshowFile.get(MAConsts.DIRECTORY).getAbsolutePath() + file.separator + subtitleFile.get(subKey).getName());
					log.info("COPY : [" + subtitleFile.get(subKey).getParentFile().getName() + file.separator + subtitleFile.get(subKey).getName() 
							+ "] -> [" + tvshowFile.get(MAConsts.DIRECTORY).getName() + file.separator + subtitleFile.get(subKey).getName() + "]");
				}
			}
		}
		else {
			if(tvshowFile.get(MAConsts.DIRECTORY) == null) {
				log.error(file.getName() + " �� �ڸ������� �̵��� ���丮�� �����ϴ�.");
			} else if(subtitleFile == null) {
				log.error(file.getName() + " �� �ڸ� ������ �����ϴ�.");
			} else if(tvshowFile == null) {
				log.error(file.getName() + " �� �̵�� ������ �����ϴ�.");
			}
		}
	}

	private void putMovieSubtitle() {
		
		String key = FilenameUtils.removeExtension(file.getName());
		
		String logKey = key.substring(0, key.length()/2) + "...." + FilenameUtils.getExtension(file.getName());
		
		ConcurrentHashMap<String, File> movieFile = null;
		if(MovieController.moviefiles.containsKey(key)) {
			movieFile = MovieController.moviefiles.get(key);
		} else {
			log.info(logKey + " ������ �̵�� ������ �������� �ʽ��ϴ�.");
			return;
		}
		
		ConcurrentHashMap<String, File> subtitleFile = null;
		if(SubtitleController.subtitlefiles.containsKey(key)) {
			subtitleFile = SubtitleController.subtitlefiles.get(key);	
		} else {
			log.info(logKey + " ������ �ڸ� ������ �������� �ʽ��ϴ�.");
			return;
		}
		
		if(movieFile.get(MAConsts.DIRECTORY) != null && subtitleFile != null) {
			List<String> subtitleExtList = Arrays.asList(MAConsts.subtitleExts);
			for(String subExt : subtitleExtList) {
				if(!movieFile.containsKey(subExt) && subtitleFile.containsKey(subExt)) {
					copy(subtitleFile.get(subExt).getAbsolutePath()
							, movieFile.get(MAConsts.DIRECTORY).getAbsolutePath() + file.separator + subtitleFile.get(subExt).getName());
					log.info("COPY : [" + subtitleFile.get(subExt).getParentFile().getName() + file.separator + subtitleFile.get(subExt).getName() 
							+ "] -> [" + movieFile.get(MAConsts.DIRECTORY).getName() + file.separator + subtitleFile.get(subExt).getName() + "]");
				}
			}
		}
		else {
			if(movieFile.get(MAConsts.DIRECTORY) == null) {
				log.error(file.getName() + " �� �ڸ������� �̵��� ���丮�� �����ϴ�.");
			} else if(subtitleFile == null) {
				log.error(file.getName() + " �� �ڸ� ������ �����ϴ�.");
			} else if(movieFile == null) {
				log.error(file.getName() + " �� �̵�� ������ �����ϴ�.");
			}
		}
	}
		
}






















