package hun;

public class Auction implements Runnable{

	private int id;
	private String title;
	private String description;
	private String winnerIp;
	private float winnerPrice;
	
	private int timeRemain = 0;

	private AppServer server;
	public Auction(int timeLimit , AppServer server , String title , String description , int id , float initialPrice)
	{
		this.id = id;
		this.title = title;
		this.description = description;
		this.server = server;
		timeRemain = timeLimit * 10;
		this.winnerPrice = initialPrice;
		
		this.winnerIp = "0.0.0.0";
		
	}
	
	private ErrorListener el;
	public void setErrorListener(ErrorListener el)
	{
		this.el = el;
	}
	private MsgListener ml;
	public void setMsgListener(MsgListener ml)
	{
		this.ml = ml;
	}
	
	public void start()
	{
		Thread auctionThread = new Thread(this);
		auctionThread.start();
	}
	
	public void stop()
	{
		timeRemain = 0;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("New Auction Started.");
		Command com = new Command();
		com.put("req", "Auction started");
		ml.msgReceived(com , "auction");
		while(timeRemain > 0)
		{
			timeRemain --;
//			System.out.println(timeRemain);
			try {
				Thread.sleep(100);
				Command com2 = new Command();
				com2.put("req" , "TIME");
				com2.put("time", "" + timeRemain / 10 + "." + timeRemain % 10 + "s");
				ml.msgReceived(com2, "auction");
				if(server.isServerClosed())
				{
					Command com1 = new Command();
					com1.put("req", "Auction Closed");
					System.out.println("Auction closed.");
					ml.msgReceived(com1, "auction");
					timeRemain = 0;
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				System.out.println(e.getMessage());
				el.errorOccured("Error occured : " + e.getMessage(), "Auction");
			}
			String counter = "" + timeRemain / 10 + "." + timeRemain % 10 + "s";
			Command com1 = new Command();
			com1.put("req" , "TIME");
			com1.put("time", counter);
			com1.put("title", this.title);
			com1.put("description" , this.description);
			com1.put("price" , "" + this.winnerPrice);
			com1.put("winner", this.winnerIp);
			server.sendMsg(com1);
		}
		Command com1 = new Command();
		com1.put("req", "Auction Closed");
		System.out.println("Auction closed.");
		ml.msgReceived(com1, "auction");
	}
	
	public void setWinnerIp(String ip)
	{
		this.winnerIp = ip;
	}
	public String getWinnerIp()
	{
		return this.winnerIp;
	}
	public void setWinnerPrice(float price)
	{
		this.winnerPrice = price;
	}
	public float getLastWinnerPrice()
	{
		return this.winnerPrice;
	}
	public int getTimeRemain()
	{
		return this.timeRemain;
	}

}
