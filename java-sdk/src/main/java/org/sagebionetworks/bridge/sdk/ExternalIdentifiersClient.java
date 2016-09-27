package org.sagebionetworks.bridge.sdk;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.sagebionetworks.bridge.sdk.models.PagedResourceList;
import org.sagebionetworks.bridge.sdk.models.accounts.ExternalIdentifier;

import com.fasterxml.jackson.core.type.TypeReference;

public class ExternalIdentifiersClient extends BaseApiCaller {
    
    private static final TypeReference<PagedResourceList<ExternalIdentifier>> EXTERNAL_IDENTIFIER_PAGED_RESOURCE_LIST = 
            new TypeReference<PagedResourceList<ExternalIdentifier>>() {};

    ExternalIdentifiersClient(BridgeSession session) {
        super(session);
    }
    
    /**
     * Get a page of external identifiers added to Bridge. All arguments are optional. 
     * 
     * @param offsetKey
     *      Optional. If provided, records will be returned after this key in the list of identifiers. 
     *      Similar to an offsetBy index, but not as flexible, this essentially allows you to work page by 
     *      page through the records. 
     * @param pageSize
     *      Number of records to return for this request
     * @param idFilter
     *      Optional string that used to match against the start of an external identifier string
     * @param assignmentFilter
     *      Optional boolean filter to return only assigned or unassigned identifiers (if not provided, both 
     *      are returned).
     * @return
     *      A list of the external identifiers that meet the query criteria
     */
    public PagedResourceList<ExternalIdentifier> getExternalIds(
            String offsetKey, Integer pageSize, String idFilter, Boolean assignmentFilter) {
        session.checkSignedIn();
        
        return get(config.getExternalIdsApi(
                offsetKey, pageSize, idFilter, assignmentFilter), EXTERNAL_IDENTIFIER_PAGED_RESOURCE_LIST);
    }
    
    /**
     * Add external identifiers to Bridge. Existing identifiers will be silently ignored.
     * 
     * @param externalIdentifiers
     *      Add the list of external identifiers
     */
    public void addExternalIds(List<String> externalIdentifiers) {
        session.checkSignedIn();
        checkNotNull(externalIdentifiers);

        post(config.getExternalIdsApi(), externalIdentifiers);
    }
    
    /**
     * Delete external identifiers (only allows if external ID validation is diabled).
     * 
     * @param externalIdentifiers
     *      Delete the list of external identifiers
     */
    public void deleteExternalIds(List<String> externalIdentifiers) {
        session.checkSignedIn();
        checkNotNull(externalIdentifiers);
        
        delete(config.getExternalIdsApi(externalIdentifiers));
    }    
}
