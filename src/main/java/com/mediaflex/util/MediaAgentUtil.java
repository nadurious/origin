package com.mediaflex.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.apache.tika.Tika;

import com.mediaflex.constants.MAConsts;

public class MediaAgentUtil {
	
	public String runShell(String command) {
		
		try {
			ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", command);
			pb.redirectError();
			pb.redirectOutput();
			
			Process process = pb.start();
			process.waitFor();
			process.destroy();
			
			return MAConsts.SUCCESS;
		} catch (Exception e) {
			return MAConsts.FAIL;
		}
			
	}//runShell
	
	public String callShell(String command) {
		
		try {
			
			ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", command);
			pb.redirectError();
			pb.redirectOutput();
			
			Process process = pb.start();
			process.waitFor();
			
			InputStream out = process.getInputStream();
			InputStream err = process.getErrorStream();
			
			BufferedReader outBr = new BufferedReader(new InputStreamReader(out));
			BufferedReader errBr = new BufferedReader(new InputStreamReader(err));
			
			StringBuilder returnMsg = new StringBuilder();
			
			String msg = "";
			while ((msg = outBr.readLine()) != null) {
				returnMsg.append(msg);
			}
			
			while ((msg = errBr.readLine()) != null) {
				returnMsg.append(msg);
			}
			
			process.destroy();
			
			return returnMsg.toString();
		} catch (Exception e) {
			return MAConsts.FAIL;
		}
			
	}//callShell
	
	public String changeAllSpecialChar(String Str, String chan) {
		
		Str = Str.replaceAll(MAConsts.SPECIAL_CHAR, chan);
		
		return Str;
	}
	
	public String getPureSiriesName(String testStr) {
		
		String siriesName 	= getSiriesName(testStr);
		
		String last = getRegStr(siriesName, MAConsts.TITLE_LAST_NUMBER_PATTERN);
		String seasonLast = getRegStr(siriesName, MAConsts.SEASON_LAST_PATTERN);
		siriesName = siriesName.replaceAll(seasonLast, "");
		siriesName = siriesName.replaceAll(last, "");
		siriesName = getSiriesName(siriesName);
		
		return siriesName;
	}
	
	public String getSiriesName(String testStr) {
		
		Pattern pattern = Pattern.compile(MAConsts.SIRIES_PATTERN);
		Matcher matcher = pattern.matcher(testStr);
		
		if(matcher.find()) {
			int start = matcher.start();
			String result = testStr.substring(0, start);
			
			if(hasPattern(result, MAConsts.SEASON_PATTERN)) {
				String season = getRegStr(result, MAConsts.SEASON_PATTERN);
				result = result.replaceAll(season, "");
			}
			
			if(!hasPattern(result, MAConsts.TITLE_LAST_PATTERN)) {
				result = result.substring(0, result.length() - 1);
				return getSiriesName(result);
			} 
			
			return result;
			
		} else if(!matcher.find()) {
			if(!hasPattern(testStr, MAConsts.TITLE_LAST_PATTERN)) {
				String result = testStr.substring(0, testStr.length() - 1);
				return getSiriesName(result);
			} 
			return testStr;
		}
		return "";
	}
	
