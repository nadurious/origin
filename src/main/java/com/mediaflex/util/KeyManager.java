package com.mediaflex.util;

import java.io.File;

import org.apache.commons.io.FilenameUtils;

import com.mediaflex.constants.MAConsts;

public class KeyManager {
	
	private MediaAgentUtil util = new MediaAgentUtil();

	public KeyManager() {
		
	}
	
	public String getTvKey(File file, boolean isDirectory) {
		
		String key = "";
		if(util.hasSiriesPattern(file)) {
			String test = util.getSiriesName(file.getName());
			//한글로 된 시즌패턴으로 끝나는 경우
			if(util.hasPattern(test, MAConsts.SEASON_LAST_PATTERN_KOR)) 
			{
				key = util.getSiriesName(file.getName());
			}
			//V 버전으로 끝나는 경우
			else if(util.hasPattern(test, MAConsts.VERSION_LAST_PATTERN)) 
			{
				key = util.getSiriesName(file.getName());
			}
			//마지막이 년도 패턴으로 끝나는 경우
			else if(util.hasPattern(test, MAConsts.TITLE_LAST_YEAR_PATTERN))
			{
				key = util.getSiriesName(file.getName());
			}
			else 
			{
				key = util.getSiriesName(file.getName());
			}
		}
		else if(util.hasDatePattern(file) && !util.hasSiriesPattern(file)) {
			key = util.getRegStr(file.getName(), MAConsts.DATE_PATTERN);
		}
		
		if(isDirectory) {
			key = file.getName();
		}
		
		return key;
	}
	
	public String getMovieKey(File file, boolean isDirectory) {
		
		String key = "";
		if(!isDirectory) {
			key = FilenameUtils.removeExtension(file.getName());
		} else {
			key = file.getName();
		}
		
		return key;
	}
	
}
