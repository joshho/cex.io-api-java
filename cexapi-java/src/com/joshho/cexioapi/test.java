package com.joshho.cexioapi;

public class test {

	public static void main(String[] args) {
		String username = "";
		String api_key = "";
		String api_secret = "";

		cexapi demo = new cexapi(username, api_key, api_secret);
		System.out.println( "Ticker (GHS/BTC)");
		System.out.println( demo.ticker() ); // or demo.ticker("GHS/BTC")
		System.out.println( "Ticker (BF1/BTC)");
		System.out.println( demo.ticker("BF1/BTC"));
		System.out.println( "Order book (GHS/BTC)");
		System.out.println( demo.order_book() ); // or demo.order_book("GHS/BTC")
		System.out.println( "Order book (BF1/BTC)");
		System.out.println( demo.order_book("BF1/BTC"));
		System.out.println( "Trade history since=100 (GHS/BTC)");
		System.out.println( demo.trade_history(100) ); // or (100,"GHS/BTC")
		System.out.println( "Trade history since=100 (BF1/BTC)");
		System.out.println( demo.trade_history(100,"BF1/BTC"));
		System.out.println( "Balance");
		System.out.println( demo.balance() );
		System.out.println( "Open orders (GHS/BTC)");
		System.out.println( demo.current_orders() ); // or ("GHS/BTC")
		System.out.println( "Open orders (BF1/BTC)");
		System.out.println( demo.current_orders("BF1/BTC"));
		System.out.println( "Cancel order (order_id=100)");
		System.out.println( demo.cancel_order(100));
		/*System.out.println( "Place order buy 4GHS/0.1BTC)");
		System.out.println( demo.place_order("buy",new Double(1), new Double(0.1)) ); // or ("buy",1,0.1,"GHS/BTC")
		System.out.println( "Open orders sell 1BF1/1.5BTC");
		System.out.println( demo.place_order("sell", new Double(1), new Double(1.5), "BF1/BTC"));*/
	}

}
