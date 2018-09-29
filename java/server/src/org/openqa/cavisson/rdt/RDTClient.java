package org.openqa.cavisson.rdt;


import com.google.gson.GsonBuilder;
import java.util.logging.Logger;
import org.openqa.cavisson.AdbCommandExecutor;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import java.util.Map;

public class RDTClient{
  private static final Logger log = Logger.getLogger(RDTClient.class.getName());
  private AdbCommandExecutor adbCommandExecutor = null ;
  private final String JSON_SESSION_ID_KEY = "server:CONFIG_UUID" ;
  private final String WEB_STORAGE_ENABLED = "webStorageEnabled" ;
  private final String TAKE_SCREENSHOT = "takesScreenshot" ;
  private final String JAVA_SCRIPT_ENABLED = "javascriptEnabled" ;
  private final String DATABASE_ENABLED = "databaseEnabled" ;
  private final String NETWORK_CONNECTION_ENABLED = "networkConnectionEnabled" ;
  private final String LOCATION_CONTEXT_ENABLED = "locationContextEnabled" ;
  private final String PLATFORM_NAME = "platformName" ;
  private final String APP_ACTIVITY = "appActivity" ;
  private final String APP_PACKAGE = "appPackage" ;
  private final String DEVICE_NAME = "deviceName" ;
  private final String DEVICE_VERSION = "version" ;
  private final String DEVICE_UDID = "deviceUDID" ;
  private final String PLATFORM_VERSION = "platformVersion" ;
  private final String DEVICE_SCREEN_SIZE = "deviceScreenSize" ;
  private final String DEVICE_MODEL = "deviceModel" ;
  private final String DEVICE_MANUFACTURER = "deviceManufacturer"; 
  private final String RDT_SESSION_ID = "sessionId"; 
  private Gson gson ;
  private JsonParser jsonParser ;
  private Map<String,Object> desiredCapabilities ;
  private Map<String,Object> extraCapabilities ;
  public RDTClient()throws Exception{
    adbCommandExecutor = new AdbCommandExecutor();
    gson = new Gson();
    jsonParser = new JsonParser();
  }
  public RDTClient(String deviceId)throws Exception{
    adbCommandExecutor = new AdbCommandExecutor(deviceId);
    gson = new Gson();
    jsonParser = new JsonParser();
  }
  public String createSession(RDTBasedRequest rdtBasedRequest){
    try{
      desiredCapabilities = rdtBasedRequest.getDesiredCapabilities();
      extraCapabilities = rdtBasedRequest.getExtraCapabilities();
      String method = rdtBasedRequest.getMethod();
      String requestPath = rdtBasedRequest.getRequestPath();
      boolean newSessionRequest = rdtBasedRequest.getNewSessionrequest(); 
      log.fine(" desired capabilities "+ desiredCapabilities + " Extra Capabilities " + extraCapabilities  + " Method " + method  + "Request Path" + requestPath  + " New SessionRequest " + newSessionRequest );
      String releaseVersion = adbCommandExecutor.getReleaseVersion();
      String buildVersion = adbCommandExecutor.getBuildVersion();
      String manufacturer = adbCommandExecutor.getManufacturer();
      String model = adbCommandExecutor.getModel();
      String windowSize = adbCommandExecutor.getWindowSize();
      String platform = desiredCapabilities.containsKey("platform")?desiredCapabilities.get("platform").toString():"";
      String deviceName = adbCommandExecutor.getDeviceName();      
      boolean isPackageAvailable = adbCommandExecutor.isPackageAvailableOnDevice(desiredCapabilities.get(APP_PACKAGE).toString());
      boolean launchApplicationByName = adbCommandExecutor.launchApplicationByPackageName(desiredCapabilities.get(APP_PACKAGE).toString(),desiredCapabilities.get(APP_ACTIVITY).toString()); 
      log.fine(" Release Version = " + releaseVersion+ " SDK Version  = " + buildVersion + " Manufacturer  = " + manufacturer +" Model = " + model+" Window Size = " + windowSize+" Is Package Available on Device =" + isPackageAvailable+" Starting the package on Device =" + launchApplicationByName); 
      JsonObject jsonObject = new JsonObject();
      JsonObject jsonValue = new JsonObject();
      JsonObject desiredJsonCapability =(JsonObject)jsonParser.parse(gson.toJson(rdtBasedRequest.getDesiredCapabilities()));
      JsonObject jsonCapability = new JsonObject();
      JsonObject jsonWarning = new JsonObject();
      jsonCapability.add("desired",(JsonElement)desiredJsonCapability);
      jsonCapability.add("warnings",(JsonElement)jsonWarning);
      jsonCapability.addProperty(WEB_STORAGE_ENABLED,true);
      jsonCapability.addProperty(TAKE_SCREENSHOT,true);
      jsonCapability.addProperty(JAVA_SCRIPT_ENABLED,true);
      jsonCapability.addProperty(DATABASE_ENABLED,true);
      jsonCapability.addProperty(NETWORK_CONNECTION_ENABLED,true);
      jsonCapability.addProperty(LOCATION_CONTEXT_ENABLED,true);
      for (String key : desiredJsonCapability.keySet()){
        jsonCapability.addProperty(key,desiredJsonCapability.get(key).getAsString());
      }
      jsonCapability.addProperty(DEVICE_NAME,deviceName);
      jsonCapability.addProperty(DEVICE_UDID,deviceName);
      jsonCapability.addProperty(DEVICE_SCREEN_SIZE,windowSize);
      jsonCapability.addProperty(DEVICE_MODEL,model);
      jsonCapability.addProperty(DEVICE_MANUFACTURER,manufacturer);
      jsonCapability.addProperty(PLATFORM_VERSION,releaseVersion);
      jsonValue.add("capabilities",(JsonElement)jsonCapability);
      jsonValue.addProperty("sessionId",desiredCapabilities.get(JSON_SESSION_ID_KEY).toString());
      jsonObject.add("value",(JsonElement)jsonValue);
      log.fine(jsonObject.toString());
      return jsonObject.toString();
    }catch(Exception e){log.fine(" Exception Caught "+e.getMessage());return "";}

  }
  public String execute(RDTBasedRequest rdtBasedRequest){
    if(rdtBasedRequest.getNewSessionrequest()){
      return createSession(rdtBasedRequest);      
    }
    else{
      return "";
    }
  }

}
