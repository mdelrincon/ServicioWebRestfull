package clase.datos;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "transferencias")
public class Transferencias {
	private ArrayList<Link> transferencias;

	public Transferencias() {
		this.transferencias = new ArrayList<Link>();
	}

	@XmlElement(name="transferencia")
	public ArrayList<Link> getTransferencias() {
		return transferencias;
	}

	public void setTransferencias(ArrayList<Link> transferencias) {
		this.transferencias = transferencias;
	}
}
