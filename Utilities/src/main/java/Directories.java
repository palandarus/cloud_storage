public enum Directories {
    SERVER_FILES_DIRECTORY (".\\server\\src\\main\\resources\\serverPath\\"), SERVER_TEMP_PATH(".\\server\\src\\main\\resources\\serverPath\\temp\\"), CLIENT_FILES_DIRECTORY (".\\client\\src\\main\\resources\\clientPath\\");
    private String path;
    Directories(String path){
       this.path=path;
    }
    public String getPath(){ return this.path;}
}
