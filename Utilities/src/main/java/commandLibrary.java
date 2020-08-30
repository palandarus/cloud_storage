import java.util.Arrays;
import java.util.List;

public class commandLibrary {
    public static final String AUTHORIZE_REQUEST="AUTH_REQUEST";
    public static final String AUTHORIZE_ACCEPT="AUTH_ACCEPT";
    public static final String AUTHORIZE_DENIED="AUTH_DENIED";

    public static final String REGISTRATION_REQUEST="REG_REQUEST";
    public static final String REGISTRATION_ACCEPT="REGISTRATION_ACCEPT";
    public static final String REGISTRATION_DENIED="REGISTRATION_DENIED";

    public static final String GET_FILES_LIST="GET_FILES_LIST";
    public static final String GET_ROOT_FILES_LIST="GET_ROOT_FILES_LIST";
    public static final String SEND_FILE="SEND_FILE";
    public static final String SEND_DIR="SEND_DIR";
    public static final String RECEIVE_FILE="RECEIV_FILE";
    public static final String READY_TO_RECEIVE_FILE="READY_TO_RECEIVE_FILE";
    public static final String RECEIVE_DIR="RECEIV_DIR";
    public static final String READY_TO_RECEIVE_DIR="READY_TO_RECEIVE_DIR";
    public static final String DELETE_FILE="DELETE_FILE";
    public static final String DELIMETER="⊗";
    public static final String MAKE_DIRECTORY="MAKE_DIR";
    public static final String FILE_SEND_COMPLETE="FILE_SEND";
    public static final String ERROR_FILE_RECEIVED="ERROR_RECEIVED_FILE";
    public static final String ERROR_FILE_DELETE="ERROR_DELETE_FILE";

    public static String makeDirMessage(String dirName, String path){
        return MAKE_DIRECTORY+DELIMETER+dirName+DELIMETER+path;
    }

    public static String deleteFileMessage(String fileName, String fullPath){
        return DELETE_FILE+DELIMETER+fileName+DELIMETER+fullPath;
    }

    public static String getRootFilesListMessage(long userId){
        return GET_ROOT_FILES_LIST+DELIMETER+userId;
    }

    public static String getFilesListMessage(String path){
        return GET_FILES_LIST+DELIMETER+path;
    }

    public static String requestFileMessage(String fileName, String path, String downloadPath){
        return SEND_FILE+DELIMETER+fileName+DELIMETER+path+DELIMETER+downloadPath;
    }

    public static String requestDirMessage(String dirName, String path){
        return SEND_DIR+DELIMETER+dirName+DELIMETER+path;
    }

    public static String sendingFileCompleteMessage(String fileName, String destinationPath, long fileLength){
        return FILE_SEND_COMPLETE+DELIMETER+fileName+DELIMETER+destinationPath+DELIMETER+fileLength;
    }

    public static String fileReceivedWithMissingDataMessage(String fileName){
        return ERROR_FILE_RECEIVED+DELIMETER+fileName;
    }

    public static String fileDeleteErrorMessage(String fileName){
        return ERROR_FILE_DELETE+DELIMETER+fileName;
    }

    public static String authorizationRequestMessage(String login, String password){
        return AUTHORIZE_REQUEST+DELIMETER+login+DELIMETER+password;
    }

    public static String registrationRequestMessage(String login, String password){
        return REGISTRATION_REQUEST+DELIMETER+login+DELIMETER+password;
    }

    public static String authorizationAccessMessage(long userID){
        return AUTHORIZE_ACCEPT+DELIMETER+userID;
    }

    public static String authorizationDeniedMessage(String login){
        return AUTHORIZE_DENIED+DELIMETER+login;
    }

    public static String registrationAccessMessage(long userID){
        return REGISTRATION_ACCEPT+DELIMETER+userID;
    }

    public static String registrationDeniedMessage(String login){
        return REGISTRATION_DENIED+DELIMETER+login;
    }

    public static String getReadyToReceiveFileMessage(String path){
        return READY_TO_RECEIVE_FILE+DELIMETER+path;
    }

    public static String getReadyToReceiveDIRMessage(String path){
        return READY_TO_RECEIVE_DIR+DELIMETER+path;
    }


}