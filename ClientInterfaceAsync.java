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

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface ClientInterfaceAsync
{
    void login(String input, String input1, AsyncCallback<LoginResult> callback)
            throws IllegalArgumentException;

    void getExperimentsForUser(boolean is_guest,
            AsyncCallback<Vector<Experiment>> callback)
            throws IllegalArgumentException;

    void getUnitsForUser(boolean guest_only,
            AsyncCallback<Vector<Unit>> callback)
            throws IllegalArgumentException;

    void getExperimentDetail(int input,
            AsyncCallback<ExperimentDetailRes> callback)
            throws IllegalArgumentException;

    void addDonor(String donorName, int organismId, String sex, Float ageStart,
            Float ageEnd, String ageUnit, String developmentStage,
            String strain, String description, AsyncCallback<Integer> callback)
            throws IllegalArgumentException, NumberFormatException, Exception;

    void getListItems(SQLListBoxQuery.items queryType, String[] params,
            AsyncCallback<Vector<SQLListItem>> callback)
            throws IllegalArgumentException;

    void addSample(String input, int donorId, String input2, int project,
            AsyncCallback<Integer> callback) throws IllegalArgumentException;

    /*
     * void makeMyKey(int userID, String unitName, int sampleId, int projectID,
     * int agentId, String platformName, int facilityId, String description,
     * String subtype, String scridText, AsyncCallback<String> callback) throws
     * IllegalArgumentException;
     */

    void getUnitDetail(int unitId, AsyncCallback<Unit> callback)
            throws IllegalArgumentException;

    void getTimeCourseMaps(
            String[] samples,
            String[] agents,
            AsyncCallback<ArrayList<HashMap<String, HashMap<String, Integer>>>> callback)
            throws IllegalArgumentException, Exception;

    void getStatistics(AsyncCallback<StatResult> callback)
            throws IllegalArgumentException;

    void recycleKey(String key, AsyncCallback<RecycleKeyResult> callback)
            throws IllegalArgumentException;

    void createDownload(String filename, Vector<Integer> experiment_id,
            Vector<Integer> unit_id, Vector<Integer> file_id, int seconds,
            AsyncCallback<String> callback) throws IllegalArgumentException,
            Exception;

    void cloudPublish(String filename, Vector<Integer> experiment_id,
            Vector<Integer> unit_id, Vector<Integer> file_id,
            AsyncCallback<String> callback);

    // void postMail(String to, String sub, String msg, int from, String cc,
    // int project, AsyncCallback<Integer> callback);

    void sendKeyData(Vector<KeyGenData> data,
            AsyncCallback<Vector<KeyGenData>> callback) throws Exception;

    void fetchDesignAndUnits(AsyncCallback<Vector<Unit>> callback)
            throws IllegalArgumentException, Exception;

    void insertNewExperiment(String username, boolean isPublic, String name,
            int projectId, Vector<String> ve, Vector<String> vc,
            AsyncCallback<Integer> callback) throws IllegalArgumentException,
            Exception;

    void createConfigFile(String filename, String data,
            AsyncCallback<Boolean> callback) throws IllegalArgumentException,
            Exception;

    void configCreatorGetRunName(AsyncCallback<String> callback)
            throws IllegalArgumentException, Exception;

    void getIndexBarcode(String cistrackId, AsyncCallback<String> callback)
            throws IllegalArgumentException, Exception;

    void addUser(String fName, String lName, String uName, String address,
            String email, String phone, String fax, String website,
            String password, int affiliation, String role, int personnelType,
            AsyncCallback<Integer> callback) throws IllegalArgumentException,
            Exception;

    void getLibraryForSample(String cistrackID, boolean keepOnlyLast,
            AsyncCallback<Vector<LibraryContents>> callback) throws Exception;

    void verifyUser(long key, int userid, AsyncCallback<Integer> callback)
            throws Exception;

    void createEmailWithUrl(String msg, String action, String subject,
            String uName, AsyncCallback<Integer> callback) throws Exception;

    void resetPassword(String password, int userid,
            AsyncCallback<Integer> callback) throws Exception;

    //void addLibrary(String cistrackID, LibraryEntry le, AsyncCallback<String> b)
    //        throws Exception;

    void addToLibrary(String name, LibraryContents lc, AsyncCallback<String> b);

    void getLibrary(String library_name,
            AsyncCallback<Vector<LibraryContents>> callback) throws Exception;

    void getPermissionsFor(PermissionsEntity pe, int id,
            AsyncCallback<BNPermissions> callback);

    void createUpdateRequestOffer(RequestOffer req,
            AsyncCallback<RequestOffer> callback) throws Exception;

    void getAllRequestOffers(AsyncCallback<Vector<RequestOffer>> callback)
            throws Exception;

    void setPermissions(int id, PermissionsEntity pe, RW read_write,
            Vector<Integer> new_groups, AsyncCallback<Integer> result);

    void getProjects(AsyncCallback<Vector<Project>> result) throws Exception;

    // openid
    void login(String input, AsyncCallback<LoginResult> handleLogin)
            throws IllegalArgumentException;

    void exportToGalaxy(String bionimbusID, AsyncCallback<String> galaxyArgs)
            throws IllegalArgumentException;
}
