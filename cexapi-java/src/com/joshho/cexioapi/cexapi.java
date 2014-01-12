package com.joshho.cexioapi;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONValue;


public class cexapi {
	private final String username;
	private final String api_key;
	private final String api_secret;
	private String nonce;

	/**
	 * 
	 * @param username
	 * @param api_key
	 * @param api_secret
	 */
	public cexapi(String username, String api_key, String api_secret){
		this.username = username;
		this.api_key = api_key;
		this.api_secret = api_secret;
	}

	private void generateNonce(){
		nonce = ""+System.currentTimeMillis();
	}

	private String getSignature() throws InvalidKeyException, NoSuchAlgorithmException, DecoderException{
		String line = nonce + username + api_key;
		return getHMac(line);
	}

	private String getHMac(String line) throws NoSuchAlgorithmException, InvalidKeyException, DecoderException{
		SecretKeySpec keySpec = new SecretKeySpec(api_secret.getBytes(),"HmacSHA256");

		Mac mac = Mac.getInstance("HmacSHA256");
		mac.init(keySpec);
		byte[] hmacBytes = mac.doFinal(line.getBytes());

		return Hex.encodeHexString(hmacBytes);
	}

	private Object api_call(String method, HashMap<String,String> hmap, Integer authData){ // api call (Middle level)
		return api_call(method, hmap, authData, null);
	}

	private Object api_call(String method, HashMap<String,String> hmap, Integer authData, String couple){ // api call (Middle level)
		if(hmap == null){hmap = new HashMap<String, String>();}
		String path = "/api/" + method + "/";//generate url
		Object answer = null;
		try{
			if (couple != null){
				path = path + couple + "/";//
			}
			if (authData == 1){ //add auth-data if needed
				generateNonce();
				hmap.put("key", api_key);
				hmap.put("signature", getSignature());
				hmap.put("nonce", nonce);
			}
			answer = post("https://www.cex.io"+path, hmap); //Post Request
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (DecoderException e) {
			e.printStackTrace();
		}
		return answer;
	}

	private Object post(String strUrl, HashMap<String,String> hmap){//Post Request (Low Level API call)
		HttpPost httppost = new HttpPost(strUrl);
		try {
			HttpClient httpclient = HttpClientBuilder.create().build();

			List <NameValuePair> nvps = new ArrayList <NameValuePair>();
			for(String key: hmap.keySet()){
				nvps.add(new BasicNameValuePair(key, hmap.get(key)));
			}

			httppost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
			httppost.setHeader("User-Agent", "bot-cex.io-"+username);

			HttpResponse response = httpclient.execute(httppost);
			HttpEntity responseEntity = response.getEntity();

			String jsonResultString = EntityUtils.toString(responseEntity);

			return JSONValue.parse(jsonResultString);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			httppost.releaseConnection();
		}
		return null;
	}

	public Object ticker(){
		return ticker(null);
	}

	public Object ticker(String couple){
		if(couple == null) couple = "GHS/BTC";
		return api_call("ticker", null, 0, couple);
	}

	public Object order_book(){
		return order_book(null);
	}

	public Object order_book(String couple){
		if(couple == null) couple = "GHS/BTC";
		return api_call("order_book", null, 0, couple);
	}

	public Object trade_history(Integer since){
		return trade_history(since, null);
	}

	public Object trade_history(Integer since, String couple){
		if(since == null) since = 1;
		if(couple == null) couple = "GHS/BTC";
		HashMap<String, String> hmap = new HashMap<String,String>();
		hmap.put("since", since.toString());
		return api_call("trade_history", hmap, 0, couple);
	}

	public Object balance(){
		return api_call("balance", null, 1);
	}

	public Object current_orders(){
		return current_orders(null);
	}

	public Object current_orders(String couple){
		if(couple == null) couple = "GHS/BTC";
		return api_call("open_orders", null, 1, couple);
	}

	public Object cancel_order(Integer order_id){
		HashMap<String, String> hmap = new HashMap<String,String>();
		hmap.put("id", order_id.toString());
		return api_call("cancel_order", hmap, 1);
	}

	public Object place_order(String ptype,Double amount,Double price){
		return place_order(ptype,amount,price);
	}

	public Object place_order(String ptype,Double amount, Double price, String couple){
		if(ptype==null) ptype="buy";
		if(amount==null || amount < 0) amount = new Double(1);
		if(price == null || price < 0) price = new Double(1);
		if(couple == null) couple = "GHS/BTC";

		HashMap<String, String> hmap = new HashMap<String,String>();
		hmap.put("type", ptype);
		hmap.put("amount", amount.toString());
		hmap.put("price", price.toString());
		return api_call("place_order", hmap, 1, couple);
	}
}
