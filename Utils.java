
public class Utils {

	public static void blockingWait(Object o) {
		while(true) {
			try {
				synchronized (o) {
					o.wait();					
				}
				return;
			} catch (InterruptedException e) {
			}			
		}		
	}
	
}
