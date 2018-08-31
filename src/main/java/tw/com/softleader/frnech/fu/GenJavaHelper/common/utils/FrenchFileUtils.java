package tw.com.softleader.frnech.fu.GenJavaHelper.common.utils;

import java.io.File;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.google.common.collect.Lists;

public class FrenchFileUtils {
	
	public static File createTempDirectory()throws IOException {
		   
			final File temp;

		    temp = File.createTempFile("temp", Long.toString(System.nanoTime()));

		    if(!(temp.delete()))
		    {
		        throw new IOException("Could not delete temp file: " + temp.getAbsolutePath());
		    }

		    if(!(temp.mkdir()))
		    {
		        throw new IOException("Could not create temp directory: " + temp.getAbsolutePath());
		    }

		    return (temp);
		}
	
}
