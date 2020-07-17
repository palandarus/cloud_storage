package DataBase;


public class User{
    private UserTable table;
    private String login;
    private String password;
    private String serverDirectory;
    private String userDirectory;
    private long userId;

    public User(String login, String password, String serverDirectory) {
        this.login = login;
        this.password = password;
        this.serverDirectory=serverDirectory;
        this.table = new UserTable();
    }

    public User(String login, String password) {
        this.login = login;
        this.password = password;
        this.table = new UserTable();
    }



    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getUserDirectory() {
        return userDirectory;
    }

    public boolean createNewUser() {
        if (!isUserExists()) {
            table.createNewUser(this);
            this.userId=  table.getUserId(login);
            this.userDirectory=serverDirectory+userId+"\\";
            return true;
        }
        else return false;
    }

    private boolean isUserExists() {
        return table.isUserExists(this.login);
    }

    public boolean authorization() {
        if(table.checkAuth(this.login, this.password)){
            this.userId=  table.getUserId(login);
            this.userDirectory=serverDirectory+userId+"\\";
            return true;
        }
        else return false;
    }

    public long getId() {
        return this.userId;
    }

    public void setServerDirectory(String serverDirectory) {
        this.serverDirectory = serverDirectory;
    }

    @Override
    public boolean equals(Object obj) {
        return this.userId==((User)obj).getId();
    }
}
