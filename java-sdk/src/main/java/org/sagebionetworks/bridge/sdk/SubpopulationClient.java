package org.sagebionetworks.bridge.sdk;

import org.sagebionetworks.bridge.sdk.models.ResourceList;
import org.sagebionetworks.bridge.sdk.models.holders.GuidVersionHolder;
import org.sagebionetworks.bridge.sdk.models.subpopulations.Subpopulation;
import org.sagebionetworks.bridge.sdk.models.subpopulations.SubpopulationGuid;

import com.fasterxml.jackson.core.type.TypeReference;

public class SubpopulationClient extends BaseApiCaller {
    
    private final TypeReference<ResourceList<Subpopulation>> SUBPOPULATION_LIST_TYPE = new TypeReference<ResourceList<Subpopulation>>() {};

    SubpopulationClient(BridgeSession session) {
        super(session);
    }
    
    /**
     * Get all subpopulations defined for this study.
     * @return
     */
    public ResourceList<Subpopulation> getAllSubpopulations() {
        session.checkSignedIn();
        return get(config.getSubpopulations(), SUBPOPULATION_LIST_TYPE);
    }
    
    /**
     * Create a new subpopulation.
     * @param subpopulation
     */
    public GuidVersionHolder createSubpopulation(Subpopulation subpopulation) {
        session.checkSignedIn();
        return post(config.getSubpopulations(), subpopulation, GuidVersionHolder.class);
    }
    
    /**
     * Get a subpopulation using its GUID.
     * @param subpopGuid
     * @return
     */
    public Subpopulation getSubpopulation(SubpopulationGuid subpopGuid) {
        session.checkSignedIn();
        return get(config.getSubpopulation(subpopGuid.getGuid()), Subpopulation.class);
    }
    
    /**
     * Update a subpopulation with the given GUID.
     * @param subpopulation
     */
    public GuidVersionHolder updateSubpopulation(Subpopulation subpopulation) {
        session.checkSignedIn();
        return post(config.getSubpopulation(subpopulation.getGuid().toString()), subpopulation, GuidVersionHolder.class);
    }
    
    /**
     * Delete a subpopulation. Studies start with a default subpopulation that cannot 
     * be deleted (although it can be edited). But other subpopulations can be deleted. 
     * Records of consent for a particular subpopulation are not deleted and historical 
     * records can be produced of consent to deleted subpopulations.
     * @param subpopGuid
     */
    public void deleteSubpopulation(SubpopulationGuid subpopGuid) {
        session.checkSignedIn();
        delete(config.getSubpopulation(subpopGuid.getGuid()));
    }
    
    /**
     * Delete a subpopulation from the database. Only used for integration tests.
     * 
     * @see org.sagebionetworks.bridge.sdk.SubpopulationClient#deleteSubpopulation
     * @param subpopGuid the subpopulation to be deleted
     */
    public void deleteSubpopulationPermanently(SubpopulationGuid subpopGuid) {
        session.checkSignedIn();
        delete(config.getSubpopulation(subpopGuid.getGuid())+"?physical=true");
    }    
}
