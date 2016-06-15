package com.oracle_hcm.dataUploadTool.configure;

import java.io.File;

public class Directory {

	public static String ROOT = System.getProperty("user.dir") + File.separator + "HCM";

	public static String EXPORTED = ROOT + File.separator + "EXPORTED";

	public static String TRANSFERED = ROOT + File.separator + "TRANSFERED";

	public static String LOADED = ROOT + File.separator + "LOADED";
}
