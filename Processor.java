
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

/**
 * A processor in a point-to-point communication network
 * A processor has an id, 
 * a list of incoming links,
 * and a list of outgoing lists
 * that link it to other processors.
 * 
 * It runs an instance of an algorithm
 * @author cmshalom
 *
 */
public class Processor implements Runnable {
	private Algorithm algorithm;
	private List<CommunicationLink> incomingLinks = new ArrayList<>();
	private List<CommunicationLink> outgoingLinks = new ArrayList<>();
	private int id;

	/**
	 * @param cls 
	 * The class of the Algorithm to be run
	 * It has to implement the Algorithm I/F
	 * It must have a constructor with one processor parameter
	 */
	public Processor(Class<? extends Algorithm> cls) {
		try {
			Constructor<? extends Algorithm> ctor = 
					cls.getConstructor(Processor.class);
			this.algorithm = ctor.newInstance(this);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public Processor(int id, Class<? extends Algorithm> cls) {
		this(cls);
		this.id = id;
	}
	
	
	// Intentionally not public
	void addIncomingLink(CommunicationLink link) {
		incomingLinks.add(link);
	};
	
	// Intentionally not public
	void addOutgoingLink(CommunicationLink link) {
		outgoingLinks.add(link);
	}
	
	public CommunicationLink getFirstOutgoingLink() {
		return outgoingLinks.get(0);
	}

	public CommunicationLink getRandomOutgoingLink() {
		int index = (int) (outgoingLinks.size() * Math.random());
		return outgoingLinks.get(index);
	}

	public Stream<CommunicationLink> outgoingLinks() {
		return outgoingLinks.stream();
	}

	public Collection<CommunicationLink> getOutgoingLinks() {
		return outgoingLinks;
	}

	public Collection<CommunicationLink> getIncomingLinks() {
		return incomingLinks;
	}
	
	public int getId() {
		return id;
	}

	public void wakeUp() {
		algorithm.onWakeUp();
	}

	@Override
	public void run() {
		while(true) {
			Utils.blockingWait(this); // Wait to be notified
			Message<?> message = fetchMessage();
			assert(message != null);
			algorithm.onMessage(message);
		}
	}

	/**
	 * Fetches a message from one of the incomingLinks
	 * return null of no message is available
	 * @return
	 */
	private Message<?> fetchMessage() {
		Message<?> result;
		for (CommunicationLink link : incomingLinks) {
			result = link.receive();
			if (result != null) return result;
		}
		return null;
	}

}