	public String renameSiries(String testStr) {
		
		String siriesName 	= getSiriesName(testStr);
		String seasonName 	= getRegStr(testStr, MAConsts.SEASON_PATTERN);
		String epiName 		= getRegStr(testStr, MAConsts.SIRIES_PATTERN);
		String date 		= getRegStr(testStr, MAConsts.DATE_PATTERN);
		String resolution	= getRegStr(testStr, MAConsts.RESOLUTION_PATTERN);
		String ext 			= FilenameUtils.getExtension(testStr);
		
		//����?? �� ������
		if(hasPattern(siriesName, MAConsts.SEASON_LAST_PATTERN_KOR)) 
		{
			seasonName = "none";
		}
		//V?? �� ������
		else if(hasPattern(siriesName, MAConsts.VERSION_LAST_PATTERN)) 
		{
			seasonName = "none";
		}
		//�⵵�� ������
		else if(hasPattern(siriesName, MAConsts.TITLE_LAST_YEAR_PATTERN))
		{
			if(hasPattern(testStr, MAConsts.SEASON_PATTERN)) {
				seasonName = getRegStr(testStr, MAConsts.SEASON_PATTERN);
			} 
			else if(hasPattern(testStr, MAConsts.SEASON_PATTERN_LONG)) {
				String tempSeason = getRegStr(testStr, MAConsts.SEASON_PATTERN_LONG);
				int seasonNum = Integer.parseInt(tempSeason.substring(6, tempSeason.length()));
				seasonName = String.format("S%02d", seasonNum);
			}
			else {
				seasonName = "S01";
			}
		}
		else 
		{
			if(hasPattern(testStr, MAConsts.SEASON_PATTERN)) {
				seasonName = getRegStr(testStr, MAConsts.SEASON_PATTERN);
			} 
			else if(hasPattern(testStr, MAConsts.SEASON_PATTERN_LONG)) {
				String tempSeason = getRegStr(testStr, MAConsts.SEASON_PATTERN_LONG);
				int seasonNum = Integer.parseInt(tempSeason.substring(6, tempSeason.length()));
				seasonName = String.format("S%02d", seasonNum);
			}
			else {
				seasonName = "S01";
			}
		}
		
//		if(hasPattern(siriesName, MAConsts.TITLE_LAST_NUMBER_PATTERN) 
//			&& !hasPattern(siriesName, MAConsts.TITLE_LAST_YEAR_PATTERN)
//			&& !hasPattern(siriesName, MAConsts.VERSION_LAST_PATTERN)
//			&& !hasPattern(siriesName, MAConsts.SEASON_LAST_PATTERN_KOR)) 
//		{
//			String last = getRegStr(siriesName, MAConsts.TITLE_LAST_NUMBER_PATTERN);
//			String seasonLast = getRegStr(siriesName, MAConsts.SEASON_LAST_PATTERN);
//			seasonName = String.format("S%02d", Integer.parseInt(last));
//			siriesName = siriesName.replaceAll(seasonLast, "");
//			siriesName = siriesName.replaceAll(last, "");
//			siriesName = getSiriesName(siriesName);
//		}
//		else if(hasPattern(siriesName, MAConsts.TITLE_LAST_YEAR_PATTERN))
//		{
//			siriesName = getSiriesName(siriesName);
//		}
//		else if(hasPattern(siriesName, MAConsts.VERSION_LAST_PATTERN))
//		{
//			seasonName = "none";
//			siriesName = getSiriesName(siriesName);
//		}
//		else if(hasPattern(siriesName, MAConsts.SEASON_LAST_PATTERN_KOR))
//		{
//			seasonName = "none";
//			siriesName = getSiriesName(siriesName);
//		}
		
		StringBuilder builder = new StringBuilder();
		if(!siriesName.equals("")) {
			builder.append(siriesName);
			builder.append(" - ");
		}
		
		if(!seasonName.equals("") && !seasonName.equals("none")) {
			builder.append(seasonName);
		} 
		else if(seasonName.equals("none")) {
		}
		else {
			builder.append("S01");
		}
		
		if(!epiName.equals("")) {
			builder.append(epiName);
			builder.append(MAConsts.RENAME_SEPARATOR);
		}
		
		if(!date.equals("")) {
			builder.append(date);
			builder.append(MAConsts.RENAME_SEPARATOR);
		}
		
		if(!resolution.equals("")) {
			builder.append(resolution);
			builder.append(MAConsts.RENAME_SEPARATOR);
		}
		
		if(!ext.equals("")) {
			builder.append(MAConsts.RENAME_WORD);
			builder.append(MAConsts.RENAME_SEPARATOR);
			builder.append(ext);
		}
		
		return builder.toString();
	}
	
	public String getRegStr(String testStr, String anchor) {
		
		Pattern pattern = Pattern.compile(anchor);
		Matcher matcher = pattern.matcher(testStr);
		
		if(matcher.find()) {
			int start = matcher.start();
			int end = matcher.end();
			String result = testStr.substring(start, end);
			return result;
		}
		
		return "";
	}
	
	public boolean hasPattern(String testStr, String anchor) {
		
		Pattern pattern = Pattern.compile(anchor);
		Matcher matcher = pattern.matcher(testStr);
		
		boolean result = matcher.find();
		
		return result;
	}
	
	public boolean hasDatePattern(File file) {
		
		String fileName = file.getName();
		
		Pattern pattern = Pattern.compile(MAConsts.DATE_PATTERN);
		Matcher matcher = pattern.matcher(fileName);
		
		boolean result = matcher.find();
		
		return result;
		
	}
	
	public boolean hasSiriesPattern(File file) {
		
		String fileName = file.getName();
		
		Pattern pattern = Pattern.compile(MAConsts.SIRIES_PATTERN);
		Matcher matcher = pattern.matcher(fileName);
		
		boolean result = matcher.find();
		
		return result;
	}
	
	public boolean isMediaFile(File file) {
		
		boolean isMedia = false;
		
		try {
			
			Tika tika = new Tika();
			String mime = tika.detect(file);
			
			if(mime.startsWith("video")) {
				isMedia = true;
			}
			if(FilenameUtils.getExtension(file.getName()).toUpperCase().equals("TS")
			 || FilenameUtils.getExtension(file.getName()).toUpperCase().equals("TP")) {
				isMedia = true;
			}
			
			return isMedia;
			
		} catch (Exception e) {
			return isMedia;
		}
	}
	
	public boolean isSubtitle(File file) {
		
		String fileName = file.getName();
		
		boolean isSub = false;
		
		List<String> subExtList = Arrays.asList(MAConsts.subtitleExts);
		
		for(String subExt : subExtList) {
			if(fileName.toUpperCase().endsWith("." + subExt)) {
				isSub = true;
			}
		}
		
		return isSub;
	}//isSubtitle

	public File createSRTFile(File file) {
		
		
		
		return null;
	}

}



















