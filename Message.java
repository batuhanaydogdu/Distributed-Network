
public abstract class Message<T extends Algorithm> {
	private Processor from;
	private Processor to;
	
	public Message(Processor from, Processor to) {
		assert(from != null && to != null);
		this.from = from;
		this.to = to;
	}

	public Message(Processor from) {
		assert(from != null);
		this.from = from;
	}
		
	public Processor getSource() {
		return from;
	}
	
	public Processor getDestination() {
		return to;
	}

	protected void setDestination(Processor to) {
		assert(this.to == null);
		this.to = to;
	}
}
