package com.mediaflex.filehandler;

import java.io.File;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationObserver;

/**
 * @author      : namwoo (namwoo@KIMNAS)
 * @file        : TestHandler
 * @created     : 금요일 10월 02, 2020 20:06:52 KST
 */

public class TestHandler implements FileAlterationListener
{
    public TestHandler()
    {
        
	}

	@Override
	public void onStart(FileAlterationObserver observer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDirectoryCreate(File directory) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDirectoryChange(File directory) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDirectoryDelete(File directory) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFileCreate(File file) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFileChange(File file) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFileDelete(File file) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStop(FileAlterationObserver observer) {
		// TODO Auto-generated method stub

	}
}


