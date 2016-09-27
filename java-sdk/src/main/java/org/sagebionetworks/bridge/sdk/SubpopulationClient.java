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
     *      a list of all subpopulations
     */
    public ResourceList<Subpopulation> getAllSubpopulations() {
        session.checkSignedIn();
        return get(config.getSubpopulations(), SUBPOPULATION_LIST_TYPE);
    }
    
    /**
     * Create a new subpopulation.
     * @param subpopulation
     *      the subpopulation to create
     * @return
     *      a holder object with the GUID and version of the newly created subpopulation
     */
    public GuidVersionHolder createSubpopulation(Subpopulation subpopulation) {
        session.checkSignedIn();
        return post(config.getSubpopulations(), subpopulation, GuidVersionHolder.class);
    }
    
    /**
     * Get a subpopulation using its GUID.
     * @param subpopGuid
     *      the GUID object of the subpopulation to retrieve
     * @return
     *      the subpopulation with the given GUID
     */
    public Subpopulation getSubpopulation(SubpopulationGuid subpopGuid) {
        session.checkSignedIn();
        return get(config.getSubpopulation(subpopGuid.getGuid()), Subpopulation.class);
    }
    
    /**
     * Update a subpopulation with the given GUID.
     * @param subpopulation
     *      the subpopulation to update
     * @return
     *      the subpopulation with the given GUID
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
     * 
     * @param subpopGuid
     *      the GUID key of the subpopulation to delete
     */
    public void deleteSubpopulation(SubpopulationGuid subpopGuid) {
        session.checkSignedIn();
        delete(config.getSubpopulation(subpopGuid.getGuid()));
    }
    
    /**
     * Delete a subpopulation from the database. Only used for integration tests.
     * 
     * @see org.sagebionetworks.bridge.sdk.SubpopulationClient#deleteSubpopulation
     * 
     * @param subpopGuid
     *      the GUID key of the subpopulation to physically delete from the server
     */
    public void deleteSubpopulationPermanently(SubpopulationGuid subpopGuid) {
        session.checkSignedIn();
        delete(config.getSubpopulation(subpopGuid.getGuid())+"?physical=true");
    }    
}
