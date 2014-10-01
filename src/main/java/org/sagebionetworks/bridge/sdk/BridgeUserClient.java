package org.sagebionetworks.bridge.sdk;

import java.util.List;

import org.sagebionetworks.bridge.sdk.models.Tracker;
import org.sagebionetworks.bridge.sdk.models.UserProfile;

public class BridgeUserClient {

    private final Utilities utils = Utilities.getInstance();

    private final ClientProvider provider;
    private final UserProfileApiCaller profileApi;
    private final TrackerApiCaller trackerApi;

    private BridgeUserClient(ClientProvider provider) {
        this.provider = provider;
        this.profileApi = UserProfileApiCaller.valueOf(provider);
        this.trackerApi = TrackerApiCaller.valueOf(provider);
    }

    static BridgeUserClient valueOf(ClientProvider provider) {
        return new BridgeUserClient(provider);
    }

    public ClientProvider getProvider() { return this.provider; }

    public UserProfile getProfile() {
        if (!provider.isSignedIn()) {
            throw new IllegalStateException("Provider must be signed in to call this method.");
        }
        return profileApi.getProfile();
    }

    public void saveProfile(UserProfile profile) {
        if (!provider.isSignedIn()) {
            throw new IllegalStateException("Provider must be signed in to call this method.");
        } else if (profile == null) {
            throw new IllegalArgumentException("Profile cannot be null.");
        } else if (profile.getFirstName() == null) {
            throw new IllegalArgumentException("Profile first name cannot be null.");
        } else if (profile.getLastName() == null) {
            throw new IllegalArgumentException("Profile last name cannot be null.");
        } else if (profile.getUsername() == null) {
            throw new IllegalArgumentException("Profile username cannot be null.");
        } else if (!utils.isValidEmail(profile.getEmail())) {
            throw new IllegalArgumentException("Profile email is not valid: " + profile.getEmail());
        }
        profileApi.updateProfile(profile);
    }

    public List<Tracker> getAllTrackers() {
        if (!provider.isSignedIn()) {
            throw new IllegalStateException("Provider must be signed in to call this method.");
        }
        return trackerApi.getAllTrackers();
    }

    public String getSchema(Tracker tracker) {
        if (!provider.isSignedIn()) {
            throw new IllegalStateException("Provider must be signed in to call this  method.");
        }
        return trackerApi.getSchema(tracker);
    }

}
