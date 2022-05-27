
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A Point-to-point FIFO communication link
 * It should be run in a thread so that random delays may be introduced
 * No multi-hop messages are supported
 * 
 * Implementation details:
 * Send messages are added to a queue
 * The message at the top of the queue is made available to the receiver after a random delay time
 * No other message is made available w/o before this one is fetched by the recipient
 *  
 * @author cmshalom
 *
 */
public class CommunicationLink implements Runnable {
	public static final int MIN_DELAY_MILLIS = 10;
	public static final int MAX_DELAY_MILLIS = 100;
	private Processor from;
	private Processor to;
	private BlockingQueue<Message<?>> pendingMessages = new LinkedBlockingQueue<>();
	private Message<?> readyForDelivery;
	
	public CommunicationLink(Processor from, Processor to) {
		this.from = from;
		this.to = to;
		from.addOutgoingLink(this);
		to.addIncomingLink(this);
	}
	
	public Processor getSource() {
		return from;
	}

	public Processor getDestination() {
		return to;
	}

	/**
	 * Sending a message 
	 * The originator of the message should be the source of the link
	 * and its destination should be the destination of the link.
	 * The destination may be null in which case it will automatically added by this method
	 * @param msg
	 */
	public void send(Message<?> msg) {
		assert(msg.getSource() == from);
		if (msg.getDestination() == null) {
			msg.setDestination(to);
		}
		assert(msg.getDestination() == to);
		// Simply append the message to the queue
		pendingMessages.add(msg);
	}
	
	/**
	 * Returns a message if available and null otherwise.
	 * @return
	 */
	public synchronized Message<?> receive() {
		if (readyForDelivery == null) return null;
		Message<?> result = readyForDelivery;
		readyForDelivery = null;
		this.notify();  // Can make a new message available
		return result;
	}
	
	@Override
	public void run() {
		while(true) {
			try {
				if (readyForDelivery != null) {
					Utils.blockingWait(this); // Wait for the recipient to collect the message
				}
				readyForDelivery = pendingMessages.take();
				Thread.sleep(randomDelay());
				synchronized (to) {
					to.notify();					
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private static int randomDelay() {
		return (int) ((MAX_DELAY_MILLIS-MIN_DELAY_MILLIS)*Math.random() + MIN_DELAY_MILLIS);
	}
	
}
