package hun;

public interface MsgListener {
	
	/*msg is json formatted. So when msg used, it must be parsed into json object*/
	public abstract void msgReceived(Command msg , String ip);
	
}
