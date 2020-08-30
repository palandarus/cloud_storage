import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ServerRunner {
    public static void main(String[] args) {
//        ApplicationContext context=new AnnotationConfigApplicationContext(SpringConfig.class);
        ApplicationContext context= new ClassPathXmlApplicationContext("spring-context.xml");
        FileServer fileServer=context.getBean("fileServer",FileServer.class);
        try {
            fileServer.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
