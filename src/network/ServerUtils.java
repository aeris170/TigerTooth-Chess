package network;

/**
 * Utilities for server. Responsible only for validating the entered IP. server
 * connected.
 * 
 * @author Doða Oruç
 * @version 06.08.2017
 */
public class ServerUtils {

	/**
	 * Instantiates a new server utils.
	 */
	public ServerUtils() {
		throw new RuntimeException("Cannot instansiate a Util class!");
	}

	/**
	 * Validate IP.
	 *
	 * @param ip
	 *            the ip
	 * @return true, if and only if the IP is actually an IP adress
	 */
	public static boolean validateIP(String ip) {
		try {
			if (ip == null || ip.isEmpty()) {
				return false;
			}
			String[] parts = ip.split("\\.");
			if (parts.length != 4) {
				return false;
			}
			for (String s : parts) {
				int i = Integer.parseInt(s);
				if ((i < 0) || (i > 255)) {
					return false;
				}
			}
			if (ip.endsWith(".")) {
				return false;
			}

			return true;
		} catch (NumberFormatException nfe) {
			return false;
		}
	}
}