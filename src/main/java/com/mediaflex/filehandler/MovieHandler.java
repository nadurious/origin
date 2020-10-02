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
import com.mediaflex.util.KeyManager;
import com.mediaflex.util.MediaAgentUtil;

public class MovieHandler extends AbstractFileHandler {
	
	private boolean isDirectory = false;
	
	private MediaAgentUtil util = new MediaAgentUtil();
	private Logger log = LoggerFactory.getLogger(MovieHandler.class);

	public MovieHandler(String stat, File file) {
		super(stat, file);
	}
	
	public MovieHandler(String stat, File file, boolean isDirectory) {
		super(stat, file);
		this.isDirectory = isDirectory;
	}
	
	@Override
	public void run() {
		
		try {
			
			ConcurrentHashMap<String, File> files = new ConcurrentHashMap<>();
			String key = new KeyManager().getMovieKey(file, isDirectory);
//			if(!isDirectory) {
//				key = FilenameUtils.removeExtension(file.getName());
//			} else {
//				key = file.getName();
//			}
			String ext = FilenameUtils.getExtension(file.getName());
			
			log.debug("KEY : [" + key + "] EXT : [" + ext + "]");
			
			if(stat.equals(MAConsts.CREATE)) {
				
				//디렉토리인 경우.
				if(isDirectory) {
					return;
				}
				
				//키값이 부모 디렉토리와 동일한 경우.
				if(key.equals(file.getParentFile().getName())){
					return;
				}
				
				//파일이 미디어 파일일 때.
				if(util.isMediaFile(file)) {
					File dir = mkdir(MAConsts.MOVIE_PATH + file.separator + key);
					log.info("MKDIR : [" + dir.getAbsolutePath() +"]");
					File mvMovie = move(file.getAbsolutePath(), dir.getAbsolutePath() + file.separator + file.getName());
					log.info("MOVE : [" + file.getAbsolutePath() +"] -> [" + mvMovie.getAbsolutePath() + "]");
					
					files.put(MAConsts.DIRECTORY, dir);
					files.put(MAConsts.CONTENT, mvMovie);
					
					MovieController.moviefiles.put(key, files);
					log.info("ADD : [" + file.getParentFile().getName() + file.separator + file.getName() +"]");
					
					chmod(dir.getAbsolutePath(), MAConsts.PERMISSION_777);
					
					getMovieSubtitle(key);
				}
				else if(util.isSubtitle(file)) {
					List<String> subtitleExtList = Arrays.asList(MAConsts.subtitleExts);
					for(String subExt : subtitleExtList) {
						if(ext.equalsIgnoreCase(subExt)) {
							files.put(subExt, file);
							if(!MovieController.moviefiles.containsKey(key)) {
								MovieController.moviefiles.put(key, files);
							} else {
								MovieController.moviefiles.get(key).putAll(files);
							}
							log.info("ADD : [" + file.getParentFile().getName() + file.separator + file.getName() +"]");
						}
					}
				}
				
				log.debug("ADD Result : " + MovieController.moviefiles.toString());
				
				return;
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
				
				if(file.getParent().equals(MAConsts.MOVIE_PATH) && !isDirectory) {
					return;
				}
				
				else if(util.isSubtitle(file)) {
					
					List<String> subtitleExtList = Arrays.asList(MAConsts.subtitleExts);
					for(String subExt : subtitleExtList) {
						if(ext.equalsIgnoreCase(subExt)) {
							if(MovieController.moviefiles.get(key).containsKey(subExt)) {
								MovieController.moviefiles.get(key).remove(subExt);
								log.info("REMOVE : [" + file.getParentFile().getName() + file.separator +file.getName() + "]");
							}
						}
					}
				}
				else if(isDirectory) {
					MovieController.moviefiles.get(key).remove(MAConsts.DIRECTORY);
					log.info("REMOVE : [" + file.getParentFile().getName() + file.separator +file.getName() + "]");
				}
				else {
					if(MovieController.moviefiles.get(key).containsKey(MAConsts.DIRECTORY)) {
						MovieController.moviefiles.get(key).remove(MAConsts.CONTENT);
						log.info("REMOVE : [" + file.getParentFile().getName() + file.separator +file.getName() + "]");
					}
				}
				
//				if(MovieController.moviefiles.get(key).isEmpty()) {
//					MovieController.moviefiles.remove(key);
//				}
				
//				log.info("REMOVE : [" + file.getParentFile().getName() + file.separator +file.getName() + "]");
			}//DELETE
			else {
				throw new Exception("MediaHandler 알 수 없는 생성값이 들어옴.");
			}
			
			log.debug("REMOVE Result : " + MovieController.moviefiles.toString());
			
			return;
		} catch (Exception e) {
			log.error(e.toString());
			e.printStackTrace();
		}
		
	}

	private boolean getMovieSubtitle(String key) {
		
		boolean result = false;
		
		String logKey = key.substring(0, key.length()/2) + "...." + FilenameUtils.getExtension(file.getName());
		
		ConcurrentHashMap<String, File> movieFile = null;
		if(MovieController.moviefiles.containsKey(key)) {
			movieFile = MovieController.moviefiles.get(key);
		} else {
			log.info(logKey + " 파일이 존재하지 않습니다.");
			return result;
		}
		
		ConcurrentHashMap<String, File> subtitleFile = null;
		if(SubtitleController.subtitlefiles.containsKey(key)) {
			subtitleFile = SubtitleController.subtitlefiles.get(key);	
		} else {
			log.info(logKey + " 자막 파일이 존재하지 않습니다.");
			return result;
		}
		
		if(movieFile.get(MAConsts.DIRECTORY) != null && subtitleFile != null) {
			List<String> subtitleExtList = Arrays.asList(MAConsts.subtitleExts);
			for(String subExt : subtitleExtList) {
				if(!movieFile.containsKey(subExt) && subtitleFile.containsKey(subExt)) {
					copy(subtitleFile.get(subExt).getAbsolutePath()
							, movieFile.get(MAConsts.DIRECTORY).getAbsolutePath() + file.separator + subtitleFile.get(subExt).getName());
						log.info("COPY : [" + subtitleFile.get(subExt).getParentFile().getName() + file.separator + subtitleFile.get(subExt).getName() 
								+ "] -> [" + movieFile.get(MAConsts.DIRECTORY).getName() + file.separator + subtitleFile.get(subExt).getName() + "]");
					result = true;
				}
			}
		}
		else {
			if(movieFile.get(MAConsts.DIRECTORY) == null) {
				log.error(file.getName() + " 파일의 디렉토리가 존재하지 않습니다.");
			} else if(subtitleFile == null) {
				log.error(file.getName() + " 파일의 자막파일이 존재하지 않습니다.");
			} else if(movieFile == null) {
				log.error(file.getName() + "파일의 미디어 파일이 존재하지 않습니다.");
			}
		}
		return result;
	}
	
}
