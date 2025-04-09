package concurrent_asyncs;


public class InternetChecker {
	
	/**
	 * A class to check for internet connection
	 *
	 * @return boolean if there is internet
	 */
    public static boolean hasInternet() {
        try {
            // We use Google DNS as it is stable
            return java.net.InetAddress.getByName("8.8.8.8").isReachable(2000);
        } catch (Exception e) {
            return false;
        }
    }
}
