import groovyx.net.http.ContentType
import groovyx.net.http.RESTClient
import org.apache.commons.codec.binary.Base64
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.lang.RandomStringUtils

/**
 * User: porter
 * Date: 09/03/2012
 * Time: 17:40
 */
abstract class BaseIntegrationTst extends GroovyTestCase {


    protected static final String DATE_STRING = com.sample.web.util.DateUtil.currentDateAsIso8061String;

    private static final BASE_URL = "http://localhost:" + System.getProperty("tomcatPort", "8080") + "/sample-web/";




   RESTClient restClient;

   RESTClient getRestClient() {
       if (restClient == null) {
           restClient = new RESTClient(BASE_URL);
       }
       return restClient;
   }

    public Object httpSignUpUser(String username, String password) {
        return getRestClient().post(path: "user", contentType: ContentType.JSON, body: getCreateUserRequest(username, password))
    }

    protected Object httpGetUser(def sessionToken, def requestingUserId, def userToGet) {
       def path = "user/" + userToGet
       def authToken = calculateAuthToken(sessionToken, path + ",GET")
       def userResponse = getRestClient().get(path: path, contentType: ContentType.JSON,
               headers: ['Authorization': requestingUserId + ":" + authToken, "x-rest-sample-date": DATE_STRING])
       return userResponse
   }

    public Object httpUpdateUser(userToken, userId, updateRequest) {
        def path =  "user/"  + userId;
       def authToken = calculateAuthToken(userToken, path +",PUT")
        return getRestClient().put(path: path, contentType: ContentType.JSON, body: updateRequest,
                headers: ['Authorization': userId + ":" + authToken, "x-rest-sample-date": DATE_STRING])
    }

    protected Object httpDeleteUser(def userId, def sessionToken) {
       def path = "user/" + userId
       def authToken = calculateAuthToken(sessionToken, path + ",DELETE")
       def response = getRestClient().delete(path: path, contentType: ContentType.JSON,
               headers: ['Authorization': userId + ":" + authToken, "x-rest-sample-date": DATE_STRING])
       return response
   }

    protected String getLastIdFromResponse(Object response) {
       String location = response.headers.'Location'
       return location.substring(location.lastIndexOf("/") + 1 )
    }


    protected String getCreateUserRequest(String username, String password) {
        return """{"user":{"emailAddress":""" + '\"' + username + '\"' + """},"password":""" + '\"' + password + '\"' + """}"""
    }

    protected String getLoginRequest(String username, String password) {
        return """{"username":""" + '\"' + username + '\"' + ""","password":""" + '\"' + password + '\"' + """}"""
    }

    protected String getNoRootUserRequest(String username, String password) {
        return """{"username":""" + '\"' + username + '\"' + ""","password":""" + '\"' + password + '\"' + """}"""
    }

    protected String getEmailAddressRequest(String emailAddress) {
        return "{" + getJsonNameValue("emailAddress", emailAddress) + "}"
    }

    protected String getJsonNameValue(String name, Object value) {
         return '\"' + name + '\":\"' +  value + '\"'
    }


    protected String createRandomUserName() {
        return RandomStringUtils.randomAlphabetic(8) + "@example.com";
    }

    protected String calculateAuthToken(String sessionToken, String stringToHash) {
        byte[] digest = DigestUtils.sha256(sessionToken + ":" + stringToHash + "," + DATE_STRING);
        return new String(Base64.encodeBase64(digest));
    }

}
