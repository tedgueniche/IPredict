package ca.ipredict.predictor.profile;

/**
 * Apply a parameter profile by name
 */
public class ProfileManager {	
	public static void loadProfileByName(String name) {
		Profile profile = null;
		try {
			Class<?> classI = Class.forName("ca.ipredict.predictor.profile."+ name + "Profile");
			profile = (Profile) classI.newInstance();
		} catch (Exception e) {
			profile = new DefaultProfile();
		}
		
		profile.Apply();
	}
}
