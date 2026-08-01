package com.lineage.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Properties;

public final class ConfigIpCheck
{
  public static boolean IPCHECKPACK;
  public static boolean IPCHECK;
  public static int TIMEMILLIS;
  public static int COUNT;
  public static boolean SETDB;
  public static boolean UFW;
  public static boolean ISONEIP;
  public static int ONETIMEMILLIS;
  public static int LINK_COUNT;
  public static int ACCLOGINTIMEMILLIS;
  public static boolean ACCLOGINBANIP;
  private static final String _ipcheck = "./config/ipcheck.properties";
  
  /* Error */
  public static void load()
    throws ConfigErrorException
  {
//    final Properties set = new Properties();
//    try {
//      final InputStream is = new FileInputStream(new File(_ipcheck));
//      set.load(new InputStreamReader(is, Charset.forName("UTF-8")));
//      is.close();
//
//      IPCHECKPACK = Boolean.parseBoolean(set.getProperty("IPCHECKPACK", "false"));
//      IPCHECK = Boolean.parseBoolean(set.getProperty("IPCHECK", "false"));
//      SETDB = Boolean.parseBoolean(set.getProperty("SETDB", "false"));
//      UFW = Boolean.parseBoolean(set.getProperty("UFW", "false"));
//      ISONEIP = Boolean.parseBoolean(set.getProperty("ISONEIP", "false"));
//      TIMEMILLIS = Integer.parseInt(set.getProperty("TIMEMILLIS", "0"));
//      COUNT = Integer.parseInt(set.getProperty("COUNT", "0"));
//      ONETIMEMILLIS = Integer.parseInt(set.getProperty("ONETIMEMILLIS", "0"));
//    } catch (final Exception e) {
//      throw new ConfigErrorException("設置檔案遺失: " + _ipcheck);
//
//    } finally {
//      set.clear();
//    }
  }
}
