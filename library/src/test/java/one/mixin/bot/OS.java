package one.mixin.bot;

/**
 * Provides methods related to the runtime environment.
 */
public class OS {

  private static final boolean osIsMacOsX;
  private static final boolean osIsWindows;
  private static final boolean osIsWindowsXP;
  private static final boolean osIsWindows2003;
  private static final boolean osIsWindowsVista;
  private static final boolean osIsLinux;

    static {
        String os = System.getProperty("os.name");
        if (os != null)
            os = os.toLowerCase();

        osIsMacOsX = "mac os x".equals(os);
        osIsWindows = os != null && os.contains("windows");
        osIsWindowsXP = "windows xp".equals(os);
        osIsWindows2003 = "windows 2003".equals(os);
        osIsWindowsVista = "windows vista".equals(os);
        osIsLinux = os != null && os.contains("linux");
    }

  /**
   * @return true if this VM is running on Mac OS X
   */
  public static boolean isMacOSX() {
    return osIsMacOsX;
  }

  /**
   * @return true if this VM is running on Windows
   */
  public static boolean isWindows() {
    return osIsWindows;
  }

  /**
   * @return true if this VM is running on Windows XP
   */
  public static boolean isWindowsXP() {
    return osIsWindowsXP;
  }

  /**
   * @return true if this VM is running on Windows 2003
   */
  public static boolean isWindows2003() {
    return osIsWindows2003;
  }

  /**
   * @return true if this VM is running on Windows Vista
   */
  public static boolean isWindowsVista() {
    return osIsWindowsVista;
  }

  /**
   * @return true if this VM is running on a Linux distribution
   */
  public static boolean isLinux() {
    return osIsLinux;
  }
}