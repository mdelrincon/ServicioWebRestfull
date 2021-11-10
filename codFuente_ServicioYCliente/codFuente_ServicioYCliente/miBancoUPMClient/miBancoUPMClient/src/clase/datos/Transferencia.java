package clase.datos;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "transferencia")
public class Transferencia {

	//Atributos
	private int id;
	private int cuentaRealiza;
	private int cuentaRecibe;
	private String fecha;
	private int saldoTransferido;
	
	//Constructor
	public Transferencia(int cuentaRealiza,int cuentaRecibe,String fecha,int saldoTransferido) {
		this.cuentaRealiza=cuentaRealiza;
		this.cuentaRecibe=cuentaRecibe;
		this.fecha=fecha;
		this.saldoTransferido=saldoTransferido;
	}
	
	public Transferencia(){
		
	}
	
	@XmlAttribute(required=false)
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id=id;
	}
	
	public int getCuentaRealiza() {
		return this.cuentaRealiza;
	}

	public void setCuentaRealiza(int cuenta) {
		this.cuentaRealiza=cuenta;
	}
	
	public int getCuentaRecibe() {
		return this.cuentaRecibe;
	}

	public void setCuentaRecibe(int cuenta) {
		this.cuentaRecibe=cuenta;
	}
	
	public String getFecha() {
		return this.fecha;
	}

	public void setFecha(String fecha) {
		this.fecha=fecha;
	}
	
	public int getSaldoTransferido() {
		return this.saldoTransferido;
	}

	public void setSaldoTransferido(int saldo) {
		this.saldoTransferido=saldo;
	}
	
}
