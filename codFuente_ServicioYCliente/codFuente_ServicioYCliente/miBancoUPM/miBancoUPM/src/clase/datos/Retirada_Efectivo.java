package clase.datos;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "retirada_efectivo")
public class Retirada_Efectivo {
	
//Atributos
private int id;
private int cuenta_id;
private String fecha;
private int saldoRetirado;

//Constructor
public Retirada_Efectivo(int cuenta,String fecha,int saldo) {
	this.cuenta_id=cuenta;
	this.fecha=fecha;
	this.saldoRetirado=saldo;
}

public Retirada_Efectivo() {
	
}
@XmlAttribute(required=false)
public int getId() {
	return this.id;
}

public void setId(int id) {
	this.id=id;
}

public int getCuenta() {
	return this.cuenta_id;
}

public void setCuenta(int cuenta) {
	this.cuenta_id=cuenta;
}

public String getFecha() {
	return this.fecha;
}

public void setFecha(String fecha) {
	this.fecha=fecha;
}

public int getSaldoRetirado() {
	return this.saldoRetirado;
}

public void setSaldoRetirado(int saldo) {
	this.saldoRetirado=saldo;
}


}
