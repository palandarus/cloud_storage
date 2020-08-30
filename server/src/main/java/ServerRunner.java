import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ServerRunner {
    public static void main(String[] args) {
        ApplicationContext context=new AnnotationConfigApplicationContext(SpringConfig.class);
        FileServer fileServer=context.getBean("fileServer",FileServer.class);
        try {
            fileServer.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
