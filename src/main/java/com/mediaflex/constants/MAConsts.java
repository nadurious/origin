package com.mediaflex.constants;

import java.io.File;

public class MAConsts {

	//PATHS
	public static final String WATCH_PATH 		= System.getProperty("watch_path");
	public static final String MOVIE_PATH 		= System.getProperty("movie_path");
	public static final String TV_PATH 			= System.getProperty("tv_path");
	public static final String SUB_PATH 		= System.getProperty("sub_path");
	public static final String USER_ID 			= System.getProperty("user_id");
	
	//REFRESH_CYCLE
	public static final String REFRESH_USE 		= System.getProperty("refresh_use");
	public static final int REFRESH_CYCLE 		= Integer.parseInt(System.getProperty("refresh_cycle"));
	
	//RENAME PARAMS
	public static final String RENAME_USE 		= System.getProperty("rename_use");
	public static final String RENAME_WORD 		= System.getProperty("rename_word");
	public static final String RENAME_SEPARATOR	= System.getProperty("rename_separator");
	
	//OS
	public static final String OS 				= System.getProperty("OS");
	public static final String LINUX 			= "LINUX";
	public static final String WINDOW 			= "WINDOW";
	
	//EXT LISTS
	public static final String[] subtitleExts 	= {"SMI", "SRT", "KO.SRT", "KOR.SRT", "KO.SMI", "KOR.SMI"};
	public static final String[] mediaExts 		= {"MKV", "MP4", "FLV", "AVI", "MOV", "WMV", "3GP", "TS", "TP", "SWF"};
	
	//OTHER LISTS
	public static final String[] others = {"ÀÎ±â°¡¿ä", "ÀÎ°¡", "¹ÂÁ÷¹ðÅ©", "¹Â¹ð", "À½¾ÇÁß½É", "À½Áß", "´õ¼î", "µå¸²ÄÜ¼­Æ®", "¼îÃ¨ÇÇ¾ð", "´õ ¼î", "¼î Ã¨ÇÇ¾ð", "°¡¿ä´ëÁ¦Àü"
										, "showchampion", "showchampions", "show champion", "show champions", "musicbank", "music bank", "musiccore", "music core"
										, "dreamconcert", "dream concert"};
	
	//DIRECTORY FILES
	public static final File WATCH_DIR 		= new File(WATCH_PATH);
	public static final File MOVIE_DIR 		= new File(MOVIE_PATH);
	public static final File TV_DIR 		= new File(TV_PATH);
	public static final File SUB_DIR 		= new File(SUB_PATH);
	
	//PATTERNS
	public static final String DATE_PATTERN 				= "([0-9])([0-9])([0-1])([0-9])([0-3])([0-9])";
	public static final String SIRIES_PATTERN 				= "(?i)E([0-9])([0-9])([0-9])?([0-9])?";
	public static final String SEASON_PATTERN 				= "(?i)S([0-9])([0-9])?";
	public static final String SEASON_PATTERN_LONG			= "(?i)SEASON([0-9])([0-9])?";
	public static final String SEASON_LAST_PATTERN 			= "(?i)S([0-9])([0-9])?$";
	public static final String RESOLUTION_PATTERN 			= "([0-9])([0-9])([0-9])([0-9])?p";
	public static final String SPECIAL_CHAR 				= "[^\\[\\]\\(\\)\\s0-9a-zA-Z°¡-ÆR]";

	public static final String TITLE_LAST_PATTERN 			= "([a-zA-Z°¡-ÆR0-9])$";
	public static final String TITLE_LAST_NUMBER_PATTERN 	= "([0-9])([0-9])?$";
	public static final String TITLE_LAST_YEAR_PATTERN 		= "([1-2])([0-9])([0-9])([0-9])$";
	public static final String SEASON_LAST_PATTERN_KOR		= "½ÃÁð([0-9])([0-9])?$";
	public static final String VERSION_LAST_PATTERN			= "(?i)V([0-9])([0-9])?$";
	
	public static final String TAG_REMOVE					= "<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>";
	
	//FILE KEYS
	public static final String DIRECTORY 		= "DIRECTORY";
	public static final String CONTENT 			= "CONTENT";
	public static final String SMI 				= "SMI";
	public static final String SRT 				= "SRT";
	public static final String KO_SMI 			= "KO_SMI";
	public static final String KOR_SMI 			= "KOR_SMI";
	public static final String KO_SRT 			= "KO_SRT";
	public static final String KOR_SRT 			= "KOR_SRT";
	
	//RESULT KEYS
	public static final String SUCCESS 			= "0";
	public static final String FAIL 			= "1";

	//BOOLEAN KEYS
	public static final String TRUE 			= "true";
	public static final String FALSE 			= "false";
	
	//FILE STATUS
	public static final String CREATE 			= "0";
	public static final String CHANGE 			= "1";
	public static final String DELETE 			= "2";
	
	//PERMISSION
	public static final String PERMISSION_777	= "777";
	public static final String PERMISSION_755	= "755";
	
}




















