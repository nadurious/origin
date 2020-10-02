package com.mediaflex.filehandler;

import java.io.File;

import com.mediaflex.constants.MAConsts;
import com.mediaflex.util.MediaAgentUtil;

public abstract class AbstractFileHandler implements Runnable {
	
	protected String stat;
	protected File file; 
	
	private MediaAgentUtil util = new MediaAgentUtil();
	
	public AbstractFileHandler(String stat, File file) {
		this.stat = stat;
		this.file = file;
	}
	
	@Override
	public void run() {
		
	}
	
	public boolean touch(String path) {
		String command = "touch \"" + path +"\"";
		String result = util.runShell(command);
		
		if(result.equals(MAConsts.SUCCESS)) {
			return true;
		}
		
		return false;
	}
	
	public boolean chown(String path) {
		String command = "chown -R " + MAConsts.USER_ID + ":" + MAConsts.USER_ID + " \"" + path + "\"";
		String result = util.runShell(command);
		
		if(result.equals(MAConsts.SUCCESS)) {
			return true;
		} 
		return false;
	}
	
	public boolean chmod(String path, String auth) {
		String command = "chmod -R " + auth + " \"" + path + "\"";
		String result = util.runShell(command);
		
		if(result.equals(MAConsts.SUCCESS)) {
			return true;
		} 
		return false;
	}
	
	public File mkdir(String path) {
		
		String command = "mkdir \"" + path + "\" && " + "chown " + MAConsts.USER_ID + ":" + MAConsts.USER_ID + " \"" + path + "\"";
		String result = util.runShell(command);
		
		if(result.equals(MAConsts.SUCCESS)) {
			return new File(path);
		} 
		return null;
	}
	
	public boolean delete(String path) {
		
		String command = "rm -f \"" + path + "\"";
		String result = util.runShell(command);
		
		if(result.equals(MAConsts.SUCCESS)) {
			return true;
		} 
		return false;
	}
	
	public File move(String path, String dest) {
		
		String command = "mv -f \"" + path + "\" \"" + dest + "\"";
		String result = util.runShell(command);
		
		if(result.equals(MAConsts.SUCCESS)) {
			return new File(dest);
		} 
		return null;
	}
	
	public File copy(String path, String dest) {
		
		String command = "cp -pf \"" + path + "\" \"" + dest + "\"";
		String result = util.runShell(command);
		
		if(result.equals(MAConsts.SUCCESS)) {
			return new File(dest);
		} 
		return null;
	}

}

















