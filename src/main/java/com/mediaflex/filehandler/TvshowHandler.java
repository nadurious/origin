package com.mediaflex.filehandler;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mediaflex.constants.MAConsts;
import com.mediaflex.controller.SubtitleController;
import com.mediaflex.controller.TvshowController;
import com.mediaflex.util.KeyManager;
import com.mediaflex.util.MediaAgentUtil;

public class TvshowHandler extends AbstractFileHandler {
	
	private boolean isDirectory = false;
	
	private MediaAgentUtil util = new MediaAgentUtil();
	private Logger log = LoggerFactory.getLogger(TvshowHandler.class);

	public TvshowHandler(String stat, File file) {
		super(stat, file);
	}
	
	public TvshowHandler(String stat, File file, boolean isDirectory) {
		super(stat, file);
		this.isDirectory = isDirectory;
	}
	
	@Override
	public void run() {
		
		try {
			if(stat.equals(MAConsts.CREATE)) {
				
				try {
					
					//시리즈 패턴이 있을 때
					if(util.hasSiriesPattern(file)) {
						
//						String key = util.getPuerSiriesName(file.getName());
						String key = new KeyManager().getTvKey(file, false);
						String ext = FilenameUtils.getExtension(file.getName());
						String fileKey = util.getRegStr(file.getName(), MAConsts.SEASON_PATTERN) + util.getRegStr(file.getName(), MAConsts.SIRIES_PATTERN);
						ConcurrentHashMap<String, File> files = new ConcurrentHashMap<>();
						
						log.debug("KEY : [" + key + "], FILEKEY : [" + fileKey + "]");
						log.debug("isMedia : [" + util.isMediaFile(file) + "], isSub : [" + util.isSubtitle(file) + "], isDir : [" + isDirectory + "]");
						
						if(util.isMediaFile(file)) {
							String mkdirPath = MAConsts.TV_PATH + file.separator + key;
							//TV시리즈 디렉토리가 없을 때
							if(!TvshowController.tvshowfiles.containsKey(key) || !TvshowController.tvshowfiles.get(key).containsKey(MAConsts.DIRECTORY)) {
								File dir = mkdir(mkdirPath);
								log.info("MKDIR : [" + dir.getParentFile().getName() + file.separator +dir.getAbsolutePath() +"]");
								File mvTvshow = move(file.getAbsolutePath(), dir.getAbsolutePath() + file.separator + file.getName());
								log.info("MOVE : [" + file.getParentFile().getName() + file.separator +file.getName() + "] -> [" + mvTvshow.getParentFile().getName() + file.separator + mvTvshow.getName() + "]");
								
								files.put(MAConsts.DIRECTORY, dir);
								files.put(fileKey, mvTvshow);
								
								chmod(dir.getAbsolutePath(), MAConsts.PERMISSION_777);
							}
							//TV 시리즈 디렉토리만 있을 때
							else if(!TvshowController.tvshowfiles.get(key).containsKey(fileKey) && TvshowController.tvshowfiles.get(key).containsKey(MAConsts.DIRECTORY)) {
		
								File mvTvshow = move(file.getAbsolutePath(), mkdirPath + file.separator + file.getName());
								log.info("MOVE : [" + file.getParentFile().getName() + file.separator +file.getName() + "] -> [" + mvTvshow.getParentFile().getName() + file.separator + mvTvshow.getName() + "]");
								
								files.put(fileKey, mvTvshow);
								
								chmod(mvTvshow.getAbsolutePath(), MAConsts.PERMISSION_777);
							}
							//둘 다 아닌경우 
							else {
								File mvTvshow = move(file.getAbsolutePath(), mkdirPath + file.separator + file.getName());
								log.info("MOVE : [" + file.getParentFile().getName() + file.separator +file.getName() + "] -> [" + mvTvshow.getParentFile().getName() + file.separator + mvTvshow.getName() + "]");
								
								files.put(fileKey, mvTvshow);
								
								chmod(mvTvshow.getAbsolutePath(), MAConsts.PERMISSION_777);
							}
							
							if(!file.getParentFile().equals(MAConsts.TV_DIR)) {
								if(!TvshowController.tvshowfiles.containsKey(key)) {
									TvshowController.tvshowfiles.put(key, files);
									log.info("ADD : [" + file.getParentFile().getName() + file.separator + file.getName() +"]");
								} else {
									TvshowController.tvshowfiles.get(key).putAll(files);
									log.info("ADD : [" + file.getParentFile().getName() + file.separator + file.getName() +"]");
								}
								getTvshowSubtitle();
							}
							
							log.debug(TvshowController.tvshowfiles.toString());
							
							return;
						
						}
						else if(util.isSubtitle(file)) {
							
							List<String> subtitleExtList = Arrays.asList(MAConsts.subtitleExts);
							for(String subExt : subtitleExtList) {
								if(ext.equalsIgnoreCase(subExt)) {
									if(util.hasSiriesPattern(file)) {
										String subKey = fileKey + "_" + subExt;
										files.put(subKey, file);
										if(!TvshowController.tvshowfiles.containsKey(key)) {
											TvshowController.tvshowfiles.put(key, files);
										} else {
											TvshowController.tvshowfiles.get(key).putAll(files);
										}
									} else {
										if(!TvshowController.tvshowfiles.containsKey(key)) {
											TvshowController.tvshowfiles.put(key, files);
										} else {
											TvshowController.tvshowfiles.get(key).putAll(files);
										}
									}
								}
							}
							log.info("ADD : [" + file.getParentFile().getName() + file.separator + file.getName() +"]");
							
							log.debug(TvshowController.tvshowfiles.toString());
						}
					}
					//날짜 패턴은 있지만 시리즈 패턴이 없을 때
					else if(util.hasDatePattern(file) && !util.hasSiriesPattern(file)) {
						//other 리스트를 뒤져서 있는지 찾아본다.	
						List<String> otherList = Arrays.asList(MAConsts.others);
						for(String other : otherList) {
							if(file.getName().contains(other.toLowerCase())) {
								File rootDir = new File(MAConsts.TV_PATH + file.separator + other);
								if(!rootDir.exists()) {
									rootDir = mkdir(MAConsts.TV_PATH + file.separator + other);
									log.info("MKDIR : [" + rootDir.getParentFile().getName() + file.separator + rootDir.getName() +"]");
									chmod(rootDir.getAbsolutePath(), MAConsts.PERMISSION_777);
								}
								
								String key = new KeyManager().getTvKey(file, false);
								File nodeDir = new File(rootDir.getAbsolutePath() + file.separator + key);
								if(!nodeDir.exists()) {
									nodeDir = mkdir(rootDir.getAbsolutePath() + file.separator + key);
									log.info("MKDIR : [" + rootDir.getParentFile().getName() + file.separator + key +"]");
									chmod(nodeDir.getAbsolutePath(), MAConsts.PERMISSION_777);
								}
								
								move(file.getAbsolutePath(), nodeDir.getAbsolutePath() + file.separator + file.getName());
								chmod(nodeDir.getAbsolutePath() + file.separator + file.getName(), MAConsts.PERMISSION_777);
								log.info("MOVE : [" + file.getParentFile().getName() + file.separator + file.getName() + "] -> [" + rootDir.getName() + file.separator + nodeDir.getName() + file.separator + file.getName() + "]");
							}
						}
						
						log.debug(TvshowController.tvshowfiles.toString());
						
						return;
					}
				
				} catch (Exception e) {
					log.error(e.toString());
					e.printStackTrace();
				}
			}//CREATE
			else if(stat.equals(MAConsts.CHANGE)) {
				if(isDirectory) {
					boolean isMedia = false;
					File[] list = file.listFiles(); 
					if(!(list.length > 0)) {
						file.delete();
					} else {
						for(File f : list) {
							if(util.isMediaFile(f)) {
								isMedia = true;
							}
						}
						if(!isMedia) {
							for(File f : list) {
								f.delete();
							}
							file.delete();
						}
					}
				}
				return;
			}//CHANGE
			else if(stat.equals(MAConsts.DELETE)) {
				
				try {
					
					if(file.getParent().equals(MAConsts.TV_PATH) && !isDirectory) {
						return;
					}
					
					String key = new KeyManager().getTvKey(file, isDirectory);
//					String key = util.getPuerSiriesName(file.getName());
//					if(isDirectory) {
//						key = file.getName();
//					}
					String fileKey = util.getRegStr(file.getName(), MAConsts.SEASON_PATTERN) + util.getRegStr(file.getName(), MAConsts.SIRIES_PATTERN);
					
					log.debug("KEY : [" + key + "], FILEKEY : [" + fileKey + "]");
					log.debug(TvshowController.tvshowfiles.toString());
					log.debug("isMedia : [" + util.isMediaFile(file) + "], isSub : [" + util.isSubtitle(file) + "], isDir : [" + isDirectory + "]");
					
					if(util.isSubtitle(file)) {
						String ext = FilenameUtils.getExtension(file.getName());
						
						List<String> subtitleExtList = Arrays.asList(MAConsts.subtitleExts);
						for(String subExt : subtitleExtList) {
							String subKey = fileKey + "_" + subExt;
							if(ext.equalsIgnoreCase(subExt)) {
								TvshowController.tvshowfiles.get(key).remove(subKey);
								log.info("REMOVE : [" + file.getParentFile().getName() + file.separator +file.getName() + "]");
							}
						}
					} 
					else if(isDirectory) {
						TvshowController.tvshowfiles.get(key).remove(MAConsts.DIRECTORY);
						log.info("REMOVE : [" + file.getParentFile().getName() + file.separator + file.getName() + "]");
					} 
					else {
						TvshowController.tvshowfiles.get(key).remove(fileKey);
						log.info("REMOVE : [" + file.getParentFile().getName() + file.separator + file.getName() + "]");
					}
					
					log.debug(TvshowController.tvshowfiles.toString());
					
//					if(TvshowController.tvshowfiles.get(key).isEmpty()) {
//						TvshowController.tvshowfiles.remove(key);
//						log.debug(TvshowController.tvshowfiles.toString());
//					}
					
				} catch (Exception e) {
					log.error(e.toString());
					e.printStackTrace();
				}
				
			}//DELETE
			else {
				throw new Exception("MediaHandler : 알 수 없는 생성값이 들어왔습니다.");
			}
		} catch (Exception e) {
			log.error(e.toString());
			return;
		}
		
	}
	
