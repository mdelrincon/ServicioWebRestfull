package clase.datos;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.bind.annotation.XmlAttribute;

public class LinkSaldo {
	private URL url;
	private String rel;
	private int saldo;
	
	@XmlAttribute(name="href")
	public URL getUrl() {
		return url;
	}
	public void setUrl(URL url) {
		this.url = url;
	}
	
	@XmlAttribute
	public String getRel() {
		return rel;
	}
	public void setRel(String rel) {
		this.rel = rel;
	}
	
	@XmlAttribute
	public int getSaldo() {
		return saldo;
	}
	public void setSaldo(int saldo) {
		this.saldo = saldo;
	}
	
	public LinkSaldo(String url, String rel, int saldo) {
		try {
			this.url = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		this.rel = rel;
		this.saldo=saldo;
	}
	
	public LinkSaldo() {

	}
}
