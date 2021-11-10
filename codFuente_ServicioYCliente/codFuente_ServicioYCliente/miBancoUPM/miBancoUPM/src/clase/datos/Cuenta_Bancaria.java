package clase.datos;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;

@XmlRootElement(name = "cuenta_bancaria")
public class Cuenta_Bancaria {
	//ATRIBUTOS
	private int id;
	private String nombreDuenio;
    private String apellidoDuenio;
    private int id_cliente;
	private int saldo;
	//private ArrayList<Transferencia> transferencias;
	//private ArrayList<Retirada_Efectivo> retiradas;
	
	//CONSTRUCTOR
	public Cuenta_Bancaria(int id_cliente,String nombreDuenio, String apellidoDuenio, int saldo) { //como saber qué usuario crea la cuenta
		this.id_cliente=id_cliente;
		this.nombreDuenio=nombreDuenio;
		this.apellidoDuenio=apellidoDuenio;
		this.saldo=saldo;
		//this.transferencias=new ArrayList<Transferencia>();
		//this.retiradas=new ArrayList<Retirada_Efectivo>();
	}
	
	public Cuenta_Bancaria() {
		
	}
	
	public int getSaldo() {
		return this.saldo;
	}
	
	public void setSaldo(int saldo) {
		this.saldo=saldo;
	}
	
	public String getNombreDuenio() {
		return this.nombreDuenio;
	}
	
	public void setNombreDuenio(String nombreDuenio) {
		this.nombreDuenio=nombreDuenio;
	}
	
	public String getApellidoDuenio() {
		return this.apellidoDuenio;
	}
	
	public void setApellidoDuenio(String apellidoDuenio) {
		this.apellidoDuenio=apellidoDuenio;
	}
	
	public int getIdCliente() {
		return this.id_cliente;
	}
	
	public void setIdCliente(int id_cliente) {
		this.id_cliente=id_cliente;
	}
	
	@XmlAttribute(required=false)
	public int getId() {
		return this.id;
	}
	
	public void setId(int id) {
		this.id=id;
	}
	
	/*@XmlElementWrapper(name="retiradas_efectivo")
    @XmlElement(name="retirada_efectivo") 
    //@XmlTransient
	public ArrayList<Retirada_Efectivo> getRetiradas() {
		return this.retiradas;
	}
	
	public void setRetiradas(ArrayList<Retirada_Efectivo> retiradas) {
		this.retiradas=retiradas;
	}
	
	@XmlElementWrapper(name="transferencias")
    @XmlElement(name="transferencia") 
    //@XmlTransient
	public ArrayList<Transferencia> getTransferencias() {
		return this.transferencias;
	}
	
	public void setTransferencias(ArrayList<Transferencia> transferencias) {
		this.transferencias=transferencias;
	}
	
	public void añadirTransferencia(Transferencia Transferencia) {
    	this.transferencias.add(Transferencia);
    }
	
	public void añadirRetirada(Retirada_Efectivo Retirada_Efectivo) {
    	this.retiradas.add(Retirada_Efectivo);
    }
	*/
} 
