package org.lac.bionimbus.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import org.lac.bionimbus.shared.BNPermissions;
import org.lac.bionimbus.shared.BNPermissions.PermissionsEntity;
import org.lac.bionimbus.shared.BNPermissions.RW;
import org.lac.bionimbus.shared.Experiment;
import org.lac.bionimbus.shared.ExperimentDetailRes;
import org.lac.bionimbus.shared.KeyGenData;
import org.lac.bionimbus.shared.LibraryContents;
import org.lac.bionimbus.shared.LoginResult;
import org.lac.bionimbus.shared.Project;
import org.lac.bionimbus.shared.RecycleKeyResult;
import org.lac.bionimbus.shared.RequestOffer;
import org.lac.bionimbus.shared.SQLListBoxQuery;
import org.lac.bionimbus.shared.SQLListItem;
import org.lac.bionimbus.shared.StatResult;
import org.lac.bionimbus.shared.Unit;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet")
public interface ClientInterface extends RemoteService
{
    LoginResult login(String username, String password) throws Exception;

    Vector<Experiment> getExperimentsForUser(boolean is_guest) throws Exception;

    Vector<Unit> getUnitsForUser(boolean guest_only) throws Exception;

    ExperimentDetailRes getExperimentDetail(int input) throws Exception;

    Integer addDonor(String donorName, int organismId, String sex,
            Float ageStart, Float ageEnd, String ageUnit,
            String developmentStage, String strain, String description)
            throws Exception;

    Vector<SQLListItem> getListItems(SQLListBoxQuery.items type, String[] params)
            throws Exception;

    Integer addSample(String input, int donorId, String comments, int projectid)
            throws Exception;

    /*
     * makeMyKey(int userID, String unitName, int sampleId, int projectID, int
     * agentId, String platformName, int facilityId, String description, String
     * subtype, String scridText) throws IllegalArgumentException;
     */

    // Integer postMail(String to, String sub, String msg, int from, String cc,
    // int project) throws IllegalArgumentException, Exception;

    Unit getUnitDetail(int unitId) throws Exception;

    ArrayList<HashMap<String, HashMap<String, Integer>>> getTimeCourseMaps(
            String[] samples, String[] agents) throws IllegalArgumentException,
            Exception;

    String createDownload(String filename, Vector<Integer> experiment_id,
            Vector<Integer> unit_id, Vector<Integer> file_id, int seconds)
            throws Exception;

    String cloudPublish(String filename, Vector<Integer> experiment_id,
            Vector<Integer> unit_id, Vector<Integer> file_id) throws Exception;

    StatResult getStatistics() throws Exception;

    RecycleKeyResult recycleKey(String key) throws Exception;

    Vector<KeyGenData> sendKeyData(Vector<KeyGenData> data) throws Exception;

    Vector<Unit> fetchDesignAndUnits() throws IllegalArgumentException,
            Exception;

    Integer insertNewExperiment(String username, boolean isPublic, String name,
            int projectId, Vector<String> ve, Vector<String> vc)
            throws IllegalArgumentException, Exception;

    Boolean createConfigFile(String filename, String data)
            throws IllegalArgumentException, Exception;

    String configCreatorGetRunName() throws IllegalArgumentException, Exception;

    String getIndexBarcode(String cistrackId) throws IllegalArgumentException,
            Exception;

    Integer addUser(String fName, String lName, String uName, String address,
            String email, String phone, String fax, String website,
            String password, int affiliation, String role, int personnelType)
            throws IllegalArgumentException, Exception;

    Vector<LibraryContents> getLibraryForSample(String cistrackID,
            boolean keepOnlyLast) throws Exception;

    Integer verifyUser(long key, int userid) throws Exception;

    Integer createEmailWithUrl(String msg, String action, String subject,
            String uName) throws Exception;

    Integer resetPassword(String password, int userid) throws Exception;

    //String addLibrary(String cistrackID, LibraryEntry le) throws Exception;

    String addToLibrary(String name, LibraryContents le) throws Exception;

    Vector<LibraryContents> getLibrary(String library_name) throws Exception;

    BNPermissions getPermissionsFor(PermissionsEntity pe, int id)
            throws Exception;

    RequestOffer createUpdateRequestOffer(RequestOffer req) throws Exception;

    Vector<RequestOffer> getAllRequestOffers() throws Exception;

    Integer setPermissions(int id, PermissionsEntity pe, RW read_write,
            Vector<Integer> new_groups) throws Exception;

    Vector<Project> getProjects() throws Exception;

    // openid
    LoginResult login(String openid) throws Exception;

    String exportToGalaxy(String bionimbusID);
}
