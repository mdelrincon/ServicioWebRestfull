package clase.datos;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "fecha")
public class Fecha {
	
private int dia;
private int mes;
private int ano;

public Fecha(int dia,int mes,int ano) {
	this.dia=dia;
	this.mes=mes;
	this.ano=ano;
	}

public int getDia() {
	return this.dia;
	}

public int getMes() {
	return this.mes;
}

public int getAno() {
	return this.ano;
}

}
