package clase.datos;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "clientes")
public class Clientes {
	private ArrayList<LinkSaldo> clientes;

	public Clientes() {
		this.clientes = new ArrayList<LinkSaldo>();
	}

	@XmlElement(name="cliente")
	public ArrayList<LinkSaldo> getClientes() {
		return clientes;
	}

	public void setClientes(ArrayList<LinkSaldo> clientes) {
		this.clientes = clientes;
	}
	
	public void remove(int indice) {
		this.clientes.remove(indice);
	}
}
