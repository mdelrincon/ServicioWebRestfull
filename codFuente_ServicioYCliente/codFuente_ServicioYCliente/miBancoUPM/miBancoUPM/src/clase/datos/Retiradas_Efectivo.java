package clase.datos;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "retiradas_efectivo")
public class Retiradas_Efectivo {
	private ArrayList<Link> retiradas_efectivo;

	public Retiradas_Efectivo() {
		this.retiradas_efectivo = new ArrayList<Link>();
	}

	@XmlElement(name="retirada_efectivo")
	public ArrayList<Link> getRetiradas_Efectivo() {
		return retiradas_efectivo;
	}

	public void setRetiradas_efectivo(ArrayList<Link> retiradas_efectivo) {
		this.retiradas_efectivo = retiradas_efectivo;
	}
}