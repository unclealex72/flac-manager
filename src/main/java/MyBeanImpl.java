import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import com.google.inject.Singleton;

@Singleton
public class MyBeanImpl {
	
	@PostConstruct
	public void start() {
		print("start");
	}
	public void hello() {
		print("hello");
	}
	@PreDestroy
	public void stop() {
		print("stop");
	}
	private void print(String string) {
		System.out.println(string);
  }
}