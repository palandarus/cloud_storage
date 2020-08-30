import DataBase.DBHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;


@Configuration
public class SpringConfig {

    @Bean
    public FileServer fileServer(DBHelper dbHelper, FileServerHandler fileServerHandler) {
    return new FileServer(dbHelper, fileServerHandler);
    }

    @Bean
    public FileServerHandler fileServerHandler(DBHelper dbHelper){
        return new FileServerHandler(dbHelper);
    }

    @Bean
    public DBHelper dbHelper(){
        return DBHelper.getInstance();
    }

}