	private boolean getTvshowSubtitle() {
		
		boolean result = false;
		
//		String key = util.getPuerSiriesName(file.getName());
		String key = new KeyManager().getTvKey(file, false);
		String fileKey = util.getRegStr(file.getName(), MAConsts.SEASON_PATTERN) + util.getRegStr(file.getName(), MAConsts.SIRIES_PATTERN);
		
		String logKey = key.substring(0, key.length()/2) + "...." + FilenameUtils.getExtension(file.getName());
		
		ConcurrentHashMap<String, File> tvshowFile = null;
		if(TvshowController.tvshowfiles.containsKey(key)) {
			tvshowFile = TvshowController.tvshowfiles.get(key);
		} else {
			log.info(logKey + " 파일의 미디어 파일이 존재하지 않습니다.");
			return result;
		}
		
		ConcurrentHashMap<String, File> subtitleFile = null;
		if(SubtitleController.subtitlefiles.containsKey(key)) {
			subtitleFile = SubtitleController.subtitlefiles.get(key);	
		} else {
			log.info(logKey + " 파일의 자막 파일이 존재하지 않습니다.");
			return result;
		}
		
		if(tvshowFile.get(MAConsts.DIRECTORY) != null && subtitleFile != null) {
			List<String> subtitleExtList  = Arrays.asList(MAConsts.subtitleExts);
			for(String ext : subtitleExtList) {
				String subKey = fileKey + "_" + ext;
				if(subtitleFile.containsKey(subKey) && !tvshowFile.containsKey(subKey)) {
					copy(subtitleFile.get(subKey).getAbsolutePath()
							, tvshowFile.get(MAConsts.DIRECTORY).getAbsolutePath() + file.separator + subtitleFile.get(subKey).getName());
					log.info("COPY : [" + subtitleFile.get(subKey).getParentFile().getName() + file.separator + subtitleFile.get(subKey).getName() 
							+ "] -> [" + tvshowFile.get(MAConsts.DIRECTORY).getName() + file.separator + subtitleFile.get(subKey).getName() + "]");
					result = true;
				}
			}
		}
		else {
			if(tvshowFile.get(MAConsts.DIRECTORY) == null) {
				log.error(file.getName() + " 파일의 디렉토리가 존재하지 않습니다.");
			} else if(subtitleFile == null) {
				log.error(file.getName() + " 파일의 자막파일이 존재하지 않습니다.");
			} else if(tvshowFile == null) {
				log.error(file.getName() + " 파일의 미디어 파일이 존재하지 않습니다.");
			}
		}
		return result;
	}//getTvshowSubtitle
		
}




















